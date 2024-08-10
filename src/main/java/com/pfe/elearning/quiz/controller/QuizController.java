package com.pfe.elearning.quiz.controller;

import com.pfe.elearning.QuizResult.dto.QuizResultResponse;
import com.pfe.elearning.QuizResult.entity.QuizResult;
import com.pfe.elearning.QuizResult.service.QuizResultService;
import com.pfe.elearning.question.dto.QuestionResponse;
import com.pfe.elearning.question.dto.SubmitAnswerRequest;
import com.pfe.elearning.question.dto.SubmitAnswerResponse;
import com.pfe.elearning.quiz.dto.QuizMapper;
import com.pfe.elearning.quiz.dto.QuizRequest;
import com.pfe.elearning.quiz.dto.QuizResponse;
import com.pfe.elearning.quiz.entity.Quiz;
import com.pfe.elearning.quiz.repository.QuizRepository;
import com.pfe.elearning.quiz.service.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController {
    private final QuizResultService quizResultService;
    private final QuizService quizService;
    private final QuizMapper quizMapper;
    private final QuizRepository quizRepository;
    @PostMapping("/course/{courseId}")
    public ResponseEntity<QuizResponse> createQuizForCourse(@PathVariable Integer courseId,
                                                            @RequestBody @Valid QuizRequest request) {
        QuizResponse quizResponse = quizService.createQuiz(request, courseId);
        return ResponseEntity.ok(quizResponse);
    }
    @GetMapping("/{quizId}")
    public ResponseEntity<QuizResponse> getQuizById(@PathVariable Long quizId) {
        QuizResponse quizResponse = quizService.getQuizById(quizId);
        return ResponseEntity.ok(quizResponse);
    }


    @PutMapping("/{quizId}")
    public ResponseEntity<QuizResponse> updateQuiz(@PathVariable Long quizId,
                                                   @RequestBody QuizRequest quizRequest) {
        QuizResponse updatedQuiz = quizService.updateQuiz(quizId, quizRequest);
        return ResponseEntity.ok(updatedQuiz);
    }

    @DeleteMapping("/{quizId}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long quizId) {
        quizService.deleteQuiz(quizId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/getAll")
    public ResponseEntity<List<QuizResponse>> getAllQuizzes() {
        List<QuizResponse> quizzes = quizService.getAllQuizzes();
        return ResponseEntity.ok(quizzes);
    }
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<QuizResponse>> getQuizzesByCourseId(@PathVariable Long courseId) {
        List<QuizResponse> quizzes = quizService.getQuizzesByCourseId(courseId);
        return ResponseEntity.ok(quizzes);
    }
    @PostMapping("/{quizId}/evaluateQuiz")
    public ResponseEntity<SubmitAnswerResponse> evaluateQuiz(
            @PathVariable Integer quizId,
            @RequestBody List<SubmitAnswerRequest> answers,
            @AuthenticationPrincipal UserDetails userDetails) {
        String candidateUsername = userDetails.getUsername();

        try {
            SubmitAnswerResponse response = quizService.evaluateQuiz(quizId, answers, candidateUsername);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @PostMapping("/course/{courseId}/add")
    public ResponseEntity<QuizResponse> createQuizWithQuestions(
            @PathVariable Integer courseId,
            @RequestBody QuizRequest request) {
        QuizResponse quizResponse = quizService.createQuizWithQuestions(request, courseId);
        return ResponseEntity.ok(quizResponse);
    }

    @GetMapping("/questions/course/{courseId}")
    public List<QuestionResponse> getAllQuestionsByCourseId(@PathVariable Integer courseId) {
        return quizService.getAllQuestionsByCourseId(courseId);
    }

    @GetMapping("/results")
    public ResponseEntity<List<QuizResultResponse>> getResultsByCandidateId(@AuthenticationPrincipal UserDetails userDetails) {
        String candidateUsername = userDetails.getUsername();
        List<QuizResultResponse> results = quizResultService.getResultsByCandidateId(candidateUsername);
        return ResponseEntity.ok(results);
    }

//    @GetMapping("/{quizId}/randomQuestions")
//    public ResponseEntity<List<QuestionResponse>> getRandomQuestionsByQuizId(
//            @PathVariable Long quizId,
//            @RequestParam(name = "count", defaultValue = "5") int count) {
//        List<QuestionResponse> randomQuestions = quizService.getRandomQuestionsByQuizId(quizId, count);
//        return ResponseEntity.ok(randomQuestions);
//    }
}