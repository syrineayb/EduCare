package com.pfe.elearning.quiz.dto;

import com.pfe.elearning.Option.dto.OptionResponse;
import com.pfe.elearning.course.entity.Course;
import com.pfe.elearning.question.dto.QuestionResponse;
import com.pfe.elearning.question.entity.Question;
import com.pfe.elearning.quiz.entity.Quiz;
import com.pfe.elearning.topic.dto.TopicResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizMapper {

    public QuizResponse toQuizResponse(Quiz quiz) {
        return QuizResponse.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .duration(quiz.getDuration())
                .courseId(quiz.getCourse().getId())
                .courseTitle(quiz.getCourse().getTitle())
                .mark(quiz.getMark())
                .build();
    }

    public QuizResponse mapToQuizResponse(Quiz quiz) {
        return QuizResponse.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .duration(quiz.getDuration())
                .courseTitle(quiz.getCourse().getTitle()) // Populate the course title
                .mark(quiz.getMark())
                .build();
    }

    public List<QuestionResponse> getAllQuestionsByQuizAndCourseId(Quiz quiz, Integer courseId) {
        return quiz.getQuestions().stream()
                .filter(question -> question.getQuiz().getId().equals(quiz.getId()) && question.getQuiz().getCourse().getId().equals(courseId))
                .map(question -> {
                    // Map question options
                    List<OptionResponse> optionResponses = question.getOptions().stream()
                            .map(option -> new OptionResponse(option.getId(), option.getText(), option.isCorrect()))
                            .collect(Collectors.toList());

                    // Create QuestionResponse
                    return new QuestionResponse(
                            Long.valueOf(question.getId()),
                            question.getTitle(),
                            question.getType(),
                            Long.valueOf(question.getQuiz().getId()),
                            question.getQuiz().getTitle(),
                            question.getQuiz().getDuration(),
                            question.getQuiz().getMark(),
                            question.getQuiz().getCourse().getId(),
                            optionResponses
                    );
                })
                .collect(Collectors.toList());
    }



    public Quiz toQuiz(QuizRequest quizRequest) {
        Quiz quiz = new Quiz();
        quiz.setTitle(quizRequest.getTitle());
        quiz.setDuration(quizRequest.getDuration());
        quiz.setMark(quizRequest.getMark());
        Course course = new Course();
        quiz.setCourse(course);
        return quiz;
    }

}