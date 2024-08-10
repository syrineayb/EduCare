package com.pfe.elearning.course.dto;

import com.pfe.elearning.course.entity.Course;
import com.pfe.elearning.lesson.dto.LessonResponse;
import com.pfe.elearning.lesson.entity.Lesson;
import com.pfe.elearning.topic.dto.TopicMapper;
import com.pfe.elearning.topic.dto.TopicResponse;
import com.pfe.elearning.topic.entity.Topic;
import com.pfe.elearning.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CourseMapperTest {

    private CourseMapper courseMapper;

    @BeforeEach
    void setUp() {
        TopicMapper topicMapper = mock(TopicMapper.class);
        courseMapper = new CourseMapper(topicMapper);
    }

    @Test
    void shouldMapCourseRequestToCourse() {
        // Créer un objet MockMultipartFile pour simuler un fichier image
        MockMultipartFile file;
        try {
            file = new MockMultipartFile(
                    "test.jpg",
                    "test.jpg",
                    "image/jpeg",
                    Files.readAllBytes(Paths.get("images/test.jpg")));
        } catch (IOException e) {
            // Gérer l'exception en cas d'erreur lors de la lecture du fichier
            e.printStackTrace();
            return; // Arrêter l'exécution du test
        }

        // Créer un objet CourseRequest
        CourseRequest courseRequest = new CourseRequest();
        courseRequest.setTitle("Test Course");
        courseRequest.setDescription("This is a test course");
        courseRequest.setImageFile(file); // Utiliser l'objet MockMultipartFile
        courseRequest.setTimeCommitment("2 weeks");
        courseRequest.setImageFile(file); // Utiliser l'objet MockMultipartFile
        courseRequest.setEndDate(LocalDate.now().plusWeeks(2)); // Définir la date de fin comme étant 2 semaines à partir d'aujourd'hui
        courseRequest.setTopicId(1);

        // Appeler la méthode toCourse
        Course course = courseMapper.toCourse(courseRequest);

        // Assertions
        assertNotNull(course);
        assertEquals(courseRequest.getTitle(), course.getTitle());
        assertEquals(courseRequest.getDescription(), course.getDescription());
        assertNotNull(course.getImageFile());
        assertEquals("test.jpg", file.getOriginalFilename()); // Vérifier le nom du fichier
        assertEquals("image/jpeg", file.getContentType()); // Vérifier le type de contenu
        assertEquals(courseRequest.getTimeCommitment(), course.getTimeCommitment());
        assertEquals(courseRequest.getTopicId(), course.getTopic().getId());
        assertEquals(courseRequest.getEndDate(), course.getEndDate()); // Vérifier la date de fin
    }


    @Test
    void shouldMapCourseToCourseResponse() {
        // Créer un objet Course
        Course course = new Course();
        course.setId(1);
        course.setTitle("Test Course");
        course.setDescription("This is a test course");
        course.setCreatedAt(LocalDateTime.now());
        course.setTimeCommitment("2 weeks");
        course.setImageFile("test.jpg");
        User publisher = new User();
        publisher.setFirstname("John");
        publisher.setLastname("Doe");
        course.setPublisher(publisher);

        // Créer un objet Topic associé à votre Course
        Topic topic = new Topic();
        topic.setId(1);
        topic.setTitle("Test Topic");
        course.setTopic(topic);

        // Créer un objet simulé TopicResponse
        TopicResponse topicResponse = new TopicResponse();
        topicResponse.setId(1);
        topicResponse.setTitle("Test Topic");

        // Configurer le mock pour la méthode toTopicResponse
        TopicMapper topicMapper = mock(TopicMapper.class);
        when(topicMapper.toTopicResponse(course.getTopic())).thenReturn(topicResponse);

        // Créer une liste de leçons et ajouter une leçon
        List<Lesson> lessons = new ArrayList<>();
        Lesson lesson1 = new Lesson();
        lesson1.setId(1);
        lesson1.setTitle("Lesson 1");
        lesson1.setDescription("This is lesson 1");
        lesson1.setCreatedAt(LocalDateTime.now());
        lesson1.setCourse(course);
        lessons.add(lesson1);
        course.setLessons(lessons);

        // Appeler la méthode toCourseResponse
        CourseResponse courseResponse = courseMapper.toCourseResponse(course);

        // Assertions
        assertNotNull(courseResponse);
        assertEquals(course.getId(), courseResponse.getId());
        assertEquals(course.getTitle(), courseResponse.getTitle());
        assertEquals(course.getDescription(), courseResponse.getDescription());
        assertEquals(course.getCreatedAt(), courseResponse.getCreatedAt());
        assertEquals(course.getTimeCommitment(), courseResponse.getTimeCommitment());
        assertEquals(course.getImageFile(), courseResponse.getImageFile());
        assertEquals(topicResponse.getId(), courseResponse.getTopicId());
        assertEquals(topicResponse.getTitle(), courseResponse.getTopicTitle());
        assertEquals(lessons.size(), courseResponse.getLessons().size());
    }

    @Test
    void shouldReturnEmptyListOfLessonResponsesWhenCourseHasNullListOfLessons() {
        // Test case for handling null list of lessons
        // Create a Course object with null list of lessons
        Course course = new Course();
        course.setId(1);
        course.setTitle("Test Course");
        course.setDescription("This is a test course");
        course.setCreatedAt(LocalDateTime.now());
        course.setTimeCommitment("2 weeks");
        course.setImageFile("test.jpg");
        User publisher = new User();
        publisher.setFirstname("John");
        publisher.setLastname("Doe");
        course.setPublisher(publisher);
        course.setLessons(null); // Set the list of lessons to null

        // Call the method to map lesson responses
        List<LessonResponse> lessonResponses = courseMapper.mapLessonResponses(course.getLessons());

        // Assertions
        assertNotNull(lessonResponses);
        assertTrue(lessonResponses.isEmpty()); // Ensure that an empty list of lesson responses is returned
    }

    @Test
    void shouldThrowNullPointerExceptionWhenCourseRequestIsNull() {
        // Act & Assert
        NullPointerException exception = assertThrows(NullPointerException.class, () -> courseMapper.toCourse(null));
        assertEquals("The course request should not be null", exception.getMessage());
    }
}
