package com.pfe.elearning.quiz.service.impl;


import com.pfe.elearning.Option.dto.OptionResponse;
import com.pfe.elearning.Option.entity.Option;
import com.pfe.elearning.Option.repository.OptionRepository;
import com.pfe.elearning.QuizResult.entity.QuizResult;
import com.pfe.elearning.QuizResult.entity.UserQuizCompletion;
import com.pfe.elearning.QuizResult.repository.QuizResultRepository;
import com.pfe.elearning.QuizResult.repository.UserQuizCompletionRepository;
import com.pfe.elearning.course.entity.Course;
import com.pfe.elearning.course.repository.CourseRepository;
import com.pfe.elearning.question.dto.*;
import com.pfe.elearning.question.entity.Question;
import com.pfe.elearning.question.repository.CandidateResponseRepository;
import com.pfe.elearning.question.repository.QuestionRepository;
import com.pfe.elearning.question.service.QuestionService;
import com.pfe.elearning.quiz.dto.QuizMapper;
import com.pfe.elearning.quiz.dto.QuizRequest;
import com.pfe.elearning.quiz.dto.QuizResponse;
import com.pfe.elearning.quiz.entity.Quiz;
import com.pfe.elearning.quiz.repository.QuizRepository;
import com.pfe.elearning.quiz.service.QuizService;
import com.pfe.elearning.user.entity.User;
import com.pfe.elearning.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {
    private final UserService userService;
    private final QuizRepository quizRepository;
    private final QuizMapper quizMapper;
    private final CourseRepository courseRepository;
    private final QuizResultRepository quizResultRepository;
    private final OptionRepository optionRepository;
    private final QuestionRepository questionRepository;
    private final QuestionService questionService;
    private final CandidateResponseRepository candidateResponseRepository;
    private final QuestionMapper questionMapper;
    private final UserQuizCompletionRepository userQuizCompletionRepository;
    @Override
    public QuizResponse createQuiz(QuizRequest request, Integer courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new NotFoundException("Course not found with id: " + courseId));

        Quiz quiz = quizMapper.toQuiz(request);
        quiz.setCourse(course);
        quiz = quizRepository.save(quiz);

        return quizMapper.toQuizResponse(quiz);
    }

    @Transactional
    @Override
    public QuizResponse createQuizWithQuestions(QuizRequest quizRequest, Integer courseId) {
        // Fetch the course by id
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new NotFoundException("Course not found with id: " + courseId));

        // Map the request to a Quiz entity
        Quiz quiz = quizMapper.toQuiz(quizRequest);
        quiz.setCourse(course);

        // Fetch and clone questions and their options within the session
        Quiz finalQuiz = quiz;
        List<Question> clonedQuestions = quizRequest.getQuestionIds().stream().map(questionId -> {
            // Find the original question by id
            Question originalQuestion = questionRepository.findById(questionId).orElseThrow(() -> new NotFoundException("Question not found with id: " + questionId));

            // Initialize the options collection to avoid LazyInitializationException
            Hibernate.initialize(originalQuestion.getOptions());

            // Clone the question
            Question clonedQuestion = new Question();
            clonedQuestion.setTitle(originalQuestion.getTitle());
            clonedQuestion.setType(originalQuestion.getType());
            clonedQuestion.setQuiz(finalQuiz); // Associate with the new quiz

            // Clone options and associate them with the cloned question
            List<Option> clonedOptions = originalQuestion.getOptions().stream().map(option -> {
                Option clonedOption = new Option();
                clonedOption.setText(option.getText());
                clonedOption.setCorrect(option.isCorrect());
                clonedOption.setQuestion(clonedQuestion); // Associate with cloned question
                return clonedOption;
            }).collect(Collectors.toList());

            clonedQuestion.setOptions(clonedOptions);
            return clonedQuestion;
        }).collect(Collectors.toList());

        // Save the quiz to get the generated ID
        quiz = quizRepository.save(quiz);

        // Save the cloned questions to the database
        clonedQuestions.forEach(questionRepository::save);

        // Map the saved quiz to a response DTO and return it
        return quizMapper.toQuizResponse(quiz);
    }


    @Override
    public List<QuestionResponse> getAllQuestionsByCourseId(Integer courseId) {
        List<Quiz> quizzesInCourse = quizRepository.findAllByCourse_Id(courseId);
        return quizzesInCourse.stream().flatMap(quiz -> quiz.getQuestions().stream()).map(question -> {
            // Map question options
            List<OptionResponse> optionResponses = question.getOptions().stream().map(option -> new OptionResponse(option.getId(), option.getText(), option.isCorrect())).collect(Collectors.toList());

            // Create QuestionResponse
            return new QuestionResponse(Long.valueOf(question.getId()), question.getTitle(), question.getType(), Long.valueOf(question.getQuiz().getId()), question.getQuiz().getTitle(), question.getQuiz().getDuration(), question.getQuiz().getMark(), question.getQuiz().getCourse().getId(), optionResponses);
        }).collect(Collectors.toList());
    }


    public QuizResponse getQuizById(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new RuntimeException("Quiz not found with id: " + quizId));
        return quizMapper.toQuizResponse(quiz);
    }

    public QuizResponse updateQuiz(Long quizId, QuizRequest quizRequest) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new RuntimeException("Quiz not found with id: " + quizId));
        // Update quiz details
        quiz.setTitle(quizRequest.getTitle());
        quiz.setDuration(quizRequest.getDuration());
        quiz.setMark(quizRequest.getMark());
        // Save the updated quiz
        Quiz updatedQuiz = quizRepository.save(quiz);
        return quizMapper.toQuizResponse(updatedQuiz);
    }

    @Override
    public void deleteQuiz(Long quizId) {
        // Check if the quiz exists
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new NotFoundException("Quiz not found with id: " + quizId));

        // Delete the quiz
        quizRepository.deleteQuizById(quizId);
    }


    @Override
    public List<QuizResponse> getAllQuizzes() {
        List<Quiz> quizzes = quizRepository.findAll();
        return quizzes.stream().map(quizMapper::toQuizResponse).collect(Collectors.toList());
    }

    @Override
    public List<QuizResponse> getQuizzesByCourseId(Long courseId) {
        List<Quiz> quizzes = quizRepository.findByCourseId(courseId);
        return quizzes.stream().map(quizMapper::mapToQuizResponse)  // Use method reference to map each quiz
                .collect(Collectors.toList());
    }

    @Override
    public List<QuestionCorrect> getQuestionsWithCorrectOptions(Long quizId) {
        List<Question> questions = questionRepository.findAllByQuizId(quizId);
        return questions.stream().map(question -> {
            List<Integer> correctOptionIds = question.getOptions().stream().filter(Option::isCorrect).map(Option::getId).collect(Collectors.toList());
            return new QuestionCorrect(question.getId(), correctOptionIds);
        }).collect(Collectors.toList());
    }


    @Override
    public SubmitAnswerResponse evaluateQuiz(Integer quizId, List<SubmitAnswerRequest> answers, String candidateUsername) {
        Quiz quiz = quizRepository.findById(Long.valueOf(quizId)).orElseThrow(() -> new RuntimeException("Quiz not found"));

        User user = userService.getUserByEmail(candidateUsername);

        List<Question> questions = questionRepository.findAllByQuizId(Long.valueOf(quiz.getId()));
        int totalQuestions = questions.size();
        int correctAnswers = 0;

        List<QuestionCorrect> correctOptionsList = getQuestionsWithCorrectOptions(Long.valueOf(quizId));
        Map<Integer, List<Integer>> correctOptionsMap = correctOptionsList.stream().collect(Collectors.toMap(QuestionCorrect::getQuetionId, QuestionCorrect::getCorrectOptionIds));

        List<SubmitAnswerResponse.QuestionResult> questionResults = new ArrayList<>();

        for (SubmitAnswerRequest answer : answers) {
            Integer questionId = answer.getQuestionId();
            List<Integer> submittedOptionIds = answer.getSelectedOptionIds();
            List<Integer> correctOptionIds = correctOptionsMap.get(questionId);

            boolean isCorrect = submittedOptionIds.containsAll(correctOptionIds) && correctOptionIds.containsAll(submittedOptionIds);

            if (isCorrect) {
                correctAnswers++;
            }

            // Save the candidate responses
            for (Integer optionId : submittedOptionIds) {
                Option option = optionRepository.findById(optionId).orElseThrow(() -> new RuntimeException("Option not found"));

                CandidateResponse candidateResponse = new CandidateResponse();
                candidateResponse.setUser(user);
                candidateResponse.setQuestion(option.getQuestion());
                candidateResponse.setOption(option);
                candidateResponse.setCorrect(option.isCorrect());

                candidateResponseRepository.save(candidateResponse);
            }

            SubmitAnswerResponse.QuestionResult questionResult = new SubmitAnswerResponse.QuestionResult(questionId, correctOptionIds, submittedOptionIds);
            questionResults.add(questionResult);
        }

        double rawPercentage = (double) correctAnswers / totalQuestions * 100;
        int roundedPercentage = (int) Math.round(rawPercentage);
        int score = correctAnswers;

        // Save quiz result
        QuizResult quizResult = new QuizResult();
        quizResult.setScore(score);
        quizResult.setPercentage(roundedPercentage);
        quizResult.setQuiz(quiz);
        quizResult.setCandidate(user);
        quizResultRepository.save(quizResult);

        // Save or update user quiz completion
        UserQuizCompletion userQuizCompletion = userQuizCompletionRepository.findByUserAndQuiz(user, quiz).orElse(new UserQuizCompletion());
        userQuizCompletion.setUser(user);
        userQuizCompletion.setQuiz(quiz);
        userQuizCompletion.setCompleted(true); // Mark as completed
        userQuizCompletionRepository.save(userQuizCompletion);

        return new SubmitAnswerResponse(score, roundedPercentage, questionResults);
    }


}