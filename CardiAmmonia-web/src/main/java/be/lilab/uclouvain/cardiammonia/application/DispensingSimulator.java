package be.lilab.uclouvain.cardiammonia.application;

import java.io.IOException;

import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.lilab.uclouvain.cardiammonia.opcua.client.CommunicationClient;
import be.lilab.uclouvain.cardiammonia.opcua.client.CommunicationClientFactory;
import be.lilab.uclouvain.cardiammonia.opcua.server.Constants;
import be.lilab.uclouvain.cardiammonia.opcua.server.simulator.SimulationServerBuilder;

public class DispensingSimulator {

	private static Logger logger = LoggerFactory.getLogger(DispensingSimulator.class);

	public static void main(String[] args) {
	    try {
		    logger.info("starting the dispensing simulator server...");
		    SimulationServerBuilder.SIMULATION_SPEED = 60;
		    OpcUaServer  dispensingServer = SimulationServerBuilder.buildDispensingServer();
			CommunicationClient client = CommunicationClientFactory.get()
					.setOpcUaProtocol()
					.setServerUrl("127.0.0.1:"+Constants.DISPENSING_TCP_PORT)
					.setServerPath("/milo")
					.setRootNodeUrl(Constants.DISPENSING_ROOT_URL).build();
			client.connect();
			client.callMethod(Constants.DISPENSING_ROOT_URL,"Start","1");
		    
		    logger.info("The dispensing simulator server has started successfuly.");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
