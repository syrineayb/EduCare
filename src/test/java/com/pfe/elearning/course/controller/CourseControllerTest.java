package com.pfe.elearning.course.controller;

import com.pfe.elearning.course.dto.CourseRequest;
import com.pfe.elearning.course.dto.CourseResponse;
import com.pfe.elearning.course.service.CourseService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CourseControllerTest {

    @Mock
    private CourseService courseService;

    @InjectMocks
    private CourseController courseController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(courseController).build();
    }

    @Test
    @DisplayName("Test Enroll in Course Endpoint - Failure")
    public void testEnrollInCourseEndpoint_Failure() throws Exception {
        // Mocking service method behavior to throw an exception
        when(courseService.enrollInCourse(any(Integer.class), any(Integer.class)))
                .thenThrow(new EntityNotFoundException("Course not found"));

        // Perform POST request to enroll in course
        mockMvc.perform(post("/api/courses/courses/enroll")
                        .param("userId", "1")
                        .param("courseId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Expecting 404 status code
    }

}
