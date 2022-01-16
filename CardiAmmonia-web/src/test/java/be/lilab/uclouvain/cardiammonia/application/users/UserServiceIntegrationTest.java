package be.lilab.uclouvain.cardiammonia.application.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import be.lilab.uclouvain.cardiammonia.application.RunSimulatorsExtenstion;
import be.lilab.uclouvain.cardiammonia.application.authentication.AuthController;
import be.lilab.uclouvain.cardiammonia.application.authentication.ERole;
import be.lilab.uclouvain.cardiammonia.application.authentication.ProfileChangeRequest;
import be.lilab.uclouvain.cardiammonia.application.authentication.Role;
import be.lilab.uclouvain.cardiammonia.application.user.User;
import be.lilab.uclouvain.cardiammonia.application.user.UserService;

@ExtendWith(SpringExtension.class)
@ExtendWith({RunSimulatorsExtenstion.class})//Required to run the simulators, or else the server will not start and the tests will fail.
//@WebMvcTest(AuthController.class)
//@ComponentScan(basePackages = { "be.lilab.uclouvain.cardiammonia.application"})
@SpringBootTest
@AutoConfigureMockMvc
//@TestPropertySource("file:src/main/resources/application.properties")
public class UserServiceIntegrationTest {
	/*@TestConfiguration
    static class UserServiceImplTestContextConfiguration {
 
        @Bean
        public UserService userService() {
            return new UserService();
        }
    }*/


    @Autowired
    private MockMvc mvc;
    

    @Autowired
    private UserService userService;
 
//    @MockBean
//   private UserRepository userRepository;
 
    
    //@Value("${server.port}")
    //private int portNumber;
    
    @BeforeTestClass
    public void createTestUsers() throws Exception {
    	registerUser("nurse","nurse_pw","ROLE_TECHNOLOGIST").andExpect(status().isOk()).andReturn();
    	registerUser("technician","technician_pw","ROLE_TECHNICIAN").andExpect(status().isOk()).andReturn();      
    }
    public ResultActions registerUser(String username, String password, String role) throws Exception{
        String token = getJWTToken("admin","12345678");

    	//Optional<String> rolesStr = Arrays.stream(roles).map(a-> "{\"roleId\":\""+a+"\", \"description\":\"\"}").reduce((String a,String b)->a+","+b);
    	String rolesStr = "{\"roleId\":\""+role+"\", \"description\":\"\"}";
    	String body = "{\"username\":\"" + username + "\", \"password\":\""
                 + password + "\", \"role\":"+rolesStr+"}";

    	return mvc.perform(MockMvcRequestBuilders.post("/api/auth/registeruser")
    		  .contentType(MediaType.APPLICATION_JSON)
              .header("Authorization", "Bearer " + token)
              .content(body));
              
    }
    
    public String getJWTToken(String username, String password) throws Exception{
         String body = "{\"username\":\"" + username + "\", \"password\":\""
                + password + "\"}";

	     MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
	    		 .contentType(MediaType.APPLICATION_JSON)
	            .content(body))
	            .andExpect(status().isOk()).andReturn();
	
	     String response = result.getResponse().getContentAsString();
	        ObjectMapper objectMapper = new ObjectMapper();
	        Map<?, ?> map = objectMapper.readValue(response,Map.class );
	        return map.get("accessToken").toString();
	        	
    }
    
/*    @Test
    public void testAuthentication() throws Exception {
    	assertThat(this.getJWTToken("nurse", "nurse_pw")).isNotNull();
    }
    */
    @Test
    public void testAdminSection() throws Exception {

       String token = getJWTToken("admin","12345678");

       mvc.perform(MockMvcRequestBuilders.get("/api/auth/test")
          .header("Authorization", "Bearer " + token))
          .andExpect(status().isOk());
          
    }
    
