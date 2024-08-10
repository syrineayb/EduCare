package com.pfe.elearning.QuizResult.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizResultResponse {
    private Long id;
    private Long quizId;
    private Long userId;
    private Integer score;
    private Double percentage;
}