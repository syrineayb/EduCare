package com.pfe.elearning.lesson.dto;

import com.pfe.elearning.course.entity.Course;
import com.pfe.elearning.lesson.entity.Lesson;
import org.springframework.stereotype.Service;

@Service
public class LessonMapper {

    public LessonResponse toLessonResponse(Lesson lesson) {
        return LessonResponse.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .description(lesson.getDescription())
                .createdAt(lesson.getCreatedAt())
                .updatedAt(lesson.getUpdatedAt())
                .courseTitle(lesson.getCourse().getTitle())

                .build();
    }

        public Lesson toLesson(LessonRequest lessonRequest, Course course) {
        Lesson lesson = new Lesson();
        lesson.setTitle(lessonRequest.getTitle());
        lesson.setDescription(lessonRequest.getDescription());
        lesson.setCourse(course);
        return lesson;
    }
}
