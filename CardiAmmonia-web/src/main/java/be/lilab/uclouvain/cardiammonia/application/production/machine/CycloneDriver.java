package be.lilab.uclouvain.cardiammonia.application.production.machine;

import java.util.concurrent.ExecutionException;

import org.eclipse.milo.opcua.stack.core.UaException;

import be.lilab.uclouvain.cardiammonia.application.production.Production;
import be.lilab.uclouvain.cardiammonia.opcua.client.CommunicationClient;
import be.lilab.uclouvain.cardiammonia.opcua.server.Constants;


public class CycloneDriver implements MachineDriver {

	CommunicationClient upcuaClient;
	//Machine cycloneEntity;
	MachineService machineService;
	private Production activeProduction;
	
	public CycloneDriver() {
		
	}
	public CycloneDriver(CommunicationClient upcuaClient, MachineService machineService) {
		this.upcuaClient = upcuaClient;
		this.machineService = machineService;
		//this.cycloneEntity = machineService.getCycloneMachine();
		init();
	}

	@Override
	public MachineService getMachineService() {
		return this.machineService;
	}

	@Override
	public CommunicationClient getOPCUAClient() {
		return upcuaClient;
	}

	public Production getActiveSubBatch() {
		try {
			String activeSubPatchId = upcuaClient.readStringParameter("AcSubBchId");
			if (activeSubPatchId.equals("0"))
					activeProduction = null;
			else if (activeProduction==null)
					activeProduction = machineService.getProduction(activeSubPatchId).get();
			else
				if (!activeProduction.getProductionId().equals(activeSubPatchId))
					activeProduction = machineService.getProduction(activeSubPatchId).get();
			
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}	

		return this.activeProduction;
	}

	@Override
	public int getState(){
		try {
			return upcuaClient.readUIntParameter("CycStt").intValue();
		} catch (InterruptedException | ExecutionException e) {
			return Constants.CYC_STATE_UNDEFINED.intValue();
		}
	}

	@Override
	public State getFriendlyState() {
		int state = getState();
		if (state==Constants.CYC_STATE_IDLE.intValue())
			return State.STATE_READY;
		if (state==Constants.CYC_STATE_PAUSED.intValue())
			return State.STATE_PAUSED;
		if (state==Constants.CYC_STATE_UNDEFINED.intValue()
				||state==Constants.CYC_STATE_TOTALOFF.intValue()
			||state==Constants.CYC_STATE_VACCUM.intValue()
				||state==Constants.CYC_STATE_SHUTTINGDOWN.intValue())
			return State.STATE_OFF;
		return State.STATE_BUSY;

	}
	
	@Override
	public void init() {
		try {
			State currState = getFriendlyState(); 
			if (currState == State.STATE_BUSY || currState == State.STATE_PAUSED)
				activeProduction = machineService.getProduction(upcuaClient.readStringParameter("AcSubBchId")).get();
			else
				upcuaClient.callMethod(Constants.CYCLONE_ROOT_URL,"Start", "1");//Operation 1:Start
		} catch (InterruptedException | ExecutionException | UaException e) {
			e.printStackTrace();
		}	
		catch(NullPointerException e) {//It might happen if the cyclone server is running a sub-batch but that sub-batch does not exist in the database.
			
		}
	}

	/**
	 * Start a sub-batch: call SubBchRq on the machine
	 * @param production: the production to run on the machine
	 */
	public void SubBatchRequest(Production production) {
		this.activeProduction = production;
		//currentState = new CycloneStartState(production, machineService, upcuaClient);
		try {
			upcuaClient.callMethod(Constants.CYCLONE_ROOT_URL,"SubBchRq", activeProduction.getProductionId(), "1","30");//SubBchID, RecID, ActRqd
		} catch (InterruptedException | ExecutionException | UaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Unload target. Call SubBchUnld
	 */
	public void UnloadTarget() {
		try {
			upcuaClient.callMethod(Constants.CYCLONE_ROOT_URL,"SubBchUnld", getActiveSubBatch().getProductionId(), true);//SubBchID, UnldRq
		} catch (InterruptedException | ExecutionException | UaException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Load target. Call SubBchUnld
	 */
	public void LoadTarget() {
		try {
			upcuaClient.callMethod(Constants.CYCLONE_ROOT_URL,"SubBchUnld", getActiveSubBatch().getProductionId(), false);//SubBchID, UnldRq
		} catch (InterruptedException | ExecutionException | UaException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Pause the production. Call SubBchPause
	 */
	public void Pause() {
		try {
			upcuaClient.callMethod(Constants.CYCLONE_ROOT_URL,"SubBchPause", getActiveSubBatch().getProductionId());//SubBchID
		} catch (InterruptedException | ExecutionException | UaException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Pause the production. Call SubBchResume
	 */
	public void Resume() {
		try {
			upcuaClient.callMethod(Constants.CYCLONE_ROOT_URL,"SubBchResume", getActiveSubBatch().getProductionId());//SubBchID
		} catch (InterruptedException | ExecutionException | UaException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Pause the production. Call SubBchCancel
	 */
	public void Cancel() {
		try {
			upcuaClient.callMethod(Constants.CYCLONE_ROOT_URL,"SubBchCancel", getActiveSubBatch().getProductionId());//SubBchID
		} catch (InterruptedException | ExecutionException | UaException e) {
			e.printStackTrace();
		}
	}
}
/*
	
	public static abstract class CardiammoniaMachineState implements State{
		private Production production;
		private MachineService machineService;
		private CommunicationClient opcuaClient;
		public CardiammoniaMachineState(Production production, MachineService machineService, CommunicationClient opcuaClient) {
			this.production = production;
			this.machineService = machineService;
			this.opcuaClient = opcuaClient;
		}
		public Production getProduction() {
			return production;
		}
		public MachineService getMachineService() {
			return machineService;
		}
		public CommunicationClient getOPCUAClient() {
			return opcuaClient;
		}
		@Override
		public boolean isFinalState() {
			return false;
		}
		@Override
		public void doAction() {
		}

	}
	
	public static class CycloneStartState extends CardiammoniaMachineState{

		public CycloneStartState(Production production, MachineService machineService, CommunicationClient opcuaClient) {
			super(production, machineService, opcuaClient);
		}
		@Override
		public State nextState() {
			return new CyclonePreparingState(getProduction(), getMachineService(), getOPCUAClient());
		}

		@Override
		public void doAction() {
			ProductionLog plog = new ProductionLog( "Cyclon", "CYC_START_SUB_BATCH", "", "", getProduction());
			getMachineService().addProductionLog(plog);
			super.doAction();
		}
		
	}

	public static class CyclonePreparingState extends CardiammoniaMachineState{

		public CyclonePreparingState(Production production, MachineService machineService, CommunicationClient opcuaClient) {
			super(production, machineService, opcuaClient);
		}

		
		@Override
		public State nextState() {
			return new CycloneBeamingState(getProduction(), getMachineService(), getOPCUAClient());
		}

		@Override
		public void doAction() {
			//Read Cyclo state. If ready, continue.
			ProductionLog plog = new ProductionLog( "Cyclon", "CYC_PREPARING", "", "", getProduction());
			getMachineService().addProductionLog(plog);

			//Log state
			//Read the following params: CPU status, CPU boot time, Firmware version, Software version and Cyclone state
			//Log the above params.			
			try {
				plog = new ProductionLog( "Cyclon", "CYC_PREPARING", "CPUStatus", getOPCUAClient().readParameter("CPUStatus").toString(), getProduction());
				getMachineService().addProductionLog(plog);
				plog = new ProductionLog( "Cyclon", "CYC_PREPARING", "CPUBootTime", getOPCUAClient().readParameter("CPUBootTime").toString(), getProduction());
				getMachineService().addProductionLog(plog);
				plog = new ProductionLog( "Cyclon", "CYC_PREPARING", "FirmwareVersion", getOPCUAClient().readParameter("FirmwareVersion").toString(), getProduction());
				getMachineService().addProductionLog(plog);
				plog = new ProductionLog( "Cyclon", "CYC_PREPARING", "SoftwareVersion", getOPCUAClient().readParameter("SoftwareVersion").toString(), getProduction());
				getMachineService().addProductionLog(plog);
				plog = new ProductionLog( "Cyclon", "CYC_PREPARING", "CycloneState", getOPCUAClient().readParameter("CycloneState").toString(), getProduction());
				getMachineService().addProductionLog(plog);
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			super.doAction();

		}
		
	}
	public static class CycloneBeamingState extends CardiammoniaMachineState{

		public CycloneBeamingState(Production production, MachineService machineService, CommunicationClient opcuaClient) {
			super(production, machineService, opcuaClient);
		}

		@Override
		public State nextState() {
			return new CycloneActivityIsReadyState(getProduction(), getMachineService(), getOPCUAClient());
		}


		@Override
		public void doAction() {
			//Call the cyclone method Sub-batch request. pass the parameters: Sub-batch ID, Recipe ID
			//Receive returned struct and log all values: Sub-batch ID, Request validated and Request rejection reason
			ProductionLog plog = new ProductionLog( "Cyclon", "CYC_BEAMING", "", "", getProduction());
			getMachineService().addProductionLog(plog);

			plog = new ProductionLog( "Cyclon", "CYC_BEAMING", "SUB_BATCH", getProduction().getProductionId()+","+getProduction().getRecipeId(), getProduction());
			getMachineService().addProductionLog(plog);

			plog = new ProductionLog( "Cyclon", "CYC_BEAMING", "requestValidated", "true", getProduction());
			getMachineService().addProductionLog(plog);
			
			plog = new ProductionLog( "Cyclon", "CYC_BEAMING", "rejectionReason", "not rejected", getProduction());
			getMachineService().addProductionLog(plog);
			super.doAction();
		}
		
	}
	public static class CycloneActivityIsReadyState extends CardiammoniaMachineState{

		public CycloneActivityIsReadyState(Production production, MachineService machineService, CommunicationClient opcuaClient) {
			super(production, machineService, opcuaClient);
		}
		
		@Override
		public State nextState() {
			return new CycloneUnloadingTargetState(getProduction(), getMachineService(), getOPCUAClient());
		}

		@Override
		public void doAction() {
			//Read the struct Active NH3 target here???, if yes, do the following below:
				// Read variables Target number, State, State, Loaded volume, Beam on time, Beam off time, Requested current, Pressure min reached, Integrated current and Actual activity
				// Log these values
			ProductionLog plog = new ProductionLog( "Cyclon", "CYC_ACTIVITYREADY", "", "", getProduction());
			getMachineService().addProductionLog(plog);

			plog = new ProductionLog( "Cyclon", "CYC_ACTIVITYREADY", "targetNumber", "1", getProduction());
			getMachineService().addProductionLog(plog);
			plog = new ProductionLog( "Cyclon", "CYC_ACTIVITYREADY", "stateCode", "10", getProduction());
			getMachineService().addProductionLog(plog);
			plog = new ProductionLog( "Cyclon", "CYC_ACTIVITYREADY", "state", "targetState", getProduction());
			getMachineService().addProductionLog(plog);
			plog = new ProductionLog( "Cyclon", "CYC_ACTIVITYREADY", "loadedVolume", "10", getProduction());
			getMachineService().addProductionLog(plog);
			plog = new ProductionLog( "Cyclon", "CYC_ACTIVITYREADY", "beamOnTime", "date and time", getProduction());
			getMachineService().addProductionLog(plog);
			plog = new ProductionLog( "Cyclon", "CYC_ACTIVITYREADY", "beamOffTime", "date and time", getProduction());
			getMachineService().addProductionLog(plog);
			plog = new ProductionLog( "Cyclon", "CYC_ACTIVITYREADY", "current", "3", getProduction());
			getMachineService().addProductionLog(plog);
			plog = new ProductionLog( "Cyclon", "CYC_ACTIVITYREADY", "pressure", "pressure", getProduction());
			getMachineService().addProductionLog(plog);
			plog = new ProductionLog( "Cyclon", "CYC_ACTIVITYREADY", "integratedCurrent", "2.5", getProduction());
			getMachineService().addProductionLog(plog);
			plog = new ProductionLog( "Cyclon", "CYC_ACTIVITYREADY", "actualActivity", "35", getProduction());
			getMachineService().addProductionLog(plog);
			super.doAction();
		}
		
	}
	public static class CycloneUnloadingTargetState extends CardiammoniaMachineState{

		public CycloneUnloadingTargetState(Production production, MachineService machineService, CommunicationClient opcuaClient) {
			super(production, machineService, opcuaClient);
		}

		
		@Override
		public State nextState() {
			return new CycloneLoadingNextTargetState(getProduction(), getMachineService(), getOPCUAClient());
		}

		@Override
		public void doAction() {
			//Call method Sub-batch unload. Pass params: Sub-batch ID and Unload request (true)
			//Read the struct Active NH3 target.Log all values or just the following:
			// Read variables Activity ready for unload, Loaded time, Unload start time and Unloaded activity ??
			// Log these values
			ProductionLog plog = new ProductionLog( "Cyclon", "CYC_UNLOADTARGET", "", "", getProduction());
			getMachineService().addProductionLog(plog);

			plog = new ProductionLog( "Cyclon", "CYC_UNLOADTARGET", "SUB_BATCH_UNLOAD", getProduction().getProductionId()+", true", getProduction());
			getMachineService().addProductionLog(plog);

			plog = new ProductionLog( "Cyclon", "CYC_UNLOADTARGET", "readyForUnloadTime", "date and time", getProduction());
			getMachineService().addProductionLog(plog);
			plog = new ProductionLog( "Cyclon", "CYC_UNLOADTARGET", "loadedTime", "date and time", getProduction());
			getMachineService().addProductionLog(plog);
			plog = new ProductionLog( "Cyclon", "CYC_UNLOADTARGET", "unloadStartTime", "date and time", getProduction());
			getMachineService().addProductionLog(plog);
			super.doAction();
			
		}
		
	}
	public static class CycloneLoadingNextTargetState extends CardiammoniaMachineState{

		public CycloneLoadingNextTargetState(Production production, MachineService machineService, CommunicationClient opcuaClient) {
			super(production, machineService, opcuaClient);
		}

		
		@Override
		public State nextState() {
			return new CycloneFinalState(getProduction(), getMachineService(), getOPCUAClient());
		}

		@Override
		public void doAction() {
			//Call method Sub-batch unload. Pass params: Sub-batch ID and Unload request (false)
			//no params to log here..
			ProductionLog plog = new ProductionLog( "Cyclon", "CYC_LOADTARGET", "", "", getProduction());
			getMachineService().addProductionLog(plog);

			plog = new ProductionLog( "Cyclon", "CYC_LOADTARGET", "SUB_BATCH_UNLOAD", getProduction().getProductionId()+", false", getProduction());
			getMachineService().addProductionLog(plog);
			super.doAction();
			
		}
	}
	public static class CycloneFinalState extends CardiammoniaMachineState{

		public CycloneFinalState(Production production, MachineService machineService, CommunicationClient opcuaClient) {
			super(production, machineService, opcuaClient);
		}

		
		@Override
		public State nextState() {
			return this;
		}

		@Override
		public void doAction() {
			ProductionLog plog = new ProductionLog( "Cyclon", "CYC_COMPLETED", "", "", getProduction());
			getMachineService().addProductionLog(plog);
		}
		@Override
		public boolean isFinalState() {
			return true;
		}

	}
*/
