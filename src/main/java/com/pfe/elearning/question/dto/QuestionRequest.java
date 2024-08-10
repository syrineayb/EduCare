package com.pfe.elearning.question.dto;

import com.pfe.elearning.Option.dto.OptionRequest;
import com.pfe.elearning.genre.entity.Genre;
import com.pfe.elearning.question.entity.QuestionType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class QuestionRequest {
    @NotBlank(message = "Question text cannot be blank")
    private String title;

    @NotNull(message = "Question type cannot be blank")
    private QuestionType type;

    @NotNull(message = "Options cannot be null")
    private List<OptionRequest> options;


}