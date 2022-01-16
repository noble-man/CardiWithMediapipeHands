package be.lilab.uclouvain.cardiammonia.opcua.server;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

import java.util.Map;

import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;

public class Constants {

	public static final UInteger CYC_STATE_UNDEFINED = uint(0);
	public static final UInteger CYC_STATE_IDLE = uint(1);
	public static final UInteger CYC_STATE_TOTALOFF = uint(2);
	public static final UInteger CYC_STATE_VACCUM = uint(3);
	public static final UInteger CYC_STATE_PREPARING = uint(4);
	public static final UInteger CYC_STATE_BEAMING = uint(5);
	public static final UInteger CYC_STATE_ACTIVITYREADY = uint(6);
	public static final UInteger CYC_STATE_UNLOADING = uint(7);
	public static final UInteger CYC_STATE_LOADINGNEXT = uint(8);
	public static final UInteger CYC_STATE_PAUSED = uint(9);
	public static final UInteger CYC_STATE_SHUTTINGDOWN = uint(10);
	
	public static final UInteger DISP_STATE_UNDEFINED = uint(0);
	public static final UInteger DISP_STATE_IDLE = uint(1);
	public static final UInteger DISP_STATE_BUSY = uint(2);

	public static final UInteger QC_STATE_OFF = uint(0);
	public static final UInteger QC_STATE_SW_ON = uint(1);
	public static final UInteger QC_STATE_BATCH_RECEIVED = uint(2);
	public static final UInteger QC_STATE_REQUEST_RECEIVED = uint(3);
	public static final UInteger QC_STATE_PREPARING = uint(4);
	public static final UInteger QC_STATE_READY_WAITING_SAMPLE = uint(5);
	public static final UInteger QC_STATE_RUNNING = uint(6);
	public static final UInteger QC_STATE_PARTIAL_RELEASE= uint(7);
	public static final UInteger QC_STATE_ERROR = uint(8);
	
	public static final int CYCLONE_TCP_PORT = 12686;
	public static final int DISPENSING_TCP_PORT = 12687;
	public static final int DOSE_CALIBRATOR_TCP_PORT = 12688;
	public static final int QC_TCP_PORT = 12689;
	public static final String CYCLONE_ROOT_URL = "CycloneKardio/General/";
	public static final String DISPENSING_ROOT_URL = "Dispensing/General/";
	public static final String DOSE_CALIBRATOR_ROOT_URL = "DoseCalibrator/General/";
	public static final String QC_ROOT_URL = "QC/General/";
	public static final String CYCLONE_NAMESPACE_URI= "urn:eclipse:milo:cyclo-kardio";
	public static final String DISPENSING_NAMESPACE_URI= "urn:eclipse:milo:dispensing-kardio";
	public static final String QC_NAMESPACE_URI= "urn:eclipse:milo:qc-kardio";
	
	public static final Map<String, ServerVariablesBuilder.Variable> CYCLONE_VARIABLES = ServerVariablesBuilder.get()
			.declareIntVariable("UaSrvStt", 0) //OPC UA Server State
			.declareIntVariable("UaLfCn", 0) //OPC UA Life Counter
			
			.declareUIntVariable("CpuSs", 0) //CPUStatus
			.declareDateTimeVariable("CpuBtTi", DateTime.now()) //CPUBootTime
			.declareStringVariable("FrwVer", "Firmwere version v0.1") //FirmwareVersion
			.declareStringVariable("SwVer", "Uclouvain-v101") //SoftwareVersion
			.declareUIntVariable("CycStt", 0)//CycloneState
			.declareStringVariable("CycFiIk", "First interlock") //CycloneFistInterlock
			.declareLongVariable("NbAl", 6l) //NumberOfActiveAlamrs
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
			.declareMethod("SubBchRq")//Sub-batch request
				.declareStringInput("SubBchId", "0")//Sub-batch ID
				.declareStringInput("RecId", "0")//Recipe ID
				.declareStringInput("ActRqd", "0")//Activity requested (can be also fixed in the recipe)
				
