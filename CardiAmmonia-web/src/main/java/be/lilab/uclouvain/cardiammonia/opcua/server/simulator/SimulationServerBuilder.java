package be.lilab.uclouvain.cardiammonia.opcua.server.simulator;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.lilab.uclouvain.cardiammonia.application.production.ProductionLog;
import be.lilab.uclouvain.cardiammonia.opcua.client.CommunicationClient;
import be.lilab.uclouvain.cardiammonia.opcua.client.CommunicationClientFactory;
import be.lilab.uclouvain.cardiammonia.opcua.client.impl.DefaultCommunicationClient;
import be.lilab.uclouvain.cardiammonia.opcua.server.Constants;
import be.lilab.uclouvain.cardiammonia.opcua.server.GenericMapUaStruct;
import be.lilab.uclouvain.cardiammonia.opcua.server.MethodSubscriber;
import be.lilab.uclouvain.cardiammonia.opcua.server.OPCUANamespace;
import be.lilab.uclouvain.cardiammonia.opcua.server.ServerBuilder;
import be.lilab.uclouvain.cardiammonia.opcua.server.ServerVariablesBuilder;
import be.lilab.uclouvain.cardiammonia.opcua.server.ServerVariablesBuilder.Variable;

public class SimulationServerBuilder {
	private static Logger logger = LoggerFactory.getLogger(SimulationServerBuilder.class);
	public static long SIMULATION_SPEED = 1;
	public static int httpsPort = 8443;

	public static OpcUaServer buildCycloneServer() throws Exception {
		OpcUaServer server;
		final String namespaceURI = Constants.CYCLONE_NAMESPACE_URI;
		final String rootNodeUrl = Constants.CYCLONE_ROOT_URL;
		final int serverTCPPort = Constants.CYCLONE_TCP_PORT;
		//Map<String, ServerVariablesBuilder.Variable> variables = ServerVariablesBuilder.CYCLONE_VARIABLES;

		server = ServerBuilder.get(namespaceURI, "/milo", serverTCPPort, httpsPort).build(); //new CycloneServer();
		OPCUANamespace cycloneNamespace = new OPCUANamespace(server, namespaceURI, rootNodeUrl, Constants.CYCLONE_VARIABLES);
		cycloneNamespace.startup();
		server.startup().get();

		CommunicationClient methodSubscriptionClient = CommunicationClientFactory.get().setOpcUaProtocol().setServerUrl("127.0.0.1:"+serverTCPPort).setServerPath("/milo").setRootNodeUrl(rootNodeUrl).build();
		methodSubscriptionClient.connect();
		ServerVariablesBuilder.MapVariable activeTargetMap =  (ServerVariablesBuilder.MapVariable)Constants.CYCLONE_VARIABLES.get("ActiveTarget");
		//Register map types after connecting
		methodSubscriptionClient.registerMapType(activeTargetMap.getValue(), namespaceURI, "ActiveTargetType");

		//The start method. Not documented by IBA. Added by UCL, Iyadk
		MethodSubscriber StartCyclone = new MethodSubscriber(methodSubscriptionClient,"Start") {
			@Override
			public void invoke(String[] inputValues) {
				try {
					if (inputValues[0].equals("1")) {//This is a start operaton
						this.getCommunicationClient().writeParameter("CycStt", Constants.CYC_STATE_UNDEFINED);
						Thread.sleep(3000*60/SIMULATION_SPEED);//Wait 3 seconds
						this.getCommunicationClient().writeParameter("CycStt", Constants.CYC_STATE_TOTALOFF);
						Thread.sleep(3000*60/SIMULATION_SPEED);//Wait 3 seconds
						this.getCommunicationClient().writeParameter("CycStt", Constants.CYC_STATE_VACCUM);
						Thread.sleep(3000*60/SIMULATION_SPEED);//Wait 3 seconds
						this.getCommunicationClient().writeParameter("CycStt", Constants.CYC_STATE_IDLE);
						Thread.sleep(3000*60/SIMULATION_SPEED);//Wait 3 seconds
						
						logger.info("Starting the cyclone server has completed");
					}
					else {//This is a shutdown operation
						this.getCommunicationClient().writeParameter("CycStt", Constants.CYC_STATE_SHUTTINGDOWN);
						logger.info("Shutting down the cyclone server.. By design, this will take forever :-)");
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};		
		//Create the method subscribers
		MethodSubscriber SubBchRq = new MethodSubscriber(methodSubscriptionClient,"SubBchRq") {
			@Override
			public void invoke(String[] inputValues) {
				try {
					long timeMillis = 0;
					this.getCommunicationClient().writeParameter("AcSubBchId", inputValues[0]);//The first parameter to the method contains the SubBatchId
					//The production can be paused during the beaming phase
					while (timeMillis<3000*60) {
						if (this.getCommunicationClient().readUIntParameter("CycStt")==Constants.CYC_STATE_IDLE && this.getCommunicationClient().readStringParameter("AcSubBchId").equals("0"))
							return;
						while(this.getCommunicationClient().readUIntParameter("CycStt")==Constants.CYC_STATE_PAUSED)
							Thread.sleep(1000/SIMULATION_SPEED);
						
						this.getCommunicationClient().writeParameter("CycStt", Constants.CYC_STATE_PREPARING);

						this.getCommunicationClient().writeParameter("CpuSs", uint(1));
						this.getCommunicationClient().writeParameter("CpuBtTi", DateTime.now());
						this.getCommunicationClient().writeParameter("FrwVer", "Firmwere version v0.1");
						this.getCommunicationClient().writeParameter("SwVer", "Uclouvain-v101");
						Thread.sleep(200);
						timeMillis+=200*SIMULATION_SPEED;
					}
					int i=0;
					timeMillis = 0;
					long stepCount = 3000*60/(SIMULATION_SPEED*200);
					while (timeMillis<3000*60) {
						if (this.getCommunicationClient().readUIntParameter("CycStt")==Constants.CYC_STATE_IDLE && this.getCommunicationClient().readStringParameter("AcSubBchId").equals("0"))
							return;
						while(this.getCommunicationClient().readUIntParameter("CycStt")==Constants.CYC_STATE_PAUSED)
							Thread.sleep(1000/SIMULATION_SPEED);
						
						this.getCommunicationClient().writeParameter("CycStt", Constants.CYC_STATE_BEAMING);
						
						GenericMapUaStruct struct = this.getCommunicationClient().readMapParameter("ActiveTarget");
						Map<String, Object> structMap = struct.getValues();
						//if (struct==null)
						//	struct = new HashMap<>();
						structMap.put("TNb", uint(1)); //Target number
						structMap.put("Stt", uint(1)); //State
						structMap.put("SttDsc", "initial"); //State description
						structMap.put("LdVol", 10.0d);//Loaded Volume
						structMap.put("BOnTi", DateTime.now());//Beam on time
						structMap.put("BOffTi", DateTime.now());//Beam off time
						structMap.put("TRqdCu", 100d*i/stepCount);//Requested current. I suggest that the max value is 100A
						structMap.put("TPMinRchd", false);//Pressure min reached
						structMap.put("TIntdCu", 30d*i/stepCount);//Integrated current. I suggest that the max value is 30A
						structMap.put("RqdTActRdy", DateTime.now());//Activity ready for unload

						structMap.put("TActActy", 35d*i/stepCount);//Actual activity. I suggest that the activiy max value is 30mC
						
						this.getCommunicationClient().writeParameter("ActiveTarget", struct);

						Thread.sleep(200);
						timeMillis+=200*SIMULATION_SPEED;
						i++;
						
					}
					GenericMapUaStruct struct = this.getCommunicationClient().readMapParameter("ActiveTarget");
					Map<String, Object> structMap = struct.getValues();
					structMap.put("TActActy", 35d);//Actual activity. I suggest that the activiy max value is 30mC
					this.getCommunicationClient().writeParameter("ActiveTarget", struct);

					this.getCommunicationClient().writeParameter("CycStt", Constants.CYC_STATE_ACTIVITYREADY);
					Thread.sleep(3000*60/SIMULATION_SPEED);

					//re-initialize
					this.getCommunicationClient().writeParameter("CpuSs", uint(0));
					this.getCommunicationClient().writeParameter("CpuBtTi", DateTime.now());
					
					logger.info("Production for sub batch ["+this.getCommunicationClient().readStringParameter("AcSubBchId")+" ] has completed");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		MethodSubscriber SubBchUnld = new MethodSubscriber(methodSubscriptionClient,"SubBchUnld") {
			@Override
			public void invoke(String[] inputValues) {
				try {
					logger.debug("Call SubBchUnld with sub batch id param equals to: "+ inputValues[0]);
					if (!inputValues[0].equalsIgnoreCase(this.getCommunicationClient().readStringParameter("AcSubBchId")))
						return;
					logger.debug("Call SubBchUnld with unload target param equals to: "+ inputValues[1]);
					boolean unloadTarget = Boolean.parseBoolean(inputValues[1]);
					GenericMapUaStruct struct = this.getCommunicationClient().readMapParameter("ActiveTarget");
					Map<String, Object> structMap = struct.getValues();
					UInteger state = uint(0);
					logger.debug("Call SubBchUnld with unload target param equals to: "+ unloadTarget);
					if (unloadTarget) {
						structMap.put("UnLdStaTi", DateTime.now());//Unload start time
						structMap.put("UnLdAct", 35d);//Unloaded activity ??
						state = Constants.CYC_STATE_UNLOADING;
					}
					else {
						
						structMap.put("LaLdTTi", DateTime.now());//Loaded time
						state = Constants.CYC_STATE_LOADINGNEXT;
						Thread.sleep(3000*60/SIMULATION_SPEED);
						this.getCommunicationClient().writeParameter("CycStt", state);
						this.getCommunicationClient().writeParameter("ActiveTarget", struct);
						this.getCommunicationClient().writeParameter("AcSubBchId", "0");//The AcSubBchId becomes null at the end of the production
						state = Constants.CYC_STATE_IDLE;
					}
					Thread.sleep(3000*60/SIMULATION_SPEED);
					this.getCommunicationClient().writeParameter("CycStt", state);
					this.getCommunicationClient().writeParameter("ActiveTarget", struct);

					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		MethodSubscriber SubBchPause = new MethodSubscriber(methodSubscriptionClient,"SubBchPause") {
			@Override
			public void invoke(String[] inputValues) {
				try {
					if (!inputValues[0].equalsIgnoreCase(this.getCommunicationClient().readStringParameter("AcSubBchId")))
						return;
					if (this.getCommunicationClient().readUIntParameter("CycStt")==Constants.CYC_STATE_BEAMING
					    || this.getCommunicationClient().readUIntParameter("CycStt")==Constants.CYC_STATE_PREPARING) {
						UInteger state = Constants.CYC_STATE_PAUSED;
						this.getCommunicationClient().writeParameter("state", state);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		MethodSubscriber SubBchResume = new MethodSubscriber(methodSubscriptionClient,"SubBchResume") {
			@Override
			public void invoke(String[] inputValues) {
				try {
					if (!inputValues[0].equalsIgnoreCase(this.getCommunicationClient().readStringParameter("AcSubBchId")))
						return;
					if (this.getCommunicationClient().readUIntParameter("CycStt")==Constants.CYC_STATE_PAUSED) {
						UInteger state = Constants.CYC_STATE_BEAMING;
						this.getCommunicationClient().writeParameter("state", state);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		MethodSubscriber SubBchCancel = new MethodSubscriber(methodSubscriptionClient,"SubBchCancel") {
			@Override
			public void invoke(String[] inputValues) {
				try {
					if (!inputValues[0].equalsIgnoreCase(this.getCommunicationClient().readStringParameter("AcSubBchId")))
						return;
					UInteger state = Constants.CYC_STATE_IDLE;
					this.getCommunicationClient().writeParameter("state", state);
					this.getCommunicationClient().writeParameter("AcSubBchId", "0");
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		return server;
	}
	
	public static OpcUaServer buildDispensingServer() throws Exception {
		
		OpcUaServer server;
		final String namespaceURI = Constants.DISPENSING_NAMESPACE_URI;
		final String rootNodeUrl = Constants.DISPENSING_ROOT_URL;
		final int serverTCPPort = Constants.DISPENSING_TCP_PORT;

		server = ServerBuilder.get(namespaceURI, "/milo", serverTCPPort, httpsPort+1).build(); 
		OPCUANamespace dispensingNamespace = new OPCUANamespace(server, namespaceURI, rootNodeUrl, Constants.DISPENSING_VARIABLES);
		dispensingNamespace.startup();
		server.startup().get();

		CommunicationClient methodSubscriptionClient = CommunicationClientFactory.get().setOpcUaProtocol().setServerUrl("127.0.0.1:"+serverTCPPort).setServerPath("/milo").setRootNodeUrl(rootNodeUrl).build();
		methodSubscriptionClient.connect();
		
		//The start method. Not documented by IBA. Added by UCL, Iyadk
		MethodSubscriber StartDispensing = new MethodSubscriber(methodSubscriptionClient,"Start") {
			@Override
			public void invoke(String[] inputValues) {
				try {
					if (inputValues[0].equals("1")) {//This is a start operation
						this.getCommunicationClient().writeParameter("DspStt", Constants.DISP_STATE_UNDEFINED);
						Thread.sleep(3000*60/SIMULATION_SPEED);//Wait 3 seconds
						this.getCommunicationClient().writeParameter("DspStt", Constants.DISP_STATE_IDLE);
						Thread.sleep(3000*60/SIMULATION_SPEED);//Wait 3 seconds
						logger.info("The dispensing server is started");
					}
					else {//This is a shutdown operation
						this.getCommunicationClient().writeParameter("DspStt", Constants.DISP_STATE_UNDEFINED);
						logger.info("The dispensing server has stopped");
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};				
		//Create the method subscribers
		MethodSubscriber SubBchRq = new MethodSubscriber(methodSubscriptionClient,"SubBchRq") {
			@Override
			public void invoke(String[] inputValues) {
				try {
					this.getCommunicationClient().writeParameter("AcSubBchId", inputValues[0]);//The first parameter to the method contains the SubBatchId
					//The second param is the Route: Send to 0: waste, 1: Syringe, 2: QC 
					this.getCommunicationClient().writeParameter("QcStt", Constants.DISP_STATE_BUSY);
					Thread.sleep(5000*60/SIMULATION_SPEED);//Wait 5 seconds
					String route = inputValues[1];
					String routeStr = "";
					if (route.equals("0")) {
						routeStr = "the waste";
					}
					if (route.equals("1")) {
						routeStr = "the syringe";
						int nbSyringe = this.getCommunicationClient().readIntParameter("NbSyringeReady");
						this.getCommunicationClient().writeParameter("NbSyringeReady", nbSyringe+1);//Work completed
					}
					if (route.equals("2")) {
						routeStr = "the qc";
					}
					logger.info("The sub batch ["+inputValues[0]+" is directed towards "+ routeStr);

					this.getCommunicationClient().writeParameter("AcSubBchId", "0");//Work completed
					this.getCommunicationClient().writeParameter("QcStt", Constants.DISP_STATE_IDLE);
										
					logger.info("Dispensing for sub batch ["+inputValues[0]+" ] has completed");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		MethodSubscriber SubBchCancel = new MethodSubscriber(methodSubscriptionClient,"SubBchCancel") {
			@Override
			public void invoke(String[] inputValues) {
				try {
					this.getCommunicationClient().writeParameter("AcSubBchId", inputValues[0]);//The first parameter to the method contains the SubBatchId
					//The second param is the Route: Send to 0: waste, 1: Syringe, 2: QC 
					Thread.sleep(3000*60/SIMULATION_SPEED);//Wait 3 seconds

					this.getCommunicationClient().writeParameter("AcSubBchId", "0");//Work completed
					this.getCommunicationClient().writeParameter("QcStt", Constants.DISP_STATE_IDLE);
										
					logger.info("Dispensing for sub batch ["+inputValues[0]+" ] is sent to waste");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};		
		
		return server;
	}
		
	public static OpcUaServer buildDoseCalibratorServer() throws Exception {
		OpcUaServer server;
		final String namespaceURI = "urn:eclipse:milo:dose-kardio";
		final String rootNodeUrl = Constants.DOSE_CALIBRATOR_ROOT_URL;
		final int serverTCPPort = Constants.DOSE_CALIBRATOR_TCP_PORT;
		Map<String, ServerVariablesBuilder.Variable> variables = ServerVariablesBuilder.get()
				.declareUIntVariable("state", 0)
				.declareMethod("SubBchStart")
					.declareStringInput("SubBchId", "0")//Sub-batch ID
					.build()
				.collect();

		server = ServerBuilder.get(namespaceURI, "/milo", serverTCPPort, 8444).build(); //new CycloneServer();
		OPCUANamespace cycloneNamespace = new OPCUANamespace(server, namespaceURI, rootNodeUrl, variables);
		cycloneNamespace.startup();
		return server;
	}
	
	public static OpcUaServer buildQCServer() throws Exception {
		OpcUaServer server;
		final String namespaceURI = Constants.QC_NAMESPACE_URI;
		final String rootNodeUrl = Constants.QC_ROOT_URL;
		final int serverTCPPort = Constants.QC_TCP_PORT;

		server = ServerBuilder.get(namespaceURI, "/milo", serverTCPPort, httpsPort+2).build(); 
		OPCUANamespace qcNamespace = new OPCUANamespace(server, namespaceURI, rootNodeUrl, Constants.QC_VARIABLES);
		qcNamespace.startup();
		server.startup().get();

		CommunicationClient methodSubscriptionClient = CommunicationClientFactory.get().setOpcUaProtocol().setServerUrl("127.0.0.1:"+serverTCPPort).setServerPath("/milo").setRootNodeUrl(rootNodeUrl).build();
		methodSubscriptionClient.connect();
		
		
		//The start method. Not documented by IBA. Added by UCL, Iyadk
		MethodSubscriber StartQC = new MethodSubscriber(methodSubscriptionClient,"Start") {
			@Override
			public void invoke(String[] inputValues) {
				try {
					if (inputValues[0].equals("1")) {//This is a start operaton
						this.getCommunicationClient().writeParameter("QcStt", Constants.QC_STATE_OFF);
						Thread.sleep(3000*60/SIMULATION_SPEED);//Wait 3 seconds
						this.getCommunicationClient().writeParameter("QcStt", Constants.QC_STATE_SW_ON);
						
						logger.info("The QC server is started");
					}
					else {//This is a shutdown operation
						this.getCommunicationClient().writeParameter("CycStt", Constants.CYC_STATE_SHUTTINGDOWN);
						logger.info("The QC server has stopped");
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};	
		
		//Create the method subscribers
		MethodSubscriber SubBchRq = new MethodSubscriber(methodSubscriptionClient,"QcSubBchRq") {
			@Override
			public void invoke(String[] inputValues) {
				try {
					this.getCommunicationClient().writeParameter("AcSubBchId", inputValues[0]);//The first parameter to the method contains the SubBatchId
					this.getCommunicationClient().writeParameter("QcStt", Constants.QC_STATE_BATCH_RECEIVED);
					Thread.sleep(3000*60/SIMULATION_SPEED);//Wait 3 seconds

					this.getCommunicationClient().writeParameter("QcStt", Constants.QC_STATE_REQUEST_RECEIVED);
					Thread.sleep(3000*60/SIMULATION_SPEED);//Wait 3 seconds
					this.getCommunicationClient().writeParameter("QcStt", Constants.QC_STATE_PREPARING);
					Thread.sleep(3000*60/SIMULATION_SPEED);//Wait 3 seconds
					this.getCommunicationClient().writeParameter("QcStt", Constants.QC_STATE_READY_WAITING_SAMPLE);
					Thread.sleep(3000*60/SIMULATION_SPEED);//Wait 3 seconds
					this.getCommunicationClient().writeParameter("QcStt", Constants.QC_STATE_RUNNING);
					Thread.sleep(3000*60/SIMULATION_SPEED);//Wait 3 seconds
					this.getCommunicationClient().writeParameter("QcStt", Constants.QC_STATE_BATCH_RECEIVED);
					Thread.sleep(3000*60/SIMULATION_SPEED);//Wait 3 seconds
					this.getCommunicationClient().writeParameter("QcStt", Constants.QC_STATE_PARTIAL_RELEASE);
					Thread.sleep(3000*60/SIMULATION_SPEED);//Wait 3 seconds

					this.getCommunicationClient().writeParameter("AcSubBchId", "0");//Work completed
										
					logger.info("QC for sub batch ["+inputValues[0]+" ] has completed");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		MethodSubscriber QcSubBchSsRq = new MethodSubscriber(methodSubscriptionClient,"QcSubBchSsRq") {//Get the status of a batch in the db
			@Override
			public void invoke(String[] inputValues) {
				try {
					String SubBatchId = inputValues[0];
					//Our simulator can not handle output parameters. To solve this caveat, we create a variable in the server to hold the output of the call to the method.
					//I will not do it at this time, but it can be done in the future.
										
					logger.info("QcSubBchSsRq for "+inputValues[0]+" is called. Not implemented yet.");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		return server;		
		
	}
}
