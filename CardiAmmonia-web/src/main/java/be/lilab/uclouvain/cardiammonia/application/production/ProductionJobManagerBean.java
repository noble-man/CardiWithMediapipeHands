package be.lilab.uclouvain.cardiammonia.application.production;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import be.lilab.uclouvain.cardiammonia.application.production.machine.CycloneDriver;
import be.lilab.uclouvain.cardiammonia.application.production.machine.DispensingDriver;
import be.lilab.uclouvain.cardiammonia.application.production.machine.DoseCalibratorDriver;

@Component
public class ProductionJobManagerBean {

	@Autowired
	CycloneDriver cycloneMachine;
	@Autowired
	DispensingDriver dispensingMachine;
	@Autowired
	DoseCalibratorDriver doseCalibratorMachine;
	
	Set<ProductionJob> productionJobSet = new HashSet<>();
	public boolean register(ProductionJob productionJob) {
		//TODO: Clean finished jobs and check there are no more than 2 active jobs at a time.
		productionJobSet.add(productionJob);
		return true;
	}
	
	public Optional<ProductionJob> getProductionJob(String productionId) {
		return productionJobSet.stream().filter(p -> p.getProduction().getProductionId().equals(productionId)).findFirst();
	}

	public CycloneDriver getCycloneMachine() {
		return cycloneMachine;
	}

	public DispensingDriver getDispensingMachine() {
		return dispensingMachine;
	}

	public DoseCalibratorDriver getDoseCalibratorMachine() {
		return doseCalibratorMachine;
	}

	public Set<ProductionJob> getProductionJobSet() {
		return productionJobSet;
	}

	
}
