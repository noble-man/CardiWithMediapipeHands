package be.lilab.uclouvain.cardiammonia.application.authentication;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import be.lilab.uclouvain.cardiammonia.application.authentication.jwt.JwtResponse;
import be.lilab.uclouvain.cardiammonia.application.authentication.jwt.JwtUtils;
import be.lilab.uclouvain.cardiammonia.application.authentication.jwt.UserDetailsImpl;
import be.lilab.uclouvain.cardiammonia.application.user.User;
import be.lilab.uclouvain.cardiammonia.application.user.UserService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserService userService;
	//UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();		
		List<String> permissions = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());
		Optional<User> opRole = userService.getUser(userDetails.getId());
		return ResponseEntity.ok(new JwtResponse(jwt, 
												 userDetails.getId(), 
												 userDetails.getUsername(), 
												 //userDetails.getEmail(), 
												 opRole.get().getRole())
												 );
	}

	@PostMapping("/updateUserByAdmin")
	@PreAuthorize("hasAuthority('MANAGE_USERS')")
	public ResponseEntity<?> updateUserByAdmin(@Valid @RequestBody User user) {
		Optional<User> dbUser = userService.getUser(user.getUserId());
		if (!dbUser.isPresent()) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Username Does not exist!"));
		}

		// Update the user's account
		User foundUser = dbUser.get();
		foundUser.setEnabled(user.getEnabled());
		foundUser.setUsername(user.getUsername());
		foundUser.setRole(user.getRole());
		foundUser.setPassword(user.getPassword());


		userService.updateUser(foundUser.getUserId(), foundUser);
		return ResponseEntity.ok(new MessageResponse("The user is updated successfully!"));
	}
	
	@PostMapping("/registeruser")
	@PreAuthorize("hasAuthority('MANAGE_USERS')")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		if (userService.getUser(signUpRequest.getUsername()).isPresent()) {
				//userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Username is already taken!"));
		}

		// Create new user's account
		User user = new User(signUpRequest.getUsername(), 
							 //signUpRequest.getEmail(),
							 encoder.encode(signUpRequest.getPassword()),
							 signUpRequest.getRole(),
							 true);
		userService.addUser(user);
		return ResponseEntity.ok(new MessageResponse("The user is registered successfully!"));
	}
	
	/**
	 * Update the current user's profile. The profile is defined in the class ProfileChangeRequest. 
	 * Currently, the profile contains the password only. It is subject to be modified in the future.
	 * @param principal
	 * @param profile
	 */
	@RequestMapping(method=RequestMethod.PUT, value="/profile")
	//@PreAuthorize("hasAuthority('MANAGE_PROFILE')")
	public void updateUser(Principal principal, @RequestBody ProfileChangeRequest profile) {
		User currentUser = userService.getUser(principal.getName()).get();
		currentUser.setPassword(encoder.encode(profile.getPassword()));
		userService.updateUser(currentUser.getUserId(), currentUser);
	}

	/**
	 * A test method for the connection of the admin
	 * @param principal
	 */
	@RequestMapping(method=RequestMethod.GET, value="/test")
	@PreAuthorize("hasAuthority('MANAGE_USERS')")
	public ResponseEntity<?> testConncetion(Principal principal) {
		User currentUser = userService.getUser(principal.getName()).get();
		
		UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();		
		List<String> permissions = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());
		permissions.stream().forEach(System.out::println);

		return ResponseEntity.ok(new MessageResponse(currentUser.getUsername()));
	}

}
