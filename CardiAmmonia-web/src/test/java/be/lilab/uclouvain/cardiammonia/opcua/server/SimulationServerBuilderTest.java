package be.lilab.uclouvain.cardiammonia.opcua.server;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.junit.jupiter.api.Test;

import be.lilab.uclouvain.cardiammonia.opcua.client.CommunicationClient;
import be.lilab.uclouvain.cardiammonia.opcua.client.CommunicationClientFactory;
import be.lilab.uclouvain.cardiammonia.opcua.server.simulator.SimulationServerBuilder;

public class SimulationServerBuilderTest {
	@Test
	void testCycloneServer() throws Exception {
		 
		SimulationServerBuilder.SIMULATION_SPEED = 60;//Speed up the process to finish in 10 seconds.
		OpcUaServer cycloneServer = SimulationServerBuilder.buildCycloneServer();
		CommunicationClient client = CommunicationClientFactory.get()
															.setOpcUaProtocol()
															.setServerUrl("127.0.0.1:"+Constants.CYCLONE_TCP_PORT)
															.setServerPath("/milo")
															.setRootNodeUrl(Constants.CYCLONE_ROOT_URL).build();
		client.connect();
				
/*				ServerVariablesBuilder.get()
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
					.build().collect();
		*/
		
		ServerVariablesBuilder.MapVariable activeTargetStructVar =  (ServerVariablesBuilder.MapVariable)Constants.CYCLONE_VARIABLES.get("ActiveTarget");
		//Register map types after connecting
		client.registerMapType(activeTargetStructVar.getValue(), Constants.CYCLONE_NAMESPACE_URI, "ActiveTargetType");

		//Start the server
		client.callMethod(Constants.CYCLONE_ROOT_URL,"Start","1");
		Thread.sleep(9000);// The server requires 9 seconds to start
		//UInteger state= SimulationServerBuilder.CYC_STATE_IDLE;
		client.callMethod(Constants.CYCLONE_ROOT_URL,"SubBchRq", "1111", "1","30"); //SubBchID, RecId, ActRqd
		Thread.sleep(5000);//Production finishes in 9 seconds. The first 6 seconds for preparing and beaming
		assertEquals(Constants.CYC_STATE_BEAMING, client.readUIntParameter("CycStt"));
		Thread.sleep(4000);//Production finishes in 9 seconds. The last 3 seconds are for the activity ready
		assertEquals(Constants.CYC_STATE_ACTIVITYREADY, client.readUIntParameter("CycStt"));

		client.callMethod(Constants.CYCLONE_ROOT_URL, "SubBchUnld", "1111", true); //SubBchID, UnldRq, output: Unload stop time
		Thread.sleep(3000);//=unload target finishes in 3 seconds
		assertEquals(Constants.CYC_STATE_UNLOADING, client.readUIntParameter("CycStt"));

		client.callMethod(Constants.CYCLONE_ROOT_URL, "SubBchUnld", "1111", false); //SubBchID, UnldRq, output: Unload stop time
		Thread.sleep(2000);//=load next target finishes in 3 seconds a
		assertEquals(Constants.CYC_STATE_LOADINGNEXT, client.readUIntParameter("CycStt"));
		
		GenericMapUaStruct struct = client.readMapParameter("ActiveTarget");
		assertEquals(35d, (Double)struct.getValues().get("TActActy"));

		Thread.sleep(4000);
		//Shutdown the server
		client.callMethod(Constants.CYCLONE_ROOT_URL,"Start","0");
		Thread.sleep(2000);//method subscriptions require max 1000 ms to ensure handling
		assertEquals(Constants.CYC_STATE_SHUTTINGDOWN, client.readUIntParameter("CycStt"));

	}
	
	@Test
	public void testSubscriptionClientStartsAProduction() throws Exception {
		SimulationServerBuilder.SIMULATION_SPEED = 60;//Speed up the process to finish in 10 seconds.
		OpcUaServer cycloneServer = SimulationServerBuilder.buildCycloneServer();
		CommunicationClient client = CommunicationClientFactory.get()
															.setOpcUaProtocol()
															.setServerUrl("127.0.0.1:"+Constants.CYCLONE_TCP_PORT)
															.setServerPath("/milo")
															.setRootNodeUrl(Constants.CYCLONE_ROOT_URL).build();
		client.connect();
		ServerVariablesBuilder.MapVariable activeTargetStructVar =  (ServerVariablesBuilder.MapVariable)Constants.CYCLONE_VARIABLES.get("ActiveTarget");
		//Register map types after connecting
		client.registerMapType(activeTargetStructVar.getValue(), Constants.CYCLONE_NAMESPACE_URI, "ActiveTargetType");

		//Start the server
		client.callMethod(Constants.CYCLONE_ROOT_URL,"Start","1");
		
		//UInteger state= SimulationServerBuilder.CYC_STATE_IDLE;
		client.callMethod(Constants.CYCLONE_ROOT_URL,"SubBchRq", "1111", "1","30"); //SubBchID, RecId, ActRqd
		Thread.sleep(5000);//Production finishes in 9 seconds. The first 6 seconds for preparing and beaming

		//Shutdown the server
		Thread.sleep(2000);//method subscriptions require max 1000 ms to ensure handling
		client.callMethod(Constants.CYCLONE_ROOT_URL,"Start","0");

	}
}
