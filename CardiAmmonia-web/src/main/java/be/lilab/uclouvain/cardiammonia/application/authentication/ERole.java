package be.lilab.uclouvain.cardiammonia.application.authentication;

public enum ERole {
	ROLE_VISITOR, //Can sign in to browse general content. Could be given to a doctor to monitor
	ROLE_RADIOPHARMACIST, // Can start/pause and resume a production. 
    ROLE_ADMIN, //Can browse production parameters. 
    ROLE_TECHNOLOGIST, // Can browse QC reports and other production parameters as a radio pharmacist.
    ROLE_TECHNICIAN // Can create users and assign roles.
}
