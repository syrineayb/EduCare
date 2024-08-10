package com.pfe.elearning.question.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class QuestionCorrect {
    private Integer quetionId;
    private List<Integer> CorrectOptionIds; // For multiple answers, use a list

}
