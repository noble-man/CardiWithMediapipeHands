package be.lilab.uclouvain.cardiammonia.application.user;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import be.lilab.uclouvain.cardiammonia.application.authentication.MessageResponse;
import be.lilab.uclouvain.cardiammonia.application.authentication.Role;
import be.lilab.uclouvain.cardiammonia.application.authentication.RoleRepository;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

	@Autowired
	private UserService userService;

	@PreAuthorize("hasAuthority('MANAGE_USERS')")
	@RequestMapping("/api/users")
	public List<User> getAllUsers() {
		List<User> users = userService.getAllUsers();
		//users.forEach(user-> user.setRole(null));
		return users;
	}
	
	@RequestMapping("/api/users/{id}")
	@PreAuthorize("hasAuthority('MANAGE_USERS')")
	public ResponseEntity<User> getUser(@PathVariable Long id) {
		try {
			Optional<User> foundUser = userService.getUser(id);
            if (foundUser.isPresent()) {
                return ResponseEntity.status(HttpStatus.OK).body(foundUser.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

	}

/*	@RequestMapping(method=RequestMethod.POST, value="/api/users")
	@PreAuthorize("hasRole('ADMIN')")
	public void addUser(@RequestBody User user) {
		
		This method is implemented in the AuthController class
		
		//userService.addUser(user);
	}
*/
	@Autowired
	PasswordEncoder encoder;

	@RequestMapping(method=RequestMethod.PUT, value="/api/users/{id}")
	@PreAuthorize("hasAuthority('MANAGE_USERS')")
	public ResponseEntity<?>  updateUser(@RequestBody User user, @PathVariable Long id) {
		User currentUser = userService.getUser(id).get();
		if (currentUser.getUserId()==user.getUserId()) {
			user.setUsername(currentUser.getUsername());//Cannot change the username
			user.setPassword(encoder.encode(user.getPassword()));
			userService.updateUser(id, user);
			return ResponseEntity.ok(new MessageResponse("User updated successfully!"));

		}
		else {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Username is already taken!"));

		}
		
	}

	@RequestMapping(method=RequestMethod.DELETE, value="/api/users/{id}")
	@PreAuthorize("hasAuthority('MANAGE_USERS')")
	public void deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
	}   

	
	@Autowired
	private RoleRepository roleRepository;

	@RequestMapping("/api/users/roles")//Security for this request must be handled in the SecurityWebConfig, on the request level 
	//@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public List<Role> getRolesList() {
		return roleRepository.findAll();
	}

}
