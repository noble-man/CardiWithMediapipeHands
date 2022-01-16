package be.lilab.uclouvain.cardiammonia.application.production;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import be.lilab.uclouvain.cardiammonia.application.user.User;

@Service
public class ProductionService {

	@Autowired
	private ProductionRepository productionRepository;

	public Optional<Production> getProduction(String id) {
		return productionRepository.findById(id);
	}

	public void addProduction(Production p) {
		productionRepository.save(p);
	}
	
	public void updateProduction(Production production) {
		productionRepository.save(production);
	}


}
