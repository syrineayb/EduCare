package com.pfe.elearning.quiz.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuizRequest {

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotNull(message = "Duration cannot be null")
    private Integer duration; // Duration in minutes

    @NotNull(message = "not cannot be null")
    private Integer mark; // Duration in minutes
    private List<Long> questionIds;

}