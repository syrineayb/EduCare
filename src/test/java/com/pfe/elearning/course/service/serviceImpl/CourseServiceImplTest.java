package com.pfe.elearning.course.service.serviceImpl;

import com.pfe.elearning.common.PageResponse;
import com.pfe.elearning.course.dto.CourseRequest;
import com.pfe.elearning.course.dto.CourseResponse;
import com.pfe.elearning.course.entity.Course;
import com.pfe.elearning.course.repository.CourseRepository;
import com.pfe.elearning.course.dto.CourseMapper;
import com.pfe.elearning.topic.entity.Topic;
import com.pfe.elearning.topic.repository.TopicRepository;
import com.pfe.elearning.user.entity.User;
import com.pfe.elearning.user.service.UserService;
import com.pfe.elearning.validator.ObjectsValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseServiceImplTest {


    @Mock
    private CourseMapper courseMapper;

    @Mock
    private UserService userService;

    @Mock
    private ObjectsValidator<CourseRequest> validator;


    @Mock
    private CourseRepository courseRepository;
    @Mock
    private TopicRepository topicRepository;

    @InjectMocks
    private CourseServiceImpl courseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
/*
    @Test
    @DisplayName("Update Course - Valid Course")
    void updateCourse_ValidCourse_UpdatesSuccessfully() throws IOException {
        // Mock data
        Integer courseId = 1;
        CourseRequest courseRequest = new CourseRequest();
        courseRequest.setTitle("Updated Title");
        courseRequest.setDescription("Updated Description");
        courseRequest.setStartDate(LocalDate.of(2024, 6, 1));
        courseRequest.setEndDate(LocalDate.of(2024, 6, 30));
        courseRequest.setTimeCommitment("10 hours");

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "updated-image.jpg",
                "image/jpeg",
                Files.readAllBytes(Paths.get("path/to/your/updated-image.jpg"))
        );

        Course existingCourse = new Course();
        existingCourse.setId(courseId);
        existingCourse.setTitle("Old Title");
        existingCourse.setDescription("Old Description");
        existingCourse.setImageFile("old-image.jpg");
        existingCourse.setStartDate(LocalDate.of(2024, 5, 1));
        existingCourse.setEndDate(LocalDate.of(2024, 5, 30));
        existingCourse.setTimeCommitment("5 hours");

        CourseResponse expectedResponse = new CourseResponse();
        expectedResponse.setId(courseId);
        expectedResponse.setTitle(courseRequest.getTitle());
        expectedResponse.setDescription(courseRequest.getDescription());
        expectedResponse.setImageFile("updated-image.jpg");
        expectedResponse.setStartDate(courseRequest.getStartDate());
        expectedResponse.setEndDate(courseRequest.getEndDate());
        expectedResponse.setTimeCommitment(courseRequest.getTimeCommitment());

        // Mock behavior
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(existingCourse));
        when(courseRepository.save(any(Course.class))).thenReturn(existingCourse);
        when(courseMapper.toCourseResponse(any(Course.class))).thenReturn(expectedResponse);

        // Method call
        CourseResponse actualResponse = courseService.updateCourse(courseId, courseRequest, file);

        // Verify
        verify(courseRepository, times(1)).findById(courseId);
        verify(courseRepository, times(1)).save(existingCourse);
        verify(courseMapper, times(1)).toCourseResponse(existingCourse);

        // Assertions
        assertEquals(expectedResponse, actualResponse);
        assertEquals(courseRequest.getTitle(), existingCourse.getTitle());
        assertEquals(courseRequest.getDescription(), existingCourse.getDescription());
        assertEquals("updated-image.jpg", existingCourse.getImageFile());
        assertEquals(courseRequest.getStartDate(), existingCourse.getStartDate());
        assertEquals(courseRequest.getEndDate(), existingCourse.getEndDate());
        assertEquals(courseRequest.getTimeCommitment(), existingCourse.getTimeCommitment());
    }


 */
    @Test
    @DisplayName("Update Course - Invalid Course ID")
    void updateCourse_InvalidCourseId_ThrowsEntityNotFoundException() {
        // Mock data
        Integer courseId = 1;
        CourseRequest courseRequest = new CourseRequest();

        // Mock behavior
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // Method call and assertion
        assertThrows(EntityNotFoundException.class, () -> {
            courseService.updateCourse(courseId, courseRequest, null);
        });

        // Verify
        verify(courseRepository, times(1)).findById(courseId);
        verify(courseRepository, never()).save(any());
        verify(courseMapper, never()).toCourseResponse(any());
    }
}

    /*
    // Tests for createCourse method
    @Test
    @DisplayName("Create Course with Invalid Publisher")
    void createCourse_InvalidPublisher_ThrowsEntityNotFoundException() {
        // Mock data
        CourseRequest courseRequest = new CourseRequest();
        courseRequest.setTitle("Test Course");
        courseRequest.setDescription("This is a test course");
        courseRequest.setImageUrl("test.jpg");
        courseRequest.setDuration("2 weeks");
        courseRequest.setTopicId(1);

        // Mock behavior to return null for getUserByEmail
        when(userService.getUserByEmail(anyString())).thenReturn(null);

        // Method call and assertions
        assertThrows(EntityNotFoundException.class, () -> {
            courseService.createCourse(courseRequest, "NonExistentUser");
        });

        // Verify that toCourse method is not called
        verify(courseMapper, never()).toCourse(courseRequest);
    }

    @Test
    @DisplayName("Create Course with Invalid Course Request")
    void createCourse_WithInvalidCourseRequest_ThrowsValidationException() {
        // Creating an invalid course request
        CourseRequest invalidCourseRequest = new CourseRequest();
        invalidCourseRequest.setTitle(""); // Empty title
        invalidCourseRequest.setDescription(""); // Empty description
        invalidCourseRequest.setTopicId(null); // Null topic ID
        invalidCourseRequest.setImageUrl(null); // Null photo
        invalidCourseRequest.setDuration(null); // Null duration

        // Mocking the userService to return a valid publisher
        when(userService.getUserByEmail(anyString())).thenReturn(new User());

        // Mocking the validator to throw a ValidationException
        doThrow(new ValidationException("Validation error occurred")).when(validator).validate(invalidCourseRequest);

        // Asserting that a ValidationException is thrown when creating the course
        assertThrows(ValidationException.class, () -> {
            courseService.createCourse(invalidCourseRequest, "validPublisherIdentifier"); // Provide a valid publisher identifier
        });
    }

    @Test
    @DisplayName("Create Course: Mapping and Save")
    void createCourse_CourseMappingAndSave() {
        // Mock data
        CourseRequest courseRequest = new CourseRequest();
        courseRequest.setTitle("Test Course");
        courseRequest.setDescription("This is a test course");
        courseRequest.setImageUrl("test.jpg");
        courseRequest.setDuration("2 weeks");
        courseRequest.setTopicId(1);

        User publisher = new User();
        publisher.setFirstname("Nahla");
        publisher.setLastname("Sassi");
        publisher.setEmail("Nahla@gmail.com");
        Course course = new Course();
        course.setId(1);

        // Mock behavior
        when(userService.getUserByEmail(anyString())).thenReturn(publisher);
        when(courseMapper.toCourse(courseRequest)).thenReturn(course);

        // Method call
        courseService.createCourse(courseRequest, "Nahla Sassi");

        // Verify that toCourse method is called with the correct parameters
        verify(courseMapper, times(1)).toCourse(courseRequest);

        // Verify that save method is called with the correct course object
        verify(courseRepository, times(1)).save(course);
    }
    // Tests for getCourseById method
    @Test
    @DisplayName("Get Course by Valid Course ID - Returns Course Response")
    void getCourseById_ValidCourseId_ReturnsCourseResponse() {
        // Mock data
        Integer courseId = 1;
        Course mockCourse = new Course(); // Create a mock Course object
        CourseResponse expectedResponse = new CourseResponse(); // Create a mock CourseResponse object

        // Mock behavior of courseRepository.findById to return an Optional containing the mockCourse
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(mockCourse));

        // Mock behavior of courseMapper.toCourseResponse to return the expectedResponse
        when(courseMapper.toCourseResponse(mockCourse)).thenReturn(expectedResponse);

        // Call the method under test
        CourseResponse actualResponse = courseService.getCourseById(courseId);

        // Verify that the courseRepository.findById was called with the correct courseId
        // Optional::get is used here because we know the Optional will contain a value due to the mock behavior
            assertEquals(mockCourse, courseRepository.findById(courseId).get());

        // Verify that courseMapper.toCourseResponse was called with the correct course
        assertEquals(expectedResponse, actualResponse);
    }
    @Test
    @DisplayName("Get Course by Nonexistent Course ID - Throws EntityNotFoundException")
    void getCourseById_CourseNotFound_ThrowsEntityNotFoundException() {
        // Test setup
        when(courseRepository.findById((Long) any())).thenReturn(Optional.empty());

        // Method call and assertion
        assertThrows(EntityNotFoundException.class, () -> {
            courseService.getCourseById(1);
        });

        // Verify that courseRepository.findById was called with the correct courseId
        verify(courseRepository, times(1)).findById(1);
    }
    // Tests for updateCourse method
    @Test
    @DisplayName("Update Course")
    void updateCourse_ValidCourseIdAndRequest_UpdatesCourseSuccessfully() {
        // Mock data
        Integer courseId = 1;
        CourseRequest courseRequest = new CourseRequest();
        courseRequest.setTitle("New Title");
        courseRequest.setDescription("New Description");
        courseRequest.setDuration("New Duration");
        courseRequest.setImageUrl("New Photo");
        courseRequest.setTopicId(1);

        Course existingCourse = new Course(); // Create a mock existing Course object
        existingCourse.setId(courseId);

        // Mock behavior of courseRepository.findById to return an Optional containing the existingCourse
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(existingCourse));

        // Mock behavior of topicRepository.findById to return an Optional containing a mock Topic object
        when(topicRepository.findById(courseRequest.getTopicId())).thenReturn(Optional.of(new Topic()));

        // Call the method under test
        courseService.updateCourse(courseId, courseRequest);

        // Verify that existingCourse was updated with the data from courseRequest
        assertEquals(courseRequest.getTitle(), existingCourse.getTitle());
        assertEquals(courseRequest.getDescription(), existingCourse.getDescription());
        assertEquals(courseRequest.getDuration(), existingCourse.getDuration());
        assertEquals(courseRequest.getImageUrl(), existingCourse.getImageUrl());

        // Verify that existingCourse was saved
        // This verification depends on how your save method works in the repository
        // You can use Mockito's verify method to ensure that save was called with the correct arguments
        verify(courseRepository, times(1)).save(existingCourse);
    }
    @Test
    @DisplayName("Update Course with Invalid Course ID - Throws EntityNotFoundException")
    void updateCourse_InvalidCourseId_ThrowsEntityNotFoundException() {
        // Mock data
        Integer courseId = 1;
        CourseRequest courseRequest = new CourseRequest();
        courseRequest.setTitle("New Title");
        courseRequest.setDescription("New Description");
        courseRequest.setDuration("New Duration");
        courseRequest.setImageUrl("New Photo");
        courseRequest.setTopicId(1);

        // Mock behavior of courseRepository.findById to return an empty Optional
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // Method call and assertion
        assertThrows(EntityNotFoundException.class, () -> {
            courseService.updateCourse(courseId, courseRequest);
        });

        // Verify that courseRepository.findById was called with the correct courseId
        verify(courseRepository, times(1)).findById(courseId);

        // Verify that courseRepository.save was not called
        verify(courseRepository, never()).save(any());
    }
    @Test
    @DisplayName("Update Course with Invalid Topic ID - Throws EntityNotFoundException")
    void updateCourse_InvalidTopicId_ThrowsEntityNotFoundException() {
        // Mock data
        Integer courseId = 1;
        CourseRequest courseRequest = new CourseRequest();
        courseRequest.setTitle("New Title");
        courseRequest.setDescription("New Description");
        courseRequest.setDuration("New Duration");
        courseRequest.setImageUrl("New Photo");
        courseRequest.setTopicId(999); // Invalid topic ID

        Course existingCourse = new Course(); // Create a mock existing Course object
        existingCourse.setId(courseId);

        // Mock behavior of courseRepository.findById to return an Optional containing the existingCourse
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(existingCourse));

        // Mock behavior of topicRepository.findById to return an empty Optional (topic not found)
        when(topicRepository.findById(courseRequest.getTopicId())).thenReturn(Optional.empty());

        // Method call and assertion
        assertThrows(EntityNotFoundException.class, () -> {
            courseService.updateCourse(courseId, courseRequest);
        });

        // Verify that courseRepository.findById was called with the correct courseId
        verify(courseRepository, times(1)).findById(courseId);

        // Verify that topicRepository.findById was called with the correct topicId
        verify(topicRepository, times(1)).findById(courseRequest.getTopicId());

        // Verify that courseRepository.save was not called
        verify(courseRepository, never()).save(any());
    }

    @Test
    @DisplayName("Update Course with Null Request - Throws EntityNotFoundException")
    void updateCourse_NullRequest_ThrowsEntityNotFoundException() {
        // Mock data
        Integer courseId = 1;

        // Mock behavior of courseRepository.findById to return an empty Optional
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // Method call and assertion
        assertThrows(EntityNotFoundException.class, () -> {
            courseService.updateCourse(courseId, null);
        });

        // Verify that courseRepository.findById was called with the correct courseId
        verify(courseRepository, times(1)).findById(courseId);

        // Verify that courseRepository.save was not called
        verify(courseRepository, never()).save(any());
    }

    // Tests for deleteCourse method
    @Test
    @DisplayName("Delete Course with Invalid course id")
    void deleteCourse_ValidCourseId_CourseDeletedSuccessfully() {
        // Mock data
        Integer courseId = 1;
        Course mockCourse = new Course();

        // Mock behavior of courseRepository.findById to return an Optional containing the mockCourse
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(mockCourse));

        // Call the method under test
        courseService.deleteCourse(courseId);

        // Verify that the delete method of courseRepository was called with the correct course
        verify(courseRepository, times(1)).delete(mockCourse);
    }
    @Test
    @DisplayName("Delete Course with Invalid course id - Throws EntityNotFoundException")
    void deleteCourse_InvalidCourseId_EntityNotFoundExceptionThrown() {
        // Mock data
        Integer courseId = 1;

        // Mock behavior of courseRepository.findById to return an empty Optional
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // Call the method under test and assert that it throws EntityNotFoundException
        assertThrows(EntityNotFoundException.class, () -> {
            courseService.deleteCourse(courseId);
        });

        // Verify that the delete method of courseRepository was not called
        verify(courseRepository, never()).delete(any());
    }
    // Tests for getAllCourses method
    @Test
    @DisplayName("Get All Courses  - Returns list of all courses")
    void getAllCourses_ReturnsListOfCourseResponses() {
        // Mock data
        Course course1 = new Course();
        Course course2 = new Course();
        course1.setId(1); // Set a unique id for course1
        course2.setId(2); // Set a unique id for course2
        List<Course> mockCourses = Arrays.asList(course1, course2);
        CourseResponse response1 = new CourseResponse();
        CourseResponse response2 = new CourseResponse();
        List<CourseResponse> expectedResponses = Arrays.asList(response1, response2);
        // Mock behavior of courseRepository.findAll to return the mockCourses
        when(courseRepository.findAll()).thenReturn(mockCourses);
        // Mock behavior of courseMapper.toCourseResponse to return the expectedResponses
        when(courseMapper.toCourseResponse(course1)).thenReturn(response1);
        when(courseMapper.toCourseResponse(course2)).thenReturn(response2);
        // Call the method under test
        List<CourseResponse> actualResponses = courseService.getAllCourses();

        // Verify that the courseRepository.findAll was called
        verify(courseRepository, times(1)).findAll();
        // Verify that courseMapper.toCourseResponse was called for each course in the list
        verify(courseMapper, times(1)).toCourseResponse(course1);
        verify(courseMapper, times(1)).toCourseResponse(course2);
        // Assert that the actual responses match the expected responses
        assertEquals(expectedResponses.size(), actualResponses.size());
        for (int i = 0; i < expectedResponses.size(); i++) {
            assertEquals(expectedResponses.get(i), actualResponses.get(i));
        }
    }
    // Tests for findCourseByTitleContaining method
    @Test
    @DisplayName("Find Courses by Title Containing - Returns PageResponse")
    void findCourseByTitleContaining_ReturnsPageResponse() {
        // Mock data
        String keyword = "Java";
        int page = 0;
        int size = 10;
        Course course1 = new Course();
        Course course2 = new Course();
        course1.setId(1);
        course2.setId(2);
        List<Course> mockCourses = Arrays.asList(course1, course2);
        Page<Course> mockCoursePage = new PageImpl<>(mockCourses);
        CourseResponse response1 = new CourseResponse();
        CourseResponse response2 = new CourseResponse();
        List<CourseResponse> expectedResponses = Arrays.asList(response1, response2);

        // Mock behavior of courseRepository.findByTitleContaining to return the mockCoursePage
        when(courseRepository.findByTitleContaining(keyword, PageRequest.of(page, size))).thenReturn(mockCoursePage);

        // Mock behavior of courseMapper.toCourseResponse to return the expectedResponses
        when(courseMapper.toCourseResponse(course1)).thenReturn(response1);
        when(courseMapper.toCourseResponse(course2)).thenReturn(response2);

        // Call the method under test
        PageResponse<CourseResponse> actualPageResponse = courseService.findCourseByTitleContaining(keyword, page, size);

        // Verify that the courseRepository.findByTitleContaining was called
        verify(courseRepository, times(1)).findByTitleContaining(keyword, PageRequest.of(page, size));

        // Verify that courseMapper.toCourseResponse was called for each course in the list
        verify(courseMapper, times(1)).toCourseResponse(course1);
        verify(courseMapper, times(1)).toCourseResponse(course2);

        // Assert that the actual page response content matches the expected responses
        assertEquals(expectedResponses.size(), actualPageResponse.getContent().size());
        for (int i = 0; i < expectedResponses.size(); i++) {
            assertEquals(expectedResponses.get(i), actualPageResponse.getContent().get(i));
        }

        // Assert that the total pages match
        assertEquals(mockCoursePage.getTotalPages(), actualPageResponse.getTotalPages());
    }
    @Test
    @DisplayName("Update Courses by title containing with invalid keyword - Throws EntityNotFoundException")
    void findCourseByTitleContaining_InvalidKeyword_ThrowsException() {
        // Define test data
        String invalidKeyword = null; // An invalid keyword (null in this case)

        // Mock behavior of courseRepository.findByTitleContaining to return null
        when(courseRepository.findByTitleContaining(anyString(), any(Pageable.class))).thenReturn(null);

        // Assert that the method throws the expected exception when called with the invalid keyword
        assertThrows(NullPointerException.class, () -> {
            courseService.findCourseByTitleContaining(invalidKeyword, 0, 10);
        });
    }
    // Tests for getCoursesByTopic method
    @Test
    @DisplayName("Get Courses By Topic")
    void getCoursesByTopic_ValidTopicId_ReturnsPageResponse() {
        // Mock data
        int topicId = 1;
        Topic topic = new Topic();
        topic.setId(topicId);

        Page<Course> coursePage = mock(Page.class);
        List<Course> courses = Collections.singletonList(new Course()); // Mock a list of courses
        when(coursePage.getContent()).thenReturn(courses);
        when(coursePage.getTotalPages()).thenReturn(1);

        // Mock behavior of topicRepository.findById to return the topic
        when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));

        // Mock behavior of courseRepository.findByTopic to return the page of courses
        when(courseRepository.findByTopic(topic, PageRequest.of(0, 10))).thenReturn(coursePage);

        // Mock behavior of courseMapper.toCourseResponse to return a dummy CourseResponse
        when(courseMapper.toCourseResponse(any())).thenReturn(new CourseResponse());

        // Call the method under test
        PageResponse<CourseResponse> pageResponse = courseService.getCoursesByTopic(topicId, 0, 10);

        // Verify that topicRepository.findById was called with the correct topicId
        verify(topicRepository, times(1)).findById(topicId);

        // Verify that courseRepository.findByTopic was called with the correct topic and pagination parameters
        verify(courseRepository, times(1)).findByTopic(topic, PageRequest.of(0, 10));

        // Verify that courseMapper.toCourseResponse was called for each course in the list
        verify(courseMapper, times(1)).toCourseResponse(any());

        // Assert that the returned PageResponse contains the expected content and total pages
        assertNotNull(pageResponse);
        assertNotNull(pageResponse.getContent());
        assertFalse(pageResponse.getContent().isEmpty());
        assertEquals(1, pageResponse.getTotalPages());
    }

    @Test
    @DisplayName("Get Courses By Topic with invalid topic id - Throws EntityNotFoundException")
    void getCoursesByTopic_InvalidTopicId_ThrowsException() {
        // Mock data
        int invalidTopicId = -1;

        // Mock behavior of topicRepository.findById to return an empty Optional (topic not found)
        when(topicRepository.findById(invalidTopicId)).thenReturn(Optional.empty());

        // Assert that the method throws the expected exception when called with the invalid topicId
        assertThrows(EntityNotFoundException.class, () -> {
            courseService.getCoursesByTopic(invalidTopicId, 0, 10);
        });

        // Verify that courseRepository.findByTopic was not called
        verify(courseRepository, never()).findByTopic(any(), any());
    }

     */
