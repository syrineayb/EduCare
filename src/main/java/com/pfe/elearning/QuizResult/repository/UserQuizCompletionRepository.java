package com.pfe.elearning.QuizResult.repository;

import com.pfe.elearning.QuizResult.entity.UserQuizCompletion;
import com.pfe.elearning.quiz.entity.Quiz;
import com.pfe.elearning.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserQuizCompletionRepository extends JpaRepository<UserQuizCompletion, Integer> {
    Optional<UserQuizCompletion> findByUserAndQuiz(User user, Quiz quiz);

    long countByUserAndQuiz_CourseIdAndCompletedTrue(User user, Long courseId);
}