package com.pfe.elearning.QuizResult.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizResultRequest {
    @NotNull(message = "Quiz ID cannot be null")
    private Long quizId;

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @NotNull(message = "Score cannot be null")
    private Integer score;

    @NotNull(message = "Percentage cannot be null")
    private Double percentage;
}