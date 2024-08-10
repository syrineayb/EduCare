package com.pfe.elearning.quiz.service;

import com.pfe.elearning.question.dto.QuestionCorrect;
import com.pfe.elearning.question.dto.QuestionResponse;
import com.pfe.elearning.question.dto.SubmitAnswerRequest;
import com.pfe.elearning.question.dto.SubmitAnswerResponse;
import com.pfe.elearning.quiz.dto.QuizRequest;
import com.pfe.elearning.quiz.dto.QuizResponse;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface QuizService {
    QuizResponse createQuiz(QuizRequest request, Integer courseId);

    QuizResponse createQuizWithQuestions(QuizRequest request, Integer courseId);

    List<QuestionResponse> getAllQuestionsByCourseId(Integer courseId);

    QuizResponse getQuizById(Long quizId);
    QuizResponse updateQuiz(Long quizId, QuizRequest quizRequest);
    void deleteQuiz(Long quizId);
    List<QuizResponse> getAllQuizzes();
    List<QuizResponse> getQuizzesByCourseId(Long courseId);

    List<QuestionCorrect> getQuestionsWithCorrectOptions(Long quizId);

    SubmitAnswerResponse evaluateQuiz(Integer quizId, List<SubmitAnswerRequest> answers, String candidateUsername);

}