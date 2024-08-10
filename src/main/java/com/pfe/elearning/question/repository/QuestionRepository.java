package com.pfe.elearning.question.repository;

import com.pfe.elearning.course.entity.Course;
import com.pfe.elearning.question.entity.Question;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query("SELECT q FROM Question q JOIN FETCH q.options WHERE q.quiz.id = :quizId")
    List<Question> findAllByQuizId(@Param("quizId") Long quizId);
    @Query("SELECT q FROM Question q JOIN FETCH q.options WHERE q.quiz.id = :quizId")
    Page<Question> findAllByQuizId(@Param("quizId") Long quizId, Pageable pageable);

    @Modifying
    @Transactional
    @Query("DELETE FROM Option o WHERE o.question.id = :questionId")
    void deleteOptionsByQuestionId(@Param("questionId") Long questionId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Question q WHERE q.id = :questionId")
    void deleteQuestionById(@Param("questionId") Long questionId);



    @Query("SELECT q FROM Question q JOIN FETCH q.options WHERE q.id = :questionId")
    Optional<Question> findByIdWithFetchOptions(@Param("questionId") Long questionId);

    @Query("SELECT q FROM Question q LEFT JOIN FETCH q.options WHERE q.quiz.course.id = :courseId")
    List<Question> findAllByCourseId(Integer courseId);
}