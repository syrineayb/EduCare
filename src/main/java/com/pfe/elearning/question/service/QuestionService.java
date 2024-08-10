package com.pfe.elearning.question.service;


import com.pfe.elearning.common.PageResponse;
import com.pfe.elearning.question.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QuestionService {


    void deleteById(Long id);

    List<QuestionResponse> getAllQuestionsByCourseId(Integer courseId);

    List<QuestionResponse> getAllQuestionsByQuizId(Long quizId);


    PageResponse<QuestionResponse> getAllQuestionsByQuizId(Long quizId, int page, int size);

    List<QuestionResponse> getAllQuestionsForCandidate(Long quizId);

    QuestionResponse updateQuestion(Long questionId, QuestionRequest questionRequest);


    List<QuestionCorrect> getQuestionsWithCorrectOptions(Long quizId); // New method

    QuestionResponse addQuestionToQuiz(Long quizId, QuestionRequest questionRequest);


}