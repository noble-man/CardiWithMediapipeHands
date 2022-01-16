package be.lilab.uclouvain.cardiammonia.application.production;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductionRepository extends JpaRepository<Production, String>{

}
