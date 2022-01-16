package be.lilab.uclouvain.cardiammonia.topic;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.BDDMockito.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import be.lilab.uclouvain.cardiammonia.learning.topic.Topic;
import be.lilab.uclouvain.cardiammonia.learning.topic.TopicController;
import be.lilab.uclouvain.cardiammonia.learning.topic.TopicService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TopicController.class)
class TopicControllerUnitTest {

    @Autowired
    private MockMvc mvc;
    
    @MockBean
    private TopicService topicService;
    
/*    @Test
	public void givenEmployees_whenGetEmployees_thenReturnJsonArray()
	  throws Exception {
	    
	    Employee alex = new Employee("alex");
	 
	    List<Employee> allEmployees = Arrays.asList(alex);
	 
	    given(service.getAllEmployees()).willReturn(allEmployees);
	 
	    mvc.perform(get("/api/employees")
	      .contentType(MediaType.APPLICATION_JSON))
	      .andExpect(status().isOk())
	      .andExpect(jsonPath("$", hasSize(1)))
	      .andExpect(jsonPath("$[0].name", is(alex.getName())));
	}
*/
    
	@Test
	void givenTopics_whenGetAllTopics_thenReturnJsonArray() throws Exception {
		Topic javaTopic = new Topic("Java", "The Java Topic", "Description of the Java Topic");
		List<Topic> allTopics = java.util.Arrays.asList(javaTopic);
		
		given(topicService.getAllTopics()).willReturn(allTopics);

		mvc.perform(get("/topics").contentType(MediaType.APPLICATION_JSON))
	      .andExpect(status().isOk())
	      .andExpect(jsonPath("$", hasSize(allTopics.size())))
	      .andExpect(jsonPath("$[0].name", is(allTopics.get(0).getName()))
	    );
    }

	@Test
	void givenTopic_whenGetTopic_thenReturnJsonTopic() throws Exception {
		Topic javaTopic = new Topic("Java", "The Java Topic", "Description of the Java Topic");
		
		given(topicService.getTopic("Java")).willReturn(javaTopic);

		mvc.perform(get("/topics/Java").contentType(MediaType.APPLICATION_JSON))
	      .andExpect(status().isOk())
	      .andExpect(jsonPath("$.id", is(javaTopic.getId()))
	    );
	}

	
/*	private byte[] toJson(Topic topic) throws JsonProcessingException {
		//Creating the ObjectMapper object
		ObjectMapper mapper = new ObjectMapper();
		//Converting the Object to JSONString
		return mapper.writeValueAsBytes(topic);
	}

	@Test
	void testUpdateTopic() {
		
	}

	@Test
	void testDeleteTopic() {
		fail("Not yet implemented");
	}
*/
}
