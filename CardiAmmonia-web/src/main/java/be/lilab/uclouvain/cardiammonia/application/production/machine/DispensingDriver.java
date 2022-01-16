package be.lilab.uclouvain.cardiammonia.application.production.machine;

import java.util.concurrent.ExecutionException;

import org.eclipse.milo.opcua.stack.core.UaException;

import be.lilab.uclouvain.cardiammonia.application.production.Production;
import be.lilab.uclouvain.cardiammonia.opcua.client.CommunicationClient;
import be.lilab.uclouvain.cardiammonia.opcua.server.Constants;

public class DispensingDriver  implements MachineDriver {

	CommunicationClient upcuaClient;
	//Machine cycloneEntity;
	MachineService machineService;
	private Production activeProduction;
	
	public DispensingDriver(CommunicationClient upcuaClient, MachineService machineService) {
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
			return upcuaClient.readUIntParameter("DspStt").intValue();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return Constants.DISP_STATE_UNDEFINED.intValue();
	}

	@Override
	public State getFriendlyState() {
		int state = getState();
		if (state==Constants.DISP_STATE_IDLE.intValue())
			return State.STATE_READY;
		if (state==Constants.DISP_STATE_UNDEFINED.intValue())
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
				upcuaClient.callMethod(Constants.DISPENSING_ROOT_URL,"Start", "1");//Operation 1:Start
		} catch (InterruptedException | ExecutionException | UaException e) {
			e.printStackTrace();
		}	
	}
	/**
	 * Start a sub-batch: call SubBchRq on the machine
	 * @param production: the production to run on the machine
	 */
	public void startSubBatch(Production production) {
		this.activeProduction = production;
		//currentState = new CycloneStartState(production, machineService, upcuaClient);
		try {
			upcuaClient.callMethod(Constants.DISPENSING_ROOT_URL,"SubBchRq", production.getProductionId(), production.getRoute());//SubBchID, Route: This shoud be taken from the production data
		} catch (InterruptedException | ExecutionException | UaException e) {
			// TODO Auto-generated catch block
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
