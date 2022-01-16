package be.lilab.uclouvain.cardiammonia.learning.course;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import be.lilab.uclouvain.cardiammonia.learning.topic.Topic;

@Entity

public class Course {
    // here generate constructors and getters/setters
    @Id
    private String id;
    private String name;
    private String description;

    @ManyToOne
    private Topic topic; //use it to tie this class to the Topic class, to make it easier for the user

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public Course() {
    }

    public Course(String id, String name, String description, String topicId) {
        super();
        this.id = id;
        this.name = name;
        this.description = description;
        this.topic = new Topic(topicId,"","");
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
