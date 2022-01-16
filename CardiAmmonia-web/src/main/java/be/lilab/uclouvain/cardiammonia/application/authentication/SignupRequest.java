package be.lilab.uclouvain.cardiammonia.application.authentication;

import java.util.Set;

import javax.validation.constraints.*;
 
public class SignupRequest {
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;
 
    /*@NotBlank
    @Size(max = 50)
    @Email
    private String email;
    */
    
//    private String roleId;
    private Role role;
    
    @NotBlank
    @Size(min = 6, max = 40)
    private String password;
  
    public SignupRequest() {}
    
    public SignupRequest(@NotBlank @Size(min = 3, max = 20) String username,
			@NotBlank @Size(min = 6, max = 40) String password, Role role) {
    	//System.out.println(username+"/"+ password+"/"+ roleId);
		this.username = username;
		this.role = role;
		//this.roleId = roleId;
		//this.role = ERole.valueOf(role);
		this.password = password;
	}

	public String getUsername() {
        return username;
    }
 
    public void setUsername(String username) {
        this.username = username;
    }
 
/*    public String getEmail() {
        return email;
    }
 
    public void setEmail(String email) {
        this.email = email;
    }
 */
    public String getPassword() {
        return password;
    }
 
    public void setPassword(String password) {
        this.password = password;
    }
    
/*    public String getRoleId() {
      return this.roleId;
    }
    
    public void setRole(String roleId) {
      this.roleId = roleId;
    }
*/
    public Role getRole() {
    	return role;//new Role(ERole.valueOf(roleId),"");
    }
    public void setRole(Role role) {
    	this.role = role;
    }
}
