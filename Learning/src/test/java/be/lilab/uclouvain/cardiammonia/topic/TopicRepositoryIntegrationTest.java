package be.lilab.uclouvain.cardiammonia.topic;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import be.lilab.uclouvain.cardiammonia.learning.topic.Topic;
import be.lilab.uclouvain.cardiammonia.learning.topic.TopicRepository;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class TopicRepositoryIntegrationTest {

	@Autowired
    private TestEntityManager entityManager;
 
    @Autowired
    private TopicRepository employeeRepository;
 
    // write test cases here
    @Test
	public void whenFindById_thenReturnTopic() {
	    // given
	    Topic java = new Topic("java","Java course", "java description");
	    entityManager.persist(java);
	    entityManager.flush();
	 
	    // when
	    Topic found = employeeRepository.findById(java.getId()).get();
	 
	    // then
	    assertThat(found.getName())
	      .isEqualTo(java.getName());
	} 
}
