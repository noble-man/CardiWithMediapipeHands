package be.lilab.uclouvain.cardiammonia.application.authentication.jwt;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import be.lilab.uclouvain.cardiammonia.application.user.User;

public class UserDetailsImpl implements UserDetails {
	private static final long serialVersionUID = 1L;

	private Long id;

	private String username;

	//private String email;

	@JsonIgnore
	private String password;

	@JsonIgnore
	private Boolean enabled;

	private Collection<? extends GrantedAuthority> authorities;

	public UserDetailsImpl(Long id, String username, /*String email,*/ String password, Boolean enabled,
			Collection<? extends GrantedAuthority> authorities) {
		this.id = id;
		this.username = username;
		//this.email = email;
		this.password = password;
		this.enabled = enabled;
		this.authorities = authorities;
	}

	public static UserDetailsImpl build(User user) {
		List<GrantedAuthority> authorities = user.getRole().getPermissions().stream()
				.map(permission -> new SimpleGrantedAuthority(permission.getPermissionId().name()))
				.collect(Collectors.toList());

		return new UserDetailsImpl(
				user.getUserId(), 
				user.getUsername(), 
				//user.getEmail(),
				user.getPassword(), 
				user.getEnabled(),
				authorities);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public Long getId() {
		return id;
	}

/*	public String getEmail() {
		return email;
	}
*/
	
	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return isEnabled();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;//this.enabled;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UserDetailsImpl user = (UserDetailsImpl) o;
		return Objects.equals(id, user.id);
	}
}
