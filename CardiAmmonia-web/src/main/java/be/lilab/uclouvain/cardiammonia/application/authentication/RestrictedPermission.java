package be.lilab.uclouvain.cardiammonia.application.authentication;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class RestrictedPermission {

	@EmbeddedId
    private RestrictedPermissionId restrictedPermissionId;
	
	@Column(length = 20)
	private int restrictionType;

	//@ManyToOne
	//private Role role;
	
	//@ManyToOne
	//private Permission permission;

	public RestrictedPermission() {}
	/**
	 * 
	 * @param restrictedPermissionId: The PK of the entity
	 * @param restrictionType: 1:permission is ok to add to this role,
	 * 						   0: Not recommended
	 * 						   -1: Not possible/Error
	 */
	public RestrictedPermission(RestrictedPermissionId restrictedPermissionId, int restrictionType) {
		super();
		this.restrictionType = restrictionType;
		this.restrictedPermissionId = restrictedPermissionId;
	}

	public int getRestrictionType() {
		return restrictionType;
	}

	/**
	 * 
	 * @param restrictionType: 1:permission is ok to add to this role,
	 * 						   0: Not recommended
	 * 						   -1: Not possible/Error
	 */
	public void setRestrictionType(int restrictionType) {
		this.restrictionType = restrictionType;
	}

	
	public RestrictedPermissionId getRestrictedPermissionId() {
		return restrictedPermissionId;
	}

	public void setRestrictedPermissionId(RestrictedPermissionId restrictedPermissionId) {
		this.restrictedPermissionId = restrictedPermissionId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((restrictedPermissionId == null) ? 0 : restrictedPermissionId.hashCode());
		result = prime * result + restrictionType;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RestrictedPermission other = (RestrictedPermission) obj;
		if (restrictedPermissionId == null) {
			if (other.restrictedPermissionId != null)
				return false;
		} else if (!restrictedPermissionId.equals(other.restrictedPermissionId))
			return false;
		if (restrictionType != other.restrictionType)
			return false;
		return true;
	}


	
}
	