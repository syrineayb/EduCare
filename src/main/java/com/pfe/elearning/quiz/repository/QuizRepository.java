package com.pfe.elearning.quiz.repository;


import com.pfe.elearning.quiz.entity.Quiz;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
@Transactional
public interface QuizRepository extends JpaRepository<Quiz,Long> {
    Optional<Quiz> findById(Long quizId);
    List<Quiz> findByCourseId(Long courseId);
    @Modifying
    @Query("DELETE FROM Quiz q WHERE q.id = :quizId")
    void deleteQuizById(@Param("quizId") Long quizId);

    List<Quiz> findAllByCourse_Id(Integer courseId);

    long countByCourseId(Long courseId);
}