package be.lilab.uclouvain.cardiammonia.application.production.machine;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import be.lilab.uclouvain.cardiammonia.application.production.Production;
import be.lilab.uclouvain.cardiammonia.application.production.ProductionLog;
import be.lilab.uclouvain.cardiammonia.application.production.ProductionLogRepository;
import be.lilab.uclouvain.cardiammonia.application.production.ProductionRepository;

@Service
public class MachineService {
	//The machine service needs to know about the production and the machines
	@Autowired
	ProductionRepository productionRepository;
	@Autowired
	private ProductionLogRepository productionLogRepository;
	@Autowired
	MachineRepository machineRepository;

	public Optional<Production> getProduction(String id) {
		return productionRepository.findById(id);
	}

	public void updateProduction(Production production) {
		productionRepository.save(production);
	}

	//Production log
	public Optional<ProductionLog> getProductionLog(Long id) {
		return productionLogRepository.findById(id);
	}

	public void addProductionLog(ProductionLog p) {
		productionLogRepository.save(p);
	}

	public List<ProductionLog> findByProduction(Production production) {
		return productionLogRepository.findByProductionOrderByProductionLogId(production);
	}

	
	//Machine
	public Optional<Machine> getMachine(String machineId){
		return machineRepository.findById(machineId);
	}
	public Machine getCycloneMachine() {
		return getMachine("CYCLONE").get();
	}
	public Machine getDoseCalibratorMachine() {
		return getMachine("DOSECALIBRATOR").get();
	}
	public Machine getDispensingMachine() {
		return getMachine("DISPENSING").get();
	}
	public Machine getQualityControlMachine() {
		return getMachine("QC").get();
	}
	
	public void updateMachine(Machine machine) {
		machineRepository.save(machine);
	}
}