/*    @Test
    public void testNurseSection() throws Exception {
    	//registerUser("nurse","nurse_pw",new String[] {"ROLE_NURSE", "ROLE_USER"}).andExpect(status().isOk()).andReturn();;

       String token = getJWTToken("nurse","12345678");

       mvc.perform(MockMvcRequestBuilders.get("/api/auth/test")
          .header("Authorization", "Bearer " + token))
          .andExpect(status().isOk());
    }
*/
    @Test
    public void testRejectAddingSameUserTwice() throws Exception {
    	registerUser("user1","pw1","ROLE_TECHNOLOGIST").andExpect(status().isOk()).andReturn();;
    	registerUser("user1","pw1","ROLE_ADMIN").andExpect(status().isBadRequest()).andReturn();;
    }

    @Test
    public void testCurrentUserCanUpdateProfile() throws Exception {
    	
    	//create a user
    	registerUser("somebody","somebody_pw","ROLE_TECHNICIAN").andExpect(status().isOk()).andReturn();;
    	//Login with the user
        String token = getJWTToken("somebody","somebody_pw");
    	//call /api/auth/profile to update the user's password, using PUT method
        
        ProfileChangeRequest profileChangeRequest = new ProfileChangeRequest();
        profileChangeRequest.setPassword("new_pwd");
        ObjectMapper objectMapper = new ObjectMapper();
        
        
        mvc.perform(MockMvcRequestBuilders.put("/api/auth/profile")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(profileChangeRequest)))       		
                .andExpect(status().isOk());

        //login again with the new password
        String token2 = getJWTToken("somebody","new_pwd");
        assertThat(token2).isNotNull();
    	//Test login has succeeded
        
    }
    
    private User getUser(String token, Long userId ) throws Exception {

        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/api/users/"+userId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        
	     String response = result.getResponse().getContentAsString();
	        ObjectMapper objectMapper = new ObjectMapper();
	        return objectMapper.readValue(response,User.class);
    }
    private User getUser(String token, String username ) throws Exception {

        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/api/users")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        
	     String response = result.getResponse().getContentAsString();
	        ObjectMapper objectMapper = new ObjectMapper();
	        User[] userList = objectMapper.readValue(response,User[].class);
	        
	        return Arrays.stream(userList).filter(a->a.getUsername().equals(username)).findFirst().get();
	        
    }
    @Test
    public void testAdminUpdatesUserCanUpdateProfile() throws Exception {
    	//Create a user
    	registerUser("testUser","testUser_pw", "ROLE_TECHNOLOGIST").andExpect(status().isOk()).andReturn();;

    	String token = getJWTToken("admin","12345678");//work as admin
    	//Get the user
    	User testUser = getUser(token, "testUser");
    	//Update the user: username, password and roles.
    	String oldpwd = testUser.getUsername();
    	testUser.setUsername("updatedUser");
    	testUser.setPassword("updatedPassword");
    	Role role = new Role(ERole.ROLE_ADMIN, "Administrator", null);
    	testUser.setRole(role);

        ObjectMapper objectMapper = new ObjectMapper();
        String serializedTestUser = objectMapper.writeValueAsString(testUser);
        mvc.perform(MockMvcRequestBuilders.put("/api/users/"+testUser.getUserId())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
	            .content(serializedTestUser))       		
                .andExpect(status().isOk());

    	//Get the user: expected: the user name is not changed. The password and the roles are updated.
    	User testUpdatedUser = getUser(token, testUser.getUserId());
    	
    	assertEquals("testUser", testUpdatedUser.getUsername());
    	assertNotEquals(oldpwd, testUpdatedUser.getPassword());
    	assertEquals(role, testUpdatedUser.getRole());
    		//Check password is changed by trying to signup
    	assertNotNull(getJWTToken("testUser","updatedPassword"));
        //Delete the user
        mvc.perform(MockMvcRequestBuilders.delete("/api/users/"+testUser.getUserId())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        
        //Get the user: expected a not found header status is returned
        mvc.perform(MockMvcRequestBuilders.get("/api/users/"+testUser.getUserId())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
 
}
