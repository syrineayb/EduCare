package com.pfe.elearning.lesson.dto;

import com.pfe.elearning.course.entity.Course;
import com.pfe.elearning.lesson.entity.Lesson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LessonMapperTest {/*
    private LessonMapper lessonMapper;
    @BeforeEach
    void setUp() {
        this.lessonMapper = new LessonMapper();
    }
    @Test
    public void testToLessonResponse() {
        // Create a Lesson object
        Lesson lesson = new Lesson();
        lesson.setId(1);
        lesson.setTitle("Test Lesson");
        lesson.setDescription("This is a test lesson");
        lesson.setCreatedAt(LocalDateTime.now());
        Course course = new Course();
        course.setId(1);
        course.setTitle("Test Course");
        lesson.setCourse(course);

        // Call toLessonResponse method
        LessonResponse lessonResponse = lessonMapper.toLessonResponse(lesson);

        // Assertions
        assertNotNull(lessonResponse);
        assertEquals(lesson.getId(), lessonResponse.getId());
        assertEquals(lesson.getTitle(), lessonResponse.getTitle());
        assertEquals(lesson.getDescription(), lessonResponse.getDescription());
        assertEquals(lesson.getCreatedAt(), lessonResponse.getCreatedAt());
        assertEquals(lesson.getCourse().getTitle(), lessonResponse.getCourseTitle());
    }

    @Test
    public void testToLesson() {
        // Create a LessonRequest object
        LessonRequest lessonRequest = new LessonRequest();
        lessonRequest.setTitle("Test Lesson");
        lessonRequest.setDescription("This is a test lesson");
        lessonRequest.setCourseId(1);

        // Call toLesson method
        Lesson lesson = lessonMapper.toLesson(lessonRequest);

        // Assertions
        assertNotNull(lesson);
        assertEquals(lessonRequest.getTitle(), lesson.getTitle());
        assertEquals(lessonRequest.getDescription(), lesson.getDescription());
        assertNotNull(lesson.getCourse());
        assertEquals(lessonRequest.getCourseId(), lesson.getCourse().getId());
    }

    */
}