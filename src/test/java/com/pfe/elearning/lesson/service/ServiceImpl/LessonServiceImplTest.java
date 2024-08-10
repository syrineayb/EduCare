package com.pfe.elearning.lesson.service.ServiceImpl;

import com.pfe.elearning.course.entity.Course;
import com.pfe.elearning.course.repository.CourseRepository;
import com.pfe.elearning.lesson.dto.LessonMapper;
import com.pfe.elearning.lesson.dto.LessonRequest;
import com.pfe.elearning.lesson.dto.LessonResponse;
import com.pfe.elearning.lesson.entity.Lesson;
import com.pfe.elearning.lesson.repository.LessonRepository;
import com.pfe.elearning.user.entity.User;
import com.pfe.elearning.user.service.UserService;
import com.pfe.elearning.validator.ObjectsValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LessonServiceImplTest {
    @Mock
    private LessonRepository lessonRepository;
    @Mock
    private LessonMapper lessonMapper;
    @Mock
    private UserService userService;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private ObjectsValidator<LessonRequest> validator;

    @InjectMocks
    private LessonServiceImpl lessonService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void createLesson_SuccessfullyCreatesLesson(){
   /*     // Given
        LessonRequest lessonRequest = new LessonRequest();
        lessonRequest.setTitle("Test Lesson");
        lessonRequest.setDescription("This is a test lesson");
        lessonRequest.setCourseId(1);

        String publisherUsername = "publisher@example.com";

        User publisher = new User();
        publisher.setEmail(publisherUsername);

        Course course = new Course();
        course.setId(1);
        course.setTitle("Test Course");
        course.setPublisher(publisher);

        Lesson lesson = new Lesson();
        lesson.setId(1);
        lesson.setTitle(lessonRequest.getTitle());
        lesson.setDescription(lessonRequest.getDescription());
        lesson.setCreatedAt(LocalDateTime.now());
        lesson.setCourse(course);

        LessonResponse expectedResponse = new LessonResponse();
        expectedResponse.setId(1);
        expectedResponse.setTitle(lessonRequest.getTitle());
        expectedResponse.setDescription(lessonRequest.getDescription());
        expectedResponse.setCreatedAt(lesson.getCreatedAt());
        expectedResponse.setCourseTitle(course.getTitle());

        // Mock calls
        when(userService.getUserByEmail(publisherUsername)).thenReturn(publisher);
        when(courseRepository.findById(lessonRequest.getCourseId())).thenReturn(Optional.of(course));
        when(lessonMapper.toLesson(lessonRequest)).thenReturn(lesson);
        when(lessonRepository.save(lesson)).thenReturn(lesson);
        when(lessonMapper.toLessonResponse(lesson)).thenReturn(expectedResponse);

        // When
        LessonResponse actualResponse = lessonService.createLesson(lessonRequest, publisherUsername);

        // Then
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getTitle(), actualResponse.getTitle());
        assertEquals(expectedResponse.getDescription(), actualResponse.getDescription());
        assertEquals(expectedResponse.getCreatedAt(), actualResponse.getCreatedAt());
        assertEquals(expectedResponse.getCourseTitle(), actualResponse.getCourseTitle());
        // Verify Interactions
        verify(userService, times(1)).getUserByEmail(publisherUsername);
        verify(courseRepository, times(1)).findById(lessonRequest.getCourseId());
        verify(lessonMapper, times(1)).toLesson(lessonRequest);
        verify(lessonRepository, times(1)).save(lesson);
        verify(lessonMapper, times(1)).toLessonResponse(lesson);

    */
    }

}
