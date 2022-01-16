package be.lilab.uclouvain.cardiammonia.application.production;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductionLogService {

	@Autowired
	private ProductionLogRepository productionLogRepository;

	public Optional<ProductionLog> getProductionLog(Long id) {
		return productionLogRepository.findById(id);
	}

	public void addProductionLog(ProductionLog p) {
		productionLogRepository.save(p);
	}

	public List<ProductionLog> findByProduction(Production production) {
		return productionLogRepository.findByProductionOrderByProductionLogId(production);
	}
	
/*	public void updateProductionLog(ProductionLog production) {
		productionLogRepository.save(production);
	}
*/
}
