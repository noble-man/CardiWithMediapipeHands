package be.lilab.uclouvain.cardiammonia.application.production.machine;

import java.util.concurrent.ExecutionException;

import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;

import be.lilab.uclouvain.cardiammonia.application.production.Production;
import be.lilab.uclouvain.cardiammonia.opcua.client.CommunicationClient;
import be.lilab.uclouvain.cardiammonia.opcua.server.Constants;


public class QualityControlDriver implements MachineDriver{

	CommunicationClient upcuaClient;
	//Machine cycloneEntity;
	MachineService machineService;
	private Production activeProduction;
	public QualityControlDriver(CommunicationClient upcuaClient, MachineService machineService) {
		this.upcuaClient = upcuaClient;
		this.machineService = machineService;
		//this.cycloneEntity = machineService.getCycloneMachine();
		init();
	}
	
	/**
	 * Start a sub-batch: call QcSubBchRq on the machine
	 * @param production: the production to run on the machine
	 */	
	public void startSubBatch(Production production) {
		this.activeProduction = production;
		try {
			upcuaClient.callMethod(Constants.QC_ROOT_URL,"QcSubBchRq", production.getProductionId(), "production name", DateTime.now());//SubBchID, PrdNa, PrdDat.
		} catch (InterruptedException | ExecutionException | UaException e) {
			e.printStackTrace();
		}
	}
	public String subBatchStatus(Production production) {
		this.activeProduction = production;
		try {
			return upcuaClient.callMethod(Constants.QC_ROOT_URL,"QcSubBchSsRq", production.getProductionId())[0].getValue().toString();//SubBchID. return status
		} catch (InterruptedException | ExecutionException | UaException e) {
			e.printStackTrace();
		}
		return null;
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
			return upcuaClient.readUIntParameter("QcStt").intValue();
		} catch (InterruptedException | ExecutionException e) {
			return Constants.DISP_STATE_UNDEFINED.intValue();
		}
	}

	@Override
	public State getFriendlyState() {
		int state = getState();
		if (state==Constants.QC_STATE_SW_ON.intValue()
				|| state==Constants.QC_STATE_PARTIAL_RELEASE.intValue())
			return State.STATE_READY;
		
		if (state==Constants.QC_STATE_OFF.intValue()
				||state==Constants.QC_STATE_ERROR.intValue())
			return State.STATE_OFF;
		if (state==Constants.QC_STATE_BATCH_RECEIVED.intValue()
				|| state==Constants.QC_STATE_REQUEST_RECEIVED.intValue()
				|| state==Constants.QC_STATE_PREPARING.intValue()
				|| state==Constants.QC_STATE_READY_WAITING_SAMPLE.intValue()
				|| state==Constants.QC_STATE_RUNNING.intValue())
			return State.STATE_BUSY;
		return State.STATE_OFF;		
	}	
	@Override
	public void init() {
		try {
			State currState = getFriendlyState(); 
			if (currState == State.STATE_BUSY || currState == State.STATE_PAUSED)
				activeProduction = machineService.getProduction(upcuaClient.readStringParameter("AcSubBchId")).get();
			else
				upcuaClient.callMethod(Constants.QC_ROOT_URL,"Start", "1");//Operation 1:Start
		} catch (InterruptedException | ExecutionException | UaException e) {
			e.printStackTrace();
		}	
	}

}
