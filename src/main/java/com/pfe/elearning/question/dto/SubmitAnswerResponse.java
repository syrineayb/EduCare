package com.pfe.elearning.question.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter

@AllArgsConstructor
public class SubmitAnswerResponse {
    private int score;
    private int percentage;
    private List<QuestionResult> questionResults;
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionResult {
        private Integer questionId;
        private List<Integer> correctOptionIds;
        private List<Integer> submittedOptionIds;
    }
}
