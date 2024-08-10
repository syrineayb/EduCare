package com.pfe.elearning.QuizResult.repository;

import com.pfe.elearning.QuizResult.entity.QuizResult;
import com.pfe.elearning.quiz.entity.Quiz;
import com.pfe.elearning.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuizResultRepository extends JpaRepository<QuizResult,Integer> {
    List<QuizResult> findByCandidateId(Integer candidateId);

    Optional<QuizResult> findByQuizAndCandidate(Quiz quiz, User user);

    @Query("SELECT AVG(qr.percentage) FROM QuizResult qr WHERE qr.candidate.id = :candidateId")
    Optional<Double> getPercentageByCandidateId(@Param("candidateId") Integer candidateId);}
