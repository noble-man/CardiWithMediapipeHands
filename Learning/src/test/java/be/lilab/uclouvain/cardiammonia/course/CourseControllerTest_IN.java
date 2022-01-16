package be.lilab.uclouvain.cardiammonia.course;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;

import be.lilab.uclouvain.cardiammonia.learning.course.CourseController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CourseController.class)
class CourseControllerTest_IN {

    @Autowired
    private MockMvc mvc;
    
	@Test
	void testGetAllcourses() throws Exception {
        RequestBuilder request = get("/hello");
        MvcResult result = mvc.perform(request).andReturn();
        assertEquals("Hello, World", result.getResponse().getContentAsString());
    }

	@Test
	void testGetCourse() throws Exception {
        mvc.perform(get("/hello?name=Dan"))
        .andExpect(content().string("Hello, Dan"));
	}

	@Test
	void testAddCourse() {
		fail("Not yet implemented");
	}

	@Test
	void testUpdateCourse() {
		fail("Not yet implemented");
	}

	@Test
	void testDeletecourse() {
		fail("Not yet implemented");
	}

}
