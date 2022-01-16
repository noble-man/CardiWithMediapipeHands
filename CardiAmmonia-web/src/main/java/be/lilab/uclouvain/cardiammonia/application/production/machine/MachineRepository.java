package be.lilab.uclouvain.cardiammonia.application.production.machine;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
	
//@Repository
public interface MachineRepository extends JpaRepository<Machine, String>{

}
