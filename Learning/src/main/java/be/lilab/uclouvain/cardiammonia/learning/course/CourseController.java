package be.lilab.uclouvain.cardiammonia.learning.course;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import be.lilab.uclouvain.cardiammonia.learning.topic.Topic;

@RestController() //makes anything a rest controller, every time you build a class and add this on top of it
public class CourseController {

    @Autowired // it marks the courseService as something that needs dependency inj.
    private CourseService courseService;// To create a service you need a private courseService variable

    //GETALL
    @RequestMapping("/topics/{id}/courses")
    public List<Course> getAllcourses(@PathVariable String id){
        return courseService.getAllCourses(id);  //getAllCourses for the topic ID
    }
    //GET
    @RequestMapping("/topics/{topicId}/courses/{id}") 

    public Course getCourse(@PathVariable String id) {
        return courseService.getCourse(id);
    }

    //POST
    @RequestMapping(method = RequestMethod.POST, value = "/topics/{topicId}/courses")
    public void addCourse(@RequestBody Course course, @PathVariable String topicId) { 
        course.setTopic(new Topic(topicId, "", ""));
        courseService.addCourse(course);
    }

    //PUT
    @RequestMapping(method = RequestMethod.PUT, value = "/topics/{topicId}/courses/{id}")
    public void updateCourse(@RequestBody Course course,  @PathVariable String id,  @PathVariable String topicId) { 
        course.setTopic(new Topic(topicId, "", ""));
        courseService.updateCourse(course);

    }
    //DELETE
    @RequestMapping(method = RequestMethod.DELETE, value = "/topics/{topicId}/courses/{id}")
    public void deletecourse(@PathVariable String id, @PathVariable String topicId) {
        courseService.deleteCourse(id);

    }
}
 