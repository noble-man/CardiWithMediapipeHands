package be.lilab.uclouvain.cardiammonia.learning.topic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TopicService {
	
	@Autowired
	private TopicRepository topicRepository;
	
	/*private List<Topic> topics = new ArrayList<>(Arrays.asList(
		new Topic("spring","Spring Framework", "Spring Framework Description"),
		new Topic("java","Core Java", "Core Java Description"),
		new Topic("javascript","Javascript", "Javascript Description")
	));*/
	
	public List<Topic> getAllTopics() {
		List<Topic> topics = new ArrayList<>();
		topicRepository.findAll().forEach(topics::add);
		return topics;
	}
	
	public Topic getTopic(String id) {
		//return topics.stream().filter(a->a.getId().equals(id)).findFirst().get();
		return topicRepository.findById(id).get();
	}

	public void addTopic(Topic topic) {
		//topics.add(topic);
		topicRepository.save(topic);
	}

	public void updateTopic(String id, Topic topic) {
		topicRepository.save(topic);
/*		for (int i=0; i<topics.size(); i++) {
			Topic current = topics.get(i); 
			if (current.getId().equals(id)) {
				topics.set(i, topic);
				return;
			}
		}*/
	}

	public void deleteTopic(String id) {
		topicRepository.deleteById(id);
		//topics.removeIf(t->t.getId().equals(id));
		
	}
}
