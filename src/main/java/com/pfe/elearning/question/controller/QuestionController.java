package com.pfe.elearning.question.controller;


import com.pfe.elearning.common.PageResponse;
import com.pfe.elearning.course.repository.CourseRepository;
import com.pfe.elearning.question.dto.*;
import com.pfe.elearning.question.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private final QuestionMapper questionMapper; // Assuming you have a QuestionMapper

    @PostMapping("/quiz/{quizId}")
    public ResponseEntity<QuestionResponse> addQuestionToQuiz(@PathVariable Long quizId, @Valid @RequestBody QuestionRequest questionRequest) {
        QuestionResponse addedQuestion = questionService.addQuestionToQuiz(quizId, questionRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedQuestion);
    }


    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<List<QuestionResponse>> getAllQuestionsByQuizId(@PathVariable Long quizId) {
        List<QuestionResponse> questions = questionService.getAllQuestionsByQuizId(quizId);
        return ResponseEntity.ok(questions);
    }
    @GetMapping("/pagable/quiz/{quizId}")
    public ResponseEntity<?> getAllQuestionsByQuizId(
            @PathVariable Long quizId,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "4", required = false) int size) {

        try {
            PageResponse<QuestionResponse> questionsPage = questionService.getAllQuestionsByQuizId(quizId, page, size);
            return ResponseEntity.ok(questionsPage);
        } catch (Exception ex) {
            // Log the exception and return an error response
            String errorMessage = "Error occurred while retrieving questions: " + ex.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorMessage);
        }
    }


    @GetMapping("/candidate/{quizId}")
    public ResponseEntity<List<QuestionResponse>> getAllQuestionsForCandidate(@PathVariable Long quizId) {
        List<QuestionResponse> questions = questionService.getAllQuestionsForCandidate(quizId);
        return ResponseEntity.ok(questions);
    }

    @PutMapping("/{questionId}")
    public ResponseEntity<QuestionResponse> updateQuestion(@PathVariable Long questionId, @RequestBody QuestionRequest questionRequest) {
        QuestionResponse updatedQuestion = questionService.updateQuestion(questionId, questionRequest);
        if (updatedQuestion != null) {
            return ResponseEntity.ok(updatedQuestion);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/course/{courseId}")
    public List<QuestionResponse> getAllQuestionsByCourseId(@PathVariable Integer courseId) {
        return questionService.getAllQuestionsByCourseId(courseId);
    }
    @GetMapping("/quiz/{quizId}/correct-options")
    public ResponseEntity<List<QuestionCorrect>> getQuestionsWithCorrectOptions(@PathVariable Long quizId) {
        List<QuestionCorrect> questionsWithCorrectOptions = questionService.getQuestionsWithCorrectOptions(quizId);
        return ResponseEntity.ok(questionsWithCorrectOptions);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
            questionService.deleteById(id);
        return ResponseEntity.noContent().build();

    }



}