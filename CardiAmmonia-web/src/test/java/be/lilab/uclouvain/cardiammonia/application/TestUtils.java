package be.lilab.uclouvain.cardiammonia.application;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TestUtils {
    public static ResultActions registerUser(MockMvc mvc, String username, String password, String[] roles) throws Exception{
        String token = getJWTToken(mvc, "admin","12345678");

    	Optional<String> rolesStr = Arrays.stream(roles).map(a-> "{\"roleId\":\""+a+"\", \"description\":\"\"}").reduce((String a,String b)->a+","+b);
    	String body = "{\"username\":\"" + username + "\", \"password\":\""
                 + password + "\", \"roles\":["+rolesStr.get()+"]}";

    	return mvc.perform(MockMvcRequestBuilders.post("/api/auth/registeruser")
    		  .contentType(MediaType.APPLICATION_JSON)
              .header("Authorization", "Bearer " + token)
              .content(body));
              
    }

    public static String getJWTToken(MockMvc mvc, String username, String password) throws Exception{
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
    
    public static void createTestUsers(MockMvc mvc) throws Exception {
    	registerUser(mvc, "nurse","nurse_pw",new String[] {"ROLE_NURSE"}).andExpect(status().isOk()).andReturn();
    	registerUser(mvc, "technician","technician_pw",new String[] {"ROLE_TECHNICIAN"}).andExpect(status().isOk()).andReturn();      
    }
    
    public static ResultActions post(MockMvc mvc, String token, String url, String body) throws Exception {
    	return  mvc.perform(MockMvcRequestBuilders.post(url)
                .header("Authorization", "Bearer " + token)
	    		 .contentType(MediaType.APPLICATION_JSON)
	            .content(body));
    }

    public static ResultActions get(MockMvc mvc, String token,  String url) throws Exception {
    	return  mvc.perform(MockMvcRequestBuilders.get(url)
                .header("Authorization", "Bearer " + token)
	    		 .contentType(MediaType.APPLICATION_JSON));
    }
    public static ResultActions put(MockMvc mvc,  String token, String url, String body) throws Exception {
    	return  mvc.perform(MockMvcRequestBuilders.put(url)
                .header("Authorization", "Bearer " + token)
	    		 .contentType(MediaType.APPLICATION_JSON)
	            .content(body));
    }
    public static ResultActions delete(MockMvc mvc,  String token, String url) throws Exception {
    	return  mvc.perform(MockMvcRequestBuilders.put(url)
                .header("Authorization", "Bearer " + token)
	    		 .contentType(MediaType.APPLICATION_JSON));
    }
}
