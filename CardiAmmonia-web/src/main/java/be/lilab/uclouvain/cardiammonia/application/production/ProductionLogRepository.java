package be.lilab.uclouvain.cardiammonia.application.production;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductionLogRepository extends JpaRepository<ProductionLog, Long>{

	List<ProductionLog> findByProductionOrderByProductionLogId(Production production);

}
