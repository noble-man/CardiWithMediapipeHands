package be.lilab.uclouvain.cardiammonia.application.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);
	Optional<User> findById(Long userId);

	void deleteById(Long userId);
	Boolean existsByUsername(String username);

	//Boolean existsByEmail(String email);
}
