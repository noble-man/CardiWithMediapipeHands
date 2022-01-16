package be.lilab.uclouvain.cardiammonia.opcua.server;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.lilab.uclouvain.cardiammonia.opcua.client.CommunicationClient;
import be.lilab.uclouvain.cardiammonia.opcua.client.CommunicationClientFactory;
import be.lilab.uclouvain.cardiammonia.opcua.client.impl.DefaultCommunicationClient;


class OPCUANamespaceTest {
	private Logger logger = LoggerFactory.getLogger(getClass());

	private static OpcUaServer server;
	private static DefaultCommunicationClient client;

	@BeforeAll
	static void InitializeContext() throws Exception {
		final String namespaceURI = "urn:eclipse:milo:cyclo-kardio";
		final String rootNodeUrl = "CycloneKardio/General/";
		final int serverTCPPort = 12686;
		Map<String, ServerVariablesBuilder.Variable> variables = ServerVariablesBuilder.get()
				.declareUIntVariable("state", 0)
				.declareMethod("sqrt(x)")
					.declareStringInput("x", "2.0")
					.declareStringOutput("x_sqrt", "hello")
					.declareStringOutput("x_sqrt2", "hello2")
					.build()
				.collect();

		server = ServerBuilder.get(namespaceURI, "/milo", serverTCPPort, 8443).build(); //new CycloneServer();
		OPCUANamespace cycloneNamespace = new OPCUANamespace(server, namespaceURI, rootNodeUrl, variables);
		cycloneNamespace.startup();
		
		server.startup().get();
		CommunicationClient opcuaClient = CommunicationClientFactory.get().setOpcUaProtocol().setServerUrl("127.0.0.1:"+serverTCPPort).setServerPath("/milo").setRootNodeUrl(rootNodeUrl).build();

		client =  (DefaultCommunicationClient)opcuaClient;
		client.connect();
	}

	@Test
	void subscribeToAndCallAMethod() throws Exception {
	    
		final AtomicInteger count_sqrt_calls = new AtomicInteger(0);
		final String[] inputValuesReturned = {null};

		MethodSubscriber sqrt = new MethodSubscriber(client,"sqrt(x)") {
			
			@Override
			public void invoke(String[] inputValues) {
				try {
					UInteger state = uint(1);
					this.getCommunicationClient().writeParameter("state", state);
					count_sqrt_calls.incrementAndGet();
					inputValuesReturned[0] = inputValues[0];
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		
		Variant[] result = client.callMethod("CycloneKardio/General/", "sqrt(x)", "16.0");
		
		int waitingMillis = 0;
		while (count_sqrt_calls.get()==0 && waitingMillis<3000) {//Wait for the subscription, but not more than three seconds
			Thread.sleep(10);
			waitingMillis+=10;
		}
	
		assertEquals(1, count_sqrt_calls.get());//subscription succeeded
		assertEquals(uint(1), client.readUIntParameter("state"));//The subscription successfully modified the state variable on the server
		assertEquals(2, result.length);
		assertEquals("hello", result[0].getValue());
		assertEquals("hello2", result[1].getValue());
		assertEquals("16.0", inputValuesReturned[0]);
	}
}
