package com.pfe.elearning.QuizResult.service;

import com.pfe.elearning.QuizResult.dto.QuizResultRequest;
import com.pfe.elearning.QuizResult.dto.QuizResultResponse;
import com.pfe.elearning.QuizResult.dto.UserQuizProgressResponse;
import com.pfe.elearning.QuizResult.entity.QuizResult;

import java.util.List;

public interface QuizResultService {
    List<QuizResultResponse> getAllQuizResults();

    List<QuizResultResponse> getResultsByCandidateId(String candidateUsername);


    int getUserQuizProgress(Long courseId, String candidateUsername);
}