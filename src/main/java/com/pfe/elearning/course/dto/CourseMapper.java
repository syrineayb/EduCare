package com.pfe.elearning.course.dto;

import com.pfe.elearning.course.entity.Course;
import com.pfe.elearning.lesson.dto.LessonResponse;
import com.pfe.elearning.lesson.entity.Lesson;
import com.pfe.elearning.topic.dto.TopicMapper;
import com.pfe.elearning.topic.dto.TopicResponse;
import com.pfe.elearning.topic.entity.Topic;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseMapper {
    private final TopicMapper topicMapper;

    public CourseResponse toCourseResponse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course object cannot be null");
        }
        CourseResponse.CourseResponseBuilder builder = CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .startDate(course.getStartDate())
                .endDate(course.getEndDate())
                .publisherName(course.getPublisher().getFullname())
                .imageFile(course.getImageFile())
                .timeCommitment(course.getTimeCommitment());
        // Vérifier si le Topic est null avant d'accéder à ses propriétés
        if (course.getTopic() != null) {
            builder
                    .topicId(course.getTopic().getId())
                    .topicTitle(course.getTopic().getTitle());
        }
        // Appeler la méthode mapLessonResponses pour mapper les leçons
        List<LessonResponse> lessonResponses = mapLessonResponses(course.getLessons());
        builder.lessons(lessonResponses);
        return builder.build();

    }

    public Course toCourse(CourseRequest courseRequest) {
        if (courseRequest == null) {
            throw new NullPointerException("The course request should not be null");
        }
        Course course = new Course();
        course.setTitle(courseRequest.getTitle());
        course.setDescription(courseRequest.getDescription());
        course.setStartDate(courseRequest.getStartDate());
        course.setEndDate(courseRequest.getEndDate());
        //course.setFree(courseRequest.getIsFree());
        course.setTimeCommitment(courseRequest.getTimeCommitment());
        // course.setPublisherUsername(courseRequest.;
        Topic topic = new Topic();
        topic.setId(courseRequest.getTopicId());
     //   topic.setTitle(courseRequest.getTitle());
        course.setTopic(topic);
        // Set the price only if the course is not free
      /*  if (!courseRequest.getIsFree()) {
            course.setPrice(courseRequest.getPrice());
        } else {
            // If the course is free, ensure that the price is null
            course.setPrice(null);
        }

       */

        return course;
    }


    List<LessonResponse> mapLessonResponses(List<Lesson> lessons) {
        if (lessons == null) {
            return Collections.emptyList(); // Return an empty list if lessons is null
        }
        return lessons.stream()
                .map(lesson -> LessonResponse.builder()
                        .id(lesson.getId())
                        .title(lesson.getTitle())
                        .description(lesson.getDescription())
                        .createdAt(lesson.getCreatedAt())
                        .courseTitle(lesson.getCourse().getTitle()) // Assuming Lesson has a reference to Course
                        .build())
                .collect(Collectors.toList());
    }
    /*List<TopicResponse> mapTopicsResponse(List<Topic> topics) {
        if (topics == null) {
            return Collections.emptyList(); // Return an empty list if lessons is null
        }
        return topics.stream()
                .map(topic -> TopicResponse.builder()
                        .id(topic.getId())
                        .title(topic.getTitle())
                        .createdAt(topic.getCreatedAt())
                        .updatedAt(topic.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }

     */
}