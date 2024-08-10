package com.pfe.elearning.QuizResult.dto;

import com.pfe.elearning.QuizResult.entity.QuizResult;
import com.pfe.elearning.quiz.entity.Quiz;
import com.pfe.elearning.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuizResultMapper {

    public QuizResultResponse toQuizResultResponse(QuizResult quizResult) {
        return QuizResultResponse.builder()
                .id(Long.valueOf(quizResult.getId()))
                .quizId(Long.valueOf(quizResult.getQuiz().getId()))
                .userId(Long.valueOf(quizResult.getCandidate().getId()))
                .score(quizResult.getScore())
                .percentage(quizResult.getPercentage())
                .build();
    }

    public QuizResult toQuizResult(QuizResultRequest quizResultRequest) {
        QuizResult quizResult = new QuizResult();
        quizResult.setScore(quizResultRequest.getScore());
        quizResult.setPercentage(quizResultRequest.getPercentage());
        Quiz quiz = new Quiz();
        quiz.setId(Math.toIntExact(quizResultRequest.getQuizId()));
        quizResult.setQuiz(quiz);
        User user = new User();
        user.setId(Math.toIntExact(quizResultRequest.getUserId()));
        quizResult.setCandidate(user);
        return quizResult;
    }
}