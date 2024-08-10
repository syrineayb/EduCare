package com.pfe.elearning.quiz.dto;

import com.pfe.elearning.question.dto.QuestionResponse;
import com.pfe.elearning.quiz.entity.Quiz;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class QuizResponse {

    private Integer id;
    private String title;
    private Integer duration;
    private Integer courseId;
    private Integer mark;
    private String courseTitle; // Add this field for the course title
    private List<QuestionResponse> questions; // Add this field for questions and their options



}