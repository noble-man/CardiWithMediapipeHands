package be.lilab.uclouvain.cardiammonia.opcua.client;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

//import static org.hamcrest.CoreMatchers.instanceOf;
//import static org.hamcrest.CoreMatchers.is;

//import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.junit.runner.RunWith;

import be.lilab.uclouvain.cardiammonia.opcua.client.impl.DefaultCommunicationClient;
import be.lilab.uclouvain.cardiammonia.opcua.server.GenericMapUaStruct;
import be.lilab.uclouvain.cardiammonia.opcua.server.OPCUANamespace;
import be.lilab.uclouvain.cardiammonia.opcua.server.ServerBuilder;
import be.lilab.uclouvain.cardiammonia.opcua.server.ServerVariablesBuilder;
import be.lilab.uclouvain.cardiammonia.opcua.server.ServerVariablesBuilder.Variable;

//import be.lilab.uclouvain.cardiammonia.learning.course.CourseController;

//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@ExtendWith(SpringExtension.class)
//@RunWith(SpringExtension.class)
class CycloneCommunicationClientTest {
	
	private static OpcUaServer server;
	private static CommunicationClient client;

	final static String namespaceURI = "urn:eclipse:milo:cyclo-kardio";

	@BeforeAll
	static void InitializeContext() throws Exception {
		final String rootNodeUrl = "CycloneKardio/General/";
		final int serverTCPPort = 12686;
		//Cyclone server variables are already implemented in : ServerVariablesBuilder.CYCLONE_VARIABLES
		Map<String, ServerVariablesBuilder.Variable> variables = ServerVariablesBuilder.get()
				.declareLongVariable("CPUStatus", 64l)
				.declareDateTimeVariable("CPUBootTime", DateTime.now())
				.declareStringVariable("FirmwareVersion", "Firmwere version v0.1")
				.declareStringVariable("SoftwareVersion", "Uclouvain-v101")
				.declareIntVariable("CycloneState", 32)
				.declareStringVariable("CycloneFistInterlock", "First interlock")
				.declareLongVariable("NumberOfActiveAlamrs", 6l)
				.declareBooleanVariable("CycloneStandByMode", false)
				.declareBooleanVariable("CycloneStartUp", false)
				.declareMapVariable("ActiveTarget", "ActiveTargetType")
					.declareUIntVariable("TNb", 0) //Target number
					.declareUIntVariable("Stt", 0) //State
					.declareStringVariable("SttDsc", "initial") //State description
					.declareDoubleVariable("LdVol", 10.0d)//Loaded Volume
					.declareDateTimeVariable("BOnTi", DateTime.now())//Beam on time
					.declareDateTimeVariable("BOffTi", DateTime.now())//Beam off time
					.declareDoubleVariable("TRqdCu", 0d)//Requested current
					.declareBooleanVariable("TPMinRchd", false)//Pressure min reached
					.declareDoubleVariable("TIntdCu", 0d)//Integrated current
					.declareDoubleVariable("TActActy", 0d)//Actual activity
					.declareDateTimeVariable("RqdTActRdy", DateTime.now())//Activity ready for unload
					.declareDateTimeVariable("LaLdTTi", DateTime.now())//Loaded time
					.declareDateTimeVariable("UnLdStaTi", DateTime.now())//Unload start time
					.declareDoubleVariable("UnLdAct", 0d)//Unloaded activity ??
				.build()
				.collect();
		server = ServerBuilder.get(namespaceURI, "/milo", serverTCPPort, 8443).build(); //new CycloneServer();
		OPCUANamespace cycloneNamespace = new OPCUANamespace(server, namespaceURI, rootNodeUrl, variables);
		cycloneNamespace.startup();
		
		server.startup().get();
		//Thread.sleep(5000);//Wait for the server to run.
		client = CommunicationClientFactory.get().setOpcUaProtocol().setServerUrl("127.0.0.1:"+serverTCPPort).setServerPath("/milo").setRootNodeUrl(rootNodeUrl).build();
		ServerVariablesBuilder.MapVariable activeTargetMap =  (ServerVariablesBuilder.MapVariable)variables.get("ActiveTarget");
		Map<String, Variable> x = activeTargetMap.getValue();
		client.connect();
		//Register map types after connecting
		client.registerMapType(activeTargetMap.getValue(), namespaceURI, "ActiveTargetType");
	}
	
	@Test
	void WriteMap_ReturnsUpdatedValues() throws Exception {
		GenericMapUaStruct struct = client.readMapParameter("ActiveTarget"); //The map is there for you. Read it and them modfy the field you want
		struct.getValues().put("TNb",uint(10));
		final boolean r = client.writeParameter("ActiveTarget", struct);
		assertEquals(true, r);
		
		struct = client.readMapParameter("ActiveTarget"); 
		assertEquals(uint(10), struct.getValues().get("TNb"));
	}
	@Test
	void WriteParameter_ReturnsTrueForValidParameter() throws Exception {
		String param = "CycloneStandByMode";
		final boolean r = client.writeParameter(param, true);
		assertEquals(true, r);
		
		final boolean s = client.readBooleanParameter(param);
		assertEquals(true, s);
	}
	
	/*@Test
	void ReadIntParameter_ReturnsValueOfParameter() throws InterruptedException, ExecutionException {
		final Integer result = client.readIntParameter("NumberOfActiveAlamrs");
		assertNull(result);	
	}*/
	@Test
	void ReadIntParameter_ReturnsNullForNonExistentParameter() throws InterruptedException, ExecutionException {
		final Object result = client.readParameter("NonExistentParameter");
		assertNull(result);
		//assertEquals("Hello, World", result);	
	}
	
	@Test
	void ReadBooleanParameter_ReturnsValueOfParameter() throws InterruptedException, ExecutionException {
		final Boolean result = client.readBooleanParameter("NumberOfActiveAlamrs");
		assertNull(result);	
	}
	@Test
	void ReadBooleanParameter_ReturnsNullForNonExistentParameter() throws InterruptedException, ExecutionException {
		final Object result = client.readParameter("NonExistentParameter");
		assertNull(result);
		//assertEquals("Hello, World", result);
		
	}
	

	@Test
	void ReadStringParameter_ReturnsValueOfParameter() throws InterruptedException, ExecutionException {
		final Object result = client.readParameter("FirmwareVersion");
		assertNotNull(result);
		//assertEquals("Hello, World", result);	
	}
	@Test
	void ReadStringParameter_ReturnsNullForNonExistentParameter() throws InterruptedException, ExecutionException {
		final Object result = client.readParameter("NonExistentParameter");
		assertNull(result);
		//assertEquals("Hello, World", result);
	}

	/*@Test
	void testReadBooleanParameter() {
		fail("Not yet implemented");
	}*/

	/*@Test
	void testReadMapParameter() {
		fail("Not yet implemented");
	}*/
	/*@Test
	void ReadMapParameter_ReturnsNullForNonExistentParameter() {
		fail("Not yet implemented");
	}*/

	@Test
	void ReadParameter_ReturnsValueOfParameter() throws InterruptedException, ExecutionException {
		final Object result = client.readParameter("CPUStatus");
		assertNotNull(result);
		//assertEquals("Hello, World", result);
		
	}
	
	@Test
	void ReadParameter_ReturnsNullForNonExistentParameter() throws InterruptedException, ExecutionException {
		final Object result = client.readParameter("NonExistentParameter");
		assertNull(result);
		//assertEquals("Hello, World", result);
		
	}

	/*@Test
	void testReadActiveTargetStruct() {
		fail("Not yet implemented");
	}*/

/*	@Test
	void testExecuteAction() {
	
		assertThrows(UnsupportedOperationException.class, () -> {client.ExecuteAction();} );
	}
	*/
	
	@AfterAll
	static void shutDownServer() throws InterruptedException, ExecutionException {
		
		server.shutdown();
	}

}
