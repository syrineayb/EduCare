package com.pfe.elearning.question.service.impl;


import com.pfe.elearning.Option.dto.OptionRequest;
import com.pfe.elearning.Option.dto.OptionResponse;
import com.pfe.elearning.Option.entity.Option;
import com.pfe.elearning.Option.repository.OptionRepository;
import com.pfe.elearning.common.PageResponse;
import com.pfe.elearning.question.dto.*;
import com.pfe.elearning.question.entity.Question;
import com.pfe.elearning.question.repository.QuestionRepository;
import com.pfe.elearning.question.service.QuestionService;
import com.pfe.elearning.quiz.entity.Quiz;
import com.pfe.elearning.quiz.repository.QuizRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private OptionRepository optionRepository;


    @Transactional
    public void deleteById(Long questionId) {
        // Delete associated options
        questionRepository.deleteOptionsByQuestionId(questionId);
        // Delete the question
        questionRepository.deleteQuestionById(questionId);
    }
    @Override
    @Transactional
    public List<QuestionResponse> getAllQuestionsByCourseId(Integer courseId) {
        List<Question> questions = questionRepository.findAllByCourseId(courseId);
        return questions.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private QuestionResponse mapToDto(Question question) {
        return QuestionResponse.builder()
                .id(Long.valueOf(question.getId()))
                .text(question.getTitle())
                .type(question.getType())
                .quizId(Long.valueOf(question.getQuiz().getId()))
                .quizTitle(question.getQuiz().getTitle())
                .duration(question.getQuiz().getDuration())
                .mark(question.getQuiz().getMark())
                .courseId(question.getQuiz().getCourse().getId())
                .options(question.getOptions().stream()
                        .map(option -> OptionResponse.builder()
                                .id(option.getId())
                                .text(option.getText())
                                .correct(option.isCorrect())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public List<QuestionResponse> getAllQuestionsByQuizId(Long quizId) {
        // Implement logic to retrieve all questions for the specified quiz ID
        List<Question> questions = questionRepository.findAllByQuizId(quizId);
        return questions.stream()
                .map(questionMapper::toQuestionResponse)
                .collect(Collectors.toList());
    }
    @Override
    public PageResponse<QuestionResponse> getAllQuestionsByQuizId(Long quizId, int page, int size) {
        try {
            Page<Question> questionsPage = questionRepository.findAllByQuizId(quizId, PageRequest.of(page, size));
            List<QuestionResponse> questionResponses = questionsPage.getContent().stream()
                    .map(questionMapper::toQuestionResponse)
                    .collect(Collectors.toList());
            return PageResponse.<QuestionResponse>builder()
                    .content(questionResponses)
                    .totalPages(questionsPage.getTotalPages())
                    .build();
        } catch (Exception ex) {
            throw new RuntimeException("Error occurred while finding questions: " + ex.getMessage(), ex);
        }
    }



    @Override
    public List<QuestionResponse> getAllQuestionsForCandidate(Long quizId) {
        List<Question> questions = questionRepository.findAllByQuizId(quizId);

        return questions.stream()
                .map(questionMapper::toQuestionResponseWithoutCorrectAnswers)
                .collect(Collectors.toList());
    }

    @Override
    public QuestionResponse addQuestionToQuiz(Long quizId, QuestionRequest questionRequest) {
        // Fetch the quiz
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found"));

        // Create the new question
        Question question = questionMapper.toQuestion(questionRequest, quiz);

        // Save the question
        Question savedQuestion = questionRepository.save(question);

        // Map and return the saved question
        return questionMapper.toQuestionResponse(savedQuestion);
    }

    @Override
    public QuestionResponse updateQuestion(Long questionId, QuestionRequest questionRequest) {
        // Fetch the question entity along with its options eagerly
        Question existingQuestion = (Question) questionRepository.findByIdWithFetchOptions(questionId)
                .orElse(null);

        // If the question does not exist, return null
        if (existingQuestion == null) {
            return null;
        }

        // Update the existing question with the new details
        existingQuestion.setTitle(questionRequest.getTitle());
        existingQuestion.setType(questionRequest.getType());

        // Update or create options
        List<OptionRequest> updatedOptions = questionRequest.getOptions();
        List<Option> existingOptions = existingQuestion.getOptions();

        // Update existing options or add new ones
        for (int i = 0; i < updatedOptions.size(); i++) {
            OptionRequest optionRequest = updatedOptions.get(i);
            Option optionToUpdate = (i < existingOptions.size()) ? existingOptions.get(i) : new Option();
            optionToUpdate.setText(optionRequest.getText());
            optionToUpdate.setCorrect(optionRequest.isCorrect());
            optionToUpdate.setQuestion(existingQuestion);
            // Add the option if it's a new one
            if (i >= existingOptions.size()) {
                existingOptions.add(optionToUpdate);
            }
        }

        // Remove extra options if needed
        if (existingOptions.size() > updatedOptions.size()) {
            existingOptions.subList(updatedOptions.size(), existingOptions.size()).clear();
        }

        // Save the updated question along with its options
        Question updatedQuestion = questionRepository.save(existingQuestion);

        // Map the updated question entity to a response DTO and return it
        return questionMapper.toQuestionResponse(updatedQuestion);
    }



    @Override
    public List<QuestionCorrect> getQuestionsWithCorrectOptions(Long quizId) {
        List<Question> questions = questionRepository.findAllByQuizId(quizId);
        return questions.stream().map(question -> {
            List<Integer> correctOptionIds = question.getOptions().stream()
                    .filter(Option::isCorrect)
                    .map(Option::getId)
                    .collect(Collectors.toList());
            return new QuestionCorrect(question.getId(), correctOptionIds);
        }).collect(Collectors.toList());
    }


}


