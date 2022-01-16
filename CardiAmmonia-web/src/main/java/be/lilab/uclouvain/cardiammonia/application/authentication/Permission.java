package be.lilab.uclouvain.cardiammonia.application.authentication;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

@Entity
public class Permission {

	@Id
 	@Enumerated(EnumType.STRING)
	private EPermission permissionId;
	@Column(length = 200)
	private String description;
	

	public Permission() {

	}

	public Permission(EPermission permissionId, String description) {
		this.description = description;
		this.permissionId = permissionId;
	}
	
	public EPermission getPermissionId() {
		return permissionId;
	}

	public void setPermissionId(EPermission permissionId) {
		this.permissionId = permissionId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Permission) {
			Permission objRole = (Permission)obj;
			return objRole.permissionId==this.permissionId;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		if (permissionId==null)
			return 0;
		return this.permissionId.hashCode();
	}
}