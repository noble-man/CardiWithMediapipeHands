package be.lilab.uclouvain.cardiammonia.learning.course;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseService {

    @Autowired // it injects the courseRepository as it's initialized
    private CourseRepository courseRepository;

    public List<Course> getAllCourses(String topicId){ //getting all is not good!
        List<Course> courses = new ArrayList<>() ;
        courseRepository.findByTopicId(topicId).forEach(courses::add);
        return courses;
    }

    public Course getCourse(String id) {
        return courseRepository.findById(id).get();
    }

    public void addCourse(Course course) {
        courseRepository.save(course); //save a course to the database
            }

    public void updateCourse(Course course) {
        courseRepository.save(course);
        //save does both add and update
        }


    public void deleteCourse(String id) {
        courseRepository.deleteById(id);
    }
}
