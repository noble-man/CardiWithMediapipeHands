package be.lilab.uclouvain.cardiammonia.application;

import java.io.IOException;

import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.lilab.uclouvain.cardiammonia.opcua.client.CommunicationClient;
import be.lilab.uclouvain.cardiammonia.opcua.client.CommunicationClientFactory;
import be.lilab.uclouvain.cardiammonia.opcua.server.Constants;
import be.lilab.uclouvain.cardiammonia.opcua.server.simulator.SimulationServerBuilder;

public class CycloneSimulator {
	private static Logger logger = LoggerFactory.getLogger(CycloneSimulator.class);

	public static void main(String[] args) {
	    try {
		    logger.info("starting the cyclone simulator server...");
		    SimulationServerBuilder.SIMULATION_SPEED = 60;
			OpcUaServer cycloneServer = SimulationServerBuilder.buildCycloneServer();
			CommunicationClient client = CommunicationClientFactory.get()
					.setOpcUaProtocol()
					.setServerUrl("127.0.0.1:"+Constants.CYCLONE_TCP_PORT)
					.setServerPath("/milo")
					.setRootNodeUrl(Constants.CYCLONE_ROOT_URL).build();
			client.connect();
			client.callMethod(Constants.CYCLONE_ROOT_URL,"Start","1");

		    logger.info("The cyclone simulator server has started successfuly.");
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
