package com.pfe.elearning.question.dto;

import com.pfe.elearning.Option.dto.OptionResponse;
import com.pfe.elearning.Option.entity.Option;

import com.pfe.elearning.question.entity.Question;
import com.pfe.elearning.quiz.entity.Quiz;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service


public class QuestionMapper {


    public QuestionResponse toQuestionResponse(Question question) {
        return QuestionResponse.builder()
                .id(Long.valueOf(question.getId()))
                .text(question.getTitle())
                .type(question.getType())
                .quizId(Long.valueOf(question.getQuiz().getId()))
                .quizTitle(question.getTitle())
                .duration(question.getQuiz().getDuration())
                .mark(question.getQuiz().getMark())
                .courseId(question.getQuiz().getCourse().getId())
                .options(question.getOptions().stream()
                        .map(option -> new OptionResponse(option.getId(), option.getText(), option.isCorrect()))
                        .collect(Collectors.toList()))
                .build();
    }
    public Question toQuestion(QuestionRequest questionRequest, Quiz quiz) {
        Question question = new Question();
        question.setTitle(questionRequest.getTitle());
        question.setType(questionRequest.getType());
        question.setQuiz(quiz);

        // Map each OptionRequest to an Option and set its correct value
        List<Option> options = questionRequest.getOptions().stream()
                .map(optionRequest -> {
                    Option option = new Option();
                    option.setText(optionRequest.getText());
                    option.setCorrect(optionRequest.isCorrect());
                    option.setQuestion(question);
                    return option;
                })
                .collect(Collectors.toList());

        // Set the options for the question
        question.setOptions(options);

        // Return the constructed question
        return question;
    }





    public QuestionResponse toQuestionResponseWithoutCorrectAnswers(Question question) {
        return QuestionResponse.builder()
                .id(Long.valueOf(question.getId()))
                .text(question.getTitle())
                .type(question.getType())
                .quizId(Long.valueOf(question.getQuiz().getId()))
                .duration(question.getQuiz().getDuration())
                .mark(question.getQuiz().getMark())
                .courseId(question.getQuiz().getCourse().getId())
                .options(question.getOptions().stream()
                        .map(option -> new OptionResponse(option.getId(), option.getText(), false)) // Do not include the correct status
                        .collect(Collectors.toList()))
                .build();
    }



}

