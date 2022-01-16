package be.lilab.uclouvain.cardiammonia.application.authentication;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

@Entity
@Table(name = "roles")
public class Role {
	@Id
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Enumerated(EnumType.STRING)
	private ERole roleId;

	@Column(length = 20)
	private String description;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(	name = "role_permission", 
				joinColumns = @JoinColumn(name = "role_id"),
				inverseJoinColumns = @JoinColumn(name = "permission_id"))
	private Set<Permission> permissions = new HashSet<>();
	
	
	public Role() {

	}

	public Role(ERole roleId, String description, Set<Permission> permissions) {
		this.description = description;
		this.roleId = roleId;
		this.permissions = permissions;
	}

	public ERole getRoleId() {
		return roleId;
	}

	public void setRoleId(ERole roleId) {
		this.roleId = roleId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
	
	public Set<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(Set<Permission> permissions) {
		this.permissions = permissions;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Role) {
			Role objRole = (Role)obj;
			return objRole.roleId==this.roleId;
		}
		return false;
	}
	@Override
	public int hashCode() {
		if (roleId==null)
			return 0;
		return this.roleId.hashCode();
	}
}