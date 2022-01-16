package be.lilab.uclouvain.cardiammonia.application.production;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.lilab.uclouvain.cardiammonia.application.production.machine.CycloneDriver;
import be.lilab.uclouvain.cardiammonia.application.production.machine.DispensingDriver;
import be.lilab.uclouvain.cardiammonia.application.production.machine.DoseCalibratorDriver;
import be.lilab.uclouvain.cardiammonia.application.production.machine.MachineDriver;
import be.lilab.uclouvain.cardiammonia.application.production.machine.MachineService;
import be.lilab.uclouvain.cardiammonia.application.production.machine.QualityControlDriver;
import be.lilab.uclouvain.cardiammonia.opcua.server.Constants;
import be.lilab.uclouvain.cardiammonia.opcua.server.GenericMapUaStruct;


public class ProductionJob implements Runnable{
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	//private int secondCounter;
	
	MachineService machineService;
	//ProductionService productionService;
	//ProductionLogService productionLogService;
	String productionId;
	CycloneDriver cyclone;
	DispensingDriver dispensing;
	DoseCalibratorDriver doseCalibrator; 
	QualityControlDriver qualityControl;
	
/*	public ProductionJob(ProductionService productionService, ProductionLogService productionLogService, String productionId) {
		this.productionService = productionService;
		this.productionLogService = productionLogService;
		Optional<Production> productionOptional = productionService.getProduction(productionId);
		if (!productionOptional.isPresent())
			throw new RuntimeException("Production with id: "+productionId+" does not exists!");
		production = productionOptional.get();
		
	}
*/
	
	public ProductionJob(MachineService machineService, String productionId, 
			CycloneDriver cyclone, DispensingDriver dispensing, DoseCalibratorDriver doseCalibrator, QualityControlDriver qualityControl) {
		this.machineService = machineService;
		this.productionId = productionId;
		this.cyclone = cyclone;
		this.dispensing = dispensing;
		this.doseCalibrator = doseCalibrator;
		this.qualityControl = qualityControl;
	}
	public Production getProduction() {
		Optional<Production> productionOptional = machineService.getProduction(productionId);
		if (!productionOptional.isPresent())
			throw new RuntimeException("Production with id: "+productionId+" does not exist!");
		return productionOptional.get();
	}


	@Override
	public void run() {
		
		if (cyclone.getFriendlyState()!=MachineDriver.State.STATE_READY) {
			logger.error("The cyclone is not ready");
			return;//TODO: Throw an exception here??
		}
		if (dispensing.getFriendlyState()==MachineDriver.State.STATE_OFF ) {
			logger.error("The dispensing machine has a problem. Please fix it first.");
			return;//TODO: Throw an exception here??
		}
		
		ProductionLog plog = new ProductionLog( "Cyclone", "CYC_START_SUB_BATCH", "", "", getProduction());
		getMachineService().addProductionLog(plog);

		cyclone.SubBatchRequest(getProduction());

		boolean keepWorking = true;
		boolean cyc_prep_logged = false;
		boolean cyc_beaming_logged = false;
		boolean cyc_activity_ready_logged = false;
		boolean cyc_unloading_logged = false;
		boolean cyc_loading_logged = false;
		boolean disp_busy_logged =false;
		boolean disp_started =false;
		while (keepWorking) {
			try {
				Thread.sleep(200);//Just wait a bit to start reading data. This is the time needed for the subscription to be activated in the OPCUA simulator
				
				//The cyclone phase
				if (cyclone.getState()==Constants.CYC_STATE_PREPARING.intValue()) {
					if (!cyc_prep_logged) {
						logCyclonePreparingState();
						cyc_prep_logged = true;
					}
				}
				else
				if (cyclone.getState()==Constants.CYC_STATE_BEAMING.intValue()) {
					if (!cyc_beaming_logged) {
						logCycloneBeamingState();
						cyc_beaming_logged = true;
					}
					//Update the production activity
					try {
						Production prod = getProduction();
						prod.setActualActivity((Double)cyclone.getOPCUAClient().readMapParameter("ActiveTarget").getValues().get("TActActy"));
						machineService.updateProduction(prod);
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
				}
				else
				if (cyclone.getState()==Constants.CYC_STATE_ACTIVITYREADY.intValue()) {
					if (dispensing.getFriendlyState()!=MachineDriver.State.STATE_READY ) {
						logger.info("The dispensing machine is not ready. putting the sub-batch in a pause mode in the cyclone.");
						cyclone.Pause();
						long millisWaiting = 0;
						long MAX_WAITING_TIME = 3*60*1000;//wait max 3 minutes then cancel the production. 
						while (dispensing.getFriendlyState()!=MachineDriver.State.STATE_READY && millisWaiting<=MAX_WAITING_TIME) {
							try {
								Thread.sleep(200);
								millisWaiting+=200;
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						if (millisWaiting<=MAX_WAITING_TIME)
							cyclone.Resume();
						else {
							cyclone.Cancel();
							keepWorking = false;
						}
					}
					if (keepWorking) {
						if (!cyc_activity_ready_logged) {
							logCycloneActivityReadyState();
							cyc_activity_ready_logged = true;
							cyclone.UnloadTarget();
						}
					}
				}
				else
				if (cyclone.getState()==Constants.CYC_STATE_UNLOADING.intValue()) {
					if (!cyc_unloading_logged) {
						cyc_unloading_logged = true;
						logCycloneUnloadState();
						dispensing.startSubBatch(getProduction());
						disp_started = true;
						cyclone.LoadTarget();
					}
				}
				else
				if (cyclone.getState()==Constants.CYC_STATE_LOADINGNEXT.intValue()) {
					if (!cyc_loading_logged) {
						cyc_loading_logged = true;
						logCycloneLoadState();
					}
				}
				else
				if (dispensing.getFriendlyState()==MachineDriver.State.STATE_BUSY
				|| (disp_started && dispensing.getFriendlyState()==MachineDriver.State.STATE_READY)/*The dispensing has finished its job*/) {
					if (!disp_busy_logged) {
						disp_busy_logged = true;
						logDispensingBusyState();
					}
					//The stop condition: For the moment, th edispensing has completed its job
					keepWorking = !(disp_started && dispensing.getFriendlyState()==MachineDriver.State.STATE_READY);
					
				}
				

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
return;
	}
	private void logDispensingBusyState() {
		ProductionLog plog = new ProductionLog( "Dispensing", "DISP_BUSY", "", "", getProduction());
		getMachineService().addProductionLog(plog);
		plog = new ProductionLog( "Dispensing", "DISP_BUSY", "SUB_BATCH", getProduction().getProductionId()+","+getProduction().getRoute(), getProduction());
		getMachineService().addProductionLog(plog);

	}
	private void logCyclonePreparingState() {
		ProductionLog plog;
		try {
			plog = new ProductionLog( "Cyclone", "CYC_PREPARING", "CpuSs", cyclone.getOPCUAClient().readParameter("CpuSs").toString(), getProduction());
			getMachineService().addProductionLog(plog);
			plog = new ProductionLog( "Cyclone", "CYC_PREPARING", "CpuBtTi", cyclone.getOPCUAClient().readParameter("CpuBtTi").toString(), getProduction());
			getMachineService().addProductionLog(plog);
			plog = new ProductionLog( "Cyclone", "CYC_PREPARING", "FrwVer", cyclone.getOPCUAClient().readParameter("FrwVer").toString(), getProduction());
			getMachineService().addProductionLog(plog);
			plog = new ProductionLog( "Cyclone", "CYC_PREPARING", "SwVer", cyclone.getOPCUAClient().readParameter("SwVer").toString(), getProduction());
			getMachineService().addProductionLog(plog);
			plog = new ProductionLog( "Cyclone", "CYC_PREPARING", "CycStt", cyclone.getOPCUAClient().readParameter("CycStt").toString(), getProduction());
			getMachineService().addProductionLog(plog);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
	private void logCycloneBeamingState() {
		ProductionLog plog = new ProductionLog( "Cyclone", "CYC_BEAMING", "", "", getProduction());
		getMachineService().addProductionLog(plog);

		plog = new ProductionLog( "Cyclone", "CYC_BEAMING", "SUB_BATCH", getProduction().getProductionId()+","+getProduction().getRecipeId(), getProduction());
		getMachineService().addProductionLog(plog);

		plog = new ProductionLog( "Cyclone", "CYC_BEAMING", "requestValidated", "true", getProduction());
		getMachineService().addProductionLog(plog);
		
		plog = new ProductionLog( "Cyclone", "CYC_BEAMING", "rejectionReason", "not rejected", getProduction());
		getMachineService().addProductionLog(plog);		
	}
	private void logCycloneActivityReadyState() {
		ProductionLog plog = new ProductionLog( "Cyclone", "CYC_ACTIVITYREADY", "", "", getProduction());
		getMachineService().addProductionLog(plog);

		GenericMapUaStruct struct;
		try {
			struct = cyclone.getOPCUAClient().readMapParameter("ActiveTarget");
			plog = new ProductionLog( "Cyclone", "CYC_ACTIVITYREADY", "TNb", struct.getValues().get("TNb").toString(), getProduction());
			getMachineService().addProductionLog(plog);
			plog = new ProductionLog( "Cyclone", "CYC_ACTIVITYREADY", "Stt", struct.getValues().get("Stt").toString(), getProduction());
			getMachineService().addProductionLog(plog);
			plog = new ProductionLog( "Cyclone", "CYC_ACTIVITYREADY", "SttDsc", struct.getValues().get("SttDsc").toString(), getProduction());
			getMachineService().addProductionLog(plog);
			plog = new ProductionLog( "Cyclone", "CYC_ACTIVITYREADY", "LdVol", struct.getValues().get("LdVol").toString(), getProduction());
			getMachineService().addProductionLog(plog);
			plog = new ProductionLog( "Cyclone", "CYC_ACTIVITYREADY", "BOnTi", struct.getValues().get("BOnTi").toString(), getProduction());
			getMachineService().addProductionLog(plog);
			plog = new ProductionLog( "Cyclone", "CYC_ACTIVITYREADY", "BOffTi", struct.getValues().get("BOffTi").toString(), getProduction());
			getMachineService().addProductionLog(plog);
			plog = new ProductionLog( "Cyclone", "CYC_ACTIVITYREADY", "TRqdCu", struct.getValues().get("TRqdCu").toString(), getProduction());
			getMachineService().addProductionLog(plog);
			plog = new ProductionLog( "Cyclone", "CYC_ACTIVITYREADY", "TPMinRchd", struct.getValues().get("TPMinRchd").toString(), getProduction());
			getMachineService().addProductionLog(plog);
			plog = new ProductionLog( "Cyclone", "CYC_ACTIVITYREADY", "TIntdCu", struct.getValues().get("TIntdCu").toString(), getProduction());
			getMachineService().addProductionLog(plog);
			plog = new ProductionLog( "Cyclone", "CYC_ACTIVITYREADY", "TActActy", struct.getValues().get("TActActy").toString(), getProduction());
			getMachineService().addProductionLog(plog);
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void logCycloneUnloadState() {
		ProductionLog plog = new ProductionLog( "Cyclone", "CYC_UNLOADTARGET", "", "", getProduction());
		getMachineService().addProductionLog(plog);

		plog = new ProductionLog( "Cyclone", "CYC_UNLOADTARGET", "SUB_BATCH_UNLOAD", getProduction().getProductionId()+", true", getProduction());
		getMachineService().addProductionLog(plog);

		GenericMapUaStruct struct;
		try {
			struct = cyclone.getOPCUAClient().readMapParameter("ActiveTarget");

			plog = new ProductionLog( "Cyclone", "CYC_UNLOADTARGET", "RqdTActRdy", struct.getValues().get("RqdTActRdy").toString(), getProduction());
			getMachineService().addProductionLog(plog);
			plog = new ProductionLog( "Cyclone", "CYC_UNLOADTARGET", "LaLdTTi", struct.getValues().get("LaLdTTi").toString(), getProduction());
			getMachineService().addProductionLog(plog);
			plog = new ProductionLog( "Cyclone", "CYC_UNLOADTARGET", "UnLdStaTi", struct.getValues().get("UnLdStaTi").toString(), getProduction());
			getMachineService().addProductionLog(plog);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
	private void logCycloneLoadState() {
		ProductionLog plog = new ProductionLog( "Cyclon", "CYC_LOADTARGET", "", "", getProduction());
		getMachineService().addProductionLog(plog);

		plog = new ProductionLog( "Cyclon", "CYC_LOADTARGET", "SUB_BATCH_UNLOAD", getProduction().getProductionId()+", false", getProduction());
		getMachineService().addProductionLog(plog);
	}
	public MachineService getMachineService() {
		return machineService;
	}

/*	public String ping() {
		return "I have been running since "+this.secondCounter +" seconds";
	}
	*/
}
