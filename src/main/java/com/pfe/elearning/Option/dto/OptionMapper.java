package com.pfe.elearning.Option.dto;

import com.pfe.elearning.Option.entity.Option;
import com.pfe.elearning.question.entity.Question;
import org.springframework.stereotype.Service;

@Service

public class OptionMapper {
    public Option toOption(OptionRequest optionRequest, Question question) {
        Option option = new Option();
        option.setText(optionRequest.getText());
        option.setCorrect(optionRequest.isCorrect()); // Ensure correct value is set here
        option.setQuestion(question);
        return option;
    }



    public OptionResponse toOptionResponse(Option option) {
        return OptionResponse.builder()
                .id((option.getId()))
                .text(option.getText())
                .correct(option.isCorrect())
                .build();
    }
}