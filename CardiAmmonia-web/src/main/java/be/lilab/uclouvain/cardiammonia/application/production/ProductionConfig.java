package be.lilab.uclouvain.cardiammonia.application.production;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import be.lilab.uclouvain.cardiammonia.application.production.machine.CycloneDriver;
import be.lilab.uclouvain.cardiammonia.application.production.machine.DispensingDriver;
import be.lilab.uclouvain.cardiammonia.application.production.machine.DoseCalibratorDriver;
import be.lilab.uclouvain.cardiammonia.application.production.machine.MachineService;
import be.lilab.uclouvain.cardiammonia.application.production.machine.QualityControlDriver;
import be.lilab.uclouvain.cardiammonia.opcua.client.CommunicationClient;
import be.lilab.uclouvain.cardiammonia.opcua.client.CommunicationClientFactory;
import be.lilab.uclouvain.cardiammonia.opcua.server.Constants;
import be.lilab.uclouvain.cardiammonia.opcua.server.ServerVariablesBuilder;

@Configuration
public class ProductionConfig {

	@Value("${opcua.server.cyclone.url}")
	private String cycloneEndpointUrl;
	@Value("${opcua.server.dispensing.url}")
	private String dispensingEndpointUrl;
	@Value("${opcua.server.qc.url}")
	private String qcEndpointUrl;
	@Value("${opcua.server.dosecalibrator.url}")
	private String dosecalibratorEndpointUrl;

	
	@Autowired
	CycloneDriver cyclone;
	@Autowired
	DispensingDriver dispensing;
	@Autowired
	DoseCalibratorDriver doseCalibrator; 
	@Autowired
	QualityControlDriver qualityControl;

	@Bean
	public TaskExecutor taskExecutor() {
	    return new SimpleAsyncTaskExecutor(); // Or use another one of your liking
	}
	
	
	@Bean
	public CycloneDriver cycloneDriver() throws Exception {
		CommunicationClient client = CommunicationClientFactory.get().setServerUrlAndPath(cycloneEndpointUrl).setOpcUaProtocol().setRootNodeUrl(Constants.CYCLONE_ROOT_URL).build();
		client.connect();
		ServerVariablesBuilder.MapVariable activeTargetStructVar =  (ServerVariablesBuilder.MapVariable)Constants.CYCLONE_VARIABLES.get("ActiveTarget");
		//Register map types after connecting
		client.registerMapType(activeTargetStructVar.getValue(), Constants.CYCLONE_NAMESPACE_URI, "ActiveTargetType");		
		return new CycloneDriver(client, machineService);
		
		//return new CycloneDriver();
	}
	
	@Bean
	public DispensingDriver dispensingDriver() throws Exception {
		CommunicationClient client = CommunicationClientFactory.get().setServerUrlAndPath(dispensingEndpointUrl).setOpcUaProtocol().setRootNodeUrl(Constants.DISPENSING_ROOT_URL).build();
		client.connect();
		return new DispensingDriver(client, machineService);
	}

	@Bean
	public DoseCalibratorDriver doseCalibratorDriver() throws Exception {
		//CommunicationClient client = CommunicationClientFactory.get().setServerUrl(dosecalibratorEndpointUrl).setOpcUaProtocol().setRootNodeUrl(Constants.DOSE_CALIBRATOR_ROOT_URL).build();
		CommunicationClient client =null;
		return new DoseCalibratorDriver(client, machineService);
	}

	@Bean
	public QualityControlDriver qualityControlDriver() throws Exception {
		CommunicationClient client = CommunicationClientFactory.get().setServerUrlAndPath(qcEndpointUrl).setOpcUaProtocol().setRootNodeUrl(Constants.QC_ROOT_URL).build();
		client.connect();
		return new QualityControlDriver(client, machineService);
	}
	
/*	@Bean
	public ProductionJobManagerBean productionJobManagerBean () {
		return new ProductionJobManagerBean();
	}
*/
/*	@Autowired
	ProductionService productionService;
	@Autowired
	ProductionLogService productionLogService;
*/
	@Autowired
	MachineService machineService;

	@Bean
	@Scope("prototype")
	public CommandLineRunner schedulingRunner(TaskExecutor executor, ProductionJobManagerBean productionJobManagerBean) {
		
	    return new CommandLineRunner() {		

			@Override
	        public void run(String... args) throws Exception {
				if (args.length==0)
					return;
				if (!Pattern.compile("\\d+(-\\d+)?").matcher(args[0]).matches())//A production id looks like: 123456789-123456789
					return;
	        	ProductionJob productionJob = new ProductionJob(machineService, args[0], cyclone, dispensing, doseCalibrator, qualityControl);
	        	if (productionJobManagerBean.register(productionJob))
	            	executor.execute(productionJob);
	            else
	            	throw new Exception("Cannot start a new production.");
	        }
	    };
	}
	
}
