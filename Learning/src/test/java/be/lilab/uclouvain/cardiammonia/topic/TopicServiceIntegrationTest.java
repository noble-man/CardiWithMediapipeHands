package be.lilab.uclouvain.cardiammonia.topic;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import be.lilab.uclouvain.cardiammonia.learning.topic.Topic;
import be.lilab.uclouvain.cardiammonia.learning.topic.TopicRepository;
import be.lilab.uclouvain.cardiammonia.learning.topic.TopicService;

@ExtendWith(SpringExtension.class)
public class TopicServiceIntegrationTest {
	
	@TestConfiguration
    static class TopicServiceImplTestContextConfiguration {
 
        @Bean
        public TopicService employeeService() {
            return new TopicService();
        }
    }
 
    @Autowired
    private TopicService topicService;
 
    @MockBean
    private TopicRepository topicRepository;
 
    // write test cases here
    @BeforeEach
	public void setUp() {
	    Topic topic = new Topic("java", "Java name", "Java description");
	 
	    Mockito.when(topicRepository.findById(topic.getId()))
	      .thenReturn(Optional.of(topic));
	}
    
    @Test
	public void whenValidName_thenEmployeeShouldBeFound() {
	    String id = "java";
	    Topic found = topicService.getTopic(id);
	 
	    assertThat(found.getId())
	      .isEqualTo(id);
	 }

}
