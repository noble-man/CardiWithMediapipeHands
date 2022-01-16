package be.lilab.uclouvain.cardiammonia.application.production.machine;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Machine {
	@Id
	private String machineId;
	private String state;//Idle, Busy, Paused, Off. See MachineDriver.State
	private String serverUrl;//Idle, Busy, Paused, Off. See MachineDriver.State
    private Timestamp startedAt;
    private Timestamp finishedAt;
	private Timestamp lastPingAt;
	private String activeProductionId;
	//private String serverUrl; //The opcua server address
	public String getMachineId() {
		return machineId;
	}
	public void setMachineId(String machineId) {
		this.machineId = machineId;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public Timestamp getStartedAt() {
		return startedAt;
	}
	public void setStartedAt(Timestamp startedAt) {
		this.startedAt = startedAt;
	}
	public Timestamp getFinishedAt() {
		return finishedAt;
	}
	public void setFinishedAt(Timestamp finishedAt) {
		this.finishedAt = finishedAt;
	}
	public Timestamp getLastPingAt() {
		return lastPingAt;
	}
	public void setLastPingAt(Timestamp lastPingAt) {
		this.lastPingAt = lastPingAt;
	}
	public String getActiveProductionId() {
		return activeProductionId;
	}
	public void setActiveProductionId(String activeProductionId) {
		this.activeProductionId = activeProductionId;
	}
/*	public String getServerUrl() {
		return serverUrl;
	}
	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}
*/	
}
