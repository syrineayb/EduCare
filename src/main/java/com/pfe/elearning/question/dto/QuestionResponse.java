package com.pfe.elearning.question.dto;

import com.pfe.elearning.Option.dto.OptionResponse;
import com.pfe.elearning.question.entity.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class QuestionResponse {

    private Long id;
    private String text;
    private QuestionType type;
    private Long quizId;
    private String quizTitle;
    private Integer duration;
    private Integer mark;
    private Integer courseId;
    private List<OptionResponse> options;  // New field for quiz name


}
