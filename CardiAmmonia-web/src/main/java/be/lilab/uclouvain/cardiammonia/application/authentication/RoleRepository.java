package be.lilab.uclouvain.cardiammonia.application.authentication;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
	Optional<Role> findByDescription(String description);
	Optional<Role> findByRoleId(ERole roleId);
}
