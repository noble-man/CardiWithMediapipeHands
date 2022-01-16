package be.lilab.uclouvain.cardiammonia.application;

import java.awt.Robot;

import javax.annotation.PreDestroy;

import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import be.lilab.uclouvain.cardiammonia.opcua.server.simulator.SimulationServerBuilder;


@SpringBootApplication
public class CardiAmmoniaApplication {
	private static Logger logger = LoggerFactory.getLogger(CardiAmmoniaApplication.class);
	/**
	 * To run the OPCUA simualtors, pass the argument {--simulators}
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		try {
            Robot robot = new Robot();
            robot.mouseMove(0,0);
            robot.mouseMove(10,10);
            robot.mouseMove(70,35);
            //robot.mouseMove(100,100);
            //robot.mouseMove(200,200);
            //robot.mouseMove(354,713);
    
        } catch (Exception e) {
            //e.printStackTrace();
        }
		
		
		for (String s:args) {
			if ("--simulators".equals(s)) {
				logger.info("Starting the simulators ..");
				new Thread(){ public void run(){ CycloneSimulator.main(null); }}.start();
				new Thread(){ public void run(){ DispensingSimulator.main(null); }}.start();
				new Thread(){ public void run(){ QCSimulator.main(null); }}.start();
				break;
			}
		}
		SpringApplication.run(CardiAmmoniaApplication.class, args);
		
		
		
		
		
	}
	
	 

	
/*	

	@Value("${use.cyclone.server.simulator}")
	private boolean useCycloneSimulatorServer;

	@Value("${use.dispensing.server.simulator}")
	private boolean useDispensingSimulatorServer;
	@Value("${use.dosecalibrator.server.simulator}")
	private boolean useDoseCalibratorSimulatorServer;
	@Value("${use.qc.server.simulator}")
	private boolean useQCSimulatorServer;

	
	private OpcUaServer cycloneServer;
	private OpcUaServer dispensingServer;
	private OpcUaServer doseCalibratorServer;
	private OpcUaServer qcServer;
*/

	//Did not work. Beans are initiated before starting the server.. Kept for documentation purposes.
/*	@EventListener(ApplicationReadyEvent.class)
	public void startOPCUASimulator() {
		try {
			if (useCycloneSimulatorServer) {
			    logger.info("starting the cyclone simulator server...");
			    cycloneServer = SimulationServerBuilder.buildCycloneServer();
			    logger.info("The cyclone simulator server has started successfuly.");
			}
			if (useDispensingSimulatorServer) {
			    logger.info("starting the dispensing simulator server...");
			    dispensingServer = SimulationServerBuilder.buildDispensingServer();
			    logger.info("The dispensing simulator server has started successfuly.");
			}
			if (useDoseCalibratorSimulatorServer) {
			    logger.info("starting the dose calibrator simulator server...");
			    doseCalibratorServer = SimulationServerBuilder.buildDoseCalibratorServer();
			    logger.info("The dose calibrator simulator server has started successfuly.");
			}
			if (useQCSimulatorServer) {
			    logger.info("starting the QC simulator server...");
			    qcServer = SimulationServerBuilder.buildQCServer();
			    logger.info("The QC simulator server has started successfuly.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@PreDestroy
	public void stopOPCUASimulator() {
		if (useCycloneSimulatorServer) {
		    logger.info("stopping the cyclone simulator server...");
		    cycloneServer.shutdown();
		    logger.info("The cyclone simulator server has stopped successfully.");
		}
		if (useDispensingSimulatorServer) {
		    logger.info("stopping the dispensing simulator server...");
		    dispensingServer.shutdown();
		    logger.info("The dispensing simulator server has stopped successfuly.");
		}
		if (useDoseCalibratorSimulatorServer) {
		    logger.info("stopping the dose calibrator simulator server...");
		    doseCalibratorServer.shutdown();
		    logger.info("The dose calibrator simulator server has stopped successfuly.");
		}
		if (useQCSimulatorServer) {
		    logger.info("stopping the QC simulator server...");
		    qcServer.shutdown();
		    logger.info("The QC simulator server has stopped successfuly.");
		}
	}
	*/
}
