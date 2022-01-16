package be.lilab.uclouvain.cardiammonia.application.production.machine;

import org.springframework.stereotype.Service;

import be.lilab.uclouvain.cardiammonia.opcua.client.CommunicationClient;

public class DoseCalibratorDriver {

	CommunicationClient upcuaClient;
	Machine cycloneEntity;
	MachineService machineService;
	public DoseCalibratorDriver(CommunicationClient upcuaClient, MachineService machineService) {
		this.upcuaClient = upcuaClient;
		this.machineService = machineService;
		//this.cycloneEntity = machineService.getCycloneMachine();
	}
}
