package com.pfe.elearning.question.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SubmitAnswerRequest {

    private Integer questionId;
    private List<Integer> selectedOptionIds; // For multiple answers, use a list

}
