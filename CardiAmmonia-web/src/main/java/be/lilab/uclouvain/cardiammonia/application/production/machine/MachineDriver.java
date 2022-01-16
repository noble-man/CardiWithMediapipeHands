package be.lilab.uclouvain.cardiammonia.application.production.machine;

import java.util.concurrent.ExecutionException;

import be.lilab.uclouvain.cardiammonia.application.production.Production;
import be.lilab.uclouvain.cardiammonia.application.production.ProductionLogService;
import be.lilab.uclouvain.cardiammonia.opcua.client.CommunicationClient;

public interface MachineDriver {
	public enum State {

		STATE_OFF(0),
		STATE_READY(1),
		STATE_BUSY(2),
		STATE_PAUSED(3);
		private int value =0;
		State(int val){
			this.value = val;
		}
		int getValue() {
			return value;
		}
	}
	int getState();
	State getFriendlyState();
	
	Production getActiveSubBatch();
	CommunicationClient getOPCUAClient();
	MachineService getMachineService();
	void init();//Called to synchronize the driver with the actual machine by updating the active sub batch. If this is the first time, it will call the Start method on the machine.
}
