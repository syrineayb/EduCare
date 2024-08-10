package com.pfe.elearning.UserResourceCompletion.UserlessonCompletion;

import com.pfe.elearning.lesson.entity.Lesson;
import com.pfe.elearning.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserlessonCompletionRepository extends JpaRepository<UserLessonCompletion, Integer> {
    Optional<UserLessonCompletion> findByUserAndLesson(User user, Lesson lesson);

   long countByUserAndLesson_Course_IdAndCompleted(User user, Integer courseId, boolean completed);
//
//    long countByLessonAndCompleted(Lesson lesson, boolean completed);
}