				.declareStringOutput("Sub-batch ID", "0")
				.declareBooleanOutput("Request validated", true)
				.declareStringOutput("Request rejection reason", "request is not rejected")
				.build()
			.declareMethod("SubBchUnld")
				.declareStringInput("SubBchId", "0")//Sub-batch ID
				.declareBooleanInput("UnldRq", true)//Unload Request. True to unload, false to load
				.declareDateTimeOutput("Unload stop time", DateTime.now())
				.build()
			.declareMethod("SubBchPause")//Sub-batch pause (on hold)
				.declareStringInput("SubBchId", "0")//Sub-batch ID
				.build()
			.declareMethod("SubBchResume")//Sub-batch pause (on hold)
				.declareStringInput("SubBchId", "0")//Sub-batch ID
				.build()
			.declareMethod("SubBchCancel")
				.declareStringInput("SubBchId", "0")//Sub-batch ID
				.declareDateTimeOutput("Unload stop time", DateTime.now())
				.build()
			.declareMethod("Start")
				.declareStringInput("Operation", "1")//"1" to start, 0 to shutdown .
				.build()
			.declareBooleanVariable("CycloneStandByMode", false)
			.declareBooleanVariable("CycloneStartUp", false)
			
			.declareStringVariable("AcSubBchId", "0") //The active sub-batch id

			.collect();
	public static final Map<String, ServerVariablesBuilder.Variable> DISPENSING_VARIABLES = ServerVariablesBuilder.get()
			.declareIntVariable("UaSrvStt", 0) //OPC UA Server State
			.declareIntVariable("UaLfCn", 0) //OPC UA Life Counter
			
			.declareStringVariable("FrwVer", "Firmwere version v0.1") //FirmwareVersion
			.declareStringVariable("SwVer", "Uclouvain-v101") //SoftwareVersion
			.declareUIntVariable("DspStt", 0)//DispensingState
			.declareStringVariable("CycFiIk", "First interlock") //CycloneFistInterlock
			.declareMethod("SubBchRq")//Sub-batch request
				.declareStringInput("SubBchId", "0")//Sub-batch ID
				.declareStringInput("Route", "0")// Send to 0: waste, 1: Syringe, 2: QC 
				.build()
			.declareMethod("SubBchCancel")
				.declareStringInput("SubBchId", "0")//Sub-batch ID
				.build()
			.declareMethod("Start")
				.declareStringInput("Operation", "1")//"1" to start, 0 to shutdown .
				.build()		
			.declareIntVariable("NbSyringeReady", 0) //The number of active syringes
			.declareStringVariable("AcSubBchId", "0") //The active sub-batch id

			.collect();
	public static final Map<String, ServerVariablesBuilder.Variable> QC_VARIABLES = ServerVariablesBuilder.get()
			.declareIntVariable("UaSrvStt", 0) //OPC UA Server State
			.declareIntVariable("UaLfCn", 0) //OPC UA Life Counter
			.declareDateTimeVariable("CpuBtTi", DateTime.now())//Process boot time
			
			.declareBooleanVariable("SwRdy", false)//Pressure min reached
			.declareBooleanVariable("QcRdy", false)//Pressure min reached
			.declareStringVariable("SwVer", "Uclouvain-v101") //SoftwareVersion

			.declareUIntVariable("QcStt", 0)//DispensingState
			.declareStringVariable("QcFiIk", "First interlock") //QC FistInterlock
			.declareLongVariable("NbAl", 6l) //NumberOfActiveAlamrs
			
			
			.declareMethod("QcSubBchRq")//Sub-batch request
				.declareStringInput("SubBchId", "0")//Sub-batch ID
				.declareStringInput("PrdNa", "none")// Send to 0: waste, 1: Syringe, 2: QC 
				.declareDateTimeInput("PrdDat", DateTime.now())
				.build()
			.declareMethod("QcSubBchSsRq")//Sub-batch request for a status
				.declareStringInput("SubBchId", "0")//Sub-batch ID
				.declareStringOutput("QcSubBchSs", "QC sub-batch status answer")//Status of operator action request, signature, data, …
				.build()
 			.declareMethod("Start")
				.declareStringInput("Operation", "1")//"1" to start, 0 to shutdown .
				.build()		
			.declareStringVariable("AcSubBchId", "0") //The active sub-batch id

			.collect();
}