package com.pfe.elearning.QuizResult.controller;

import com.pfe.elearning.QuizResult.dto.UserQuizProgressResponse;
import com.pfe.elearning.QuizResult.service.QuizResultService;
import com.pfe.elearning.UserResourceCompletion.UserLessonProgressResponse;
import com.pfe.elearning.quiz.service.QuizService;
import com.pfe.elearning.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/quiz-progress")
@RequiredArgsConstructor
public class QuizProgressController {

    private final QuizResultService quizResultService;
    private final UserService userService;

    @GetMapping("/user/{courseId}")
    public ResponseEntity<UserQuizProgressResponse> getUserQuizProgress(@PathVariable Long courseId, @AuthenticationPrincipal UserDetails userDetails) {
        String candidateUsername = userDetails.getUsername();
        int progress = quizResultService.getUserQuizProgress(courseId, candidateUsername);
        return ResponseEntity.ok(new UserQuizProgressResponse(progress));
    }



}