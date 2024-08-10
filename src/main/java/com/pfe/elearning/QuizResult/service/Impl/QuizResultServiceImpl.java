package com.pfe.elearning.QuizResult.service.Impl;

import com.pfe.elearning.QuizResult.dto.QuizResultMapper;
import com.pfe.elearning.QuizResult.dto.QuizResultResponse;
import com.pfe.elearning.QuizResult.repository.QuizResultRepository;
import com.pfe.elearning.QuizResult.repository.UserQuizCompletionRepository;
import com.pfe.elearning.QuizResult.service.QuizResultService;
import com.pfe.elearning.quiz.repository.QuizRepository;
import com.pfe.elearning.user.entity.User;
import com.pfe.elearning.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizResultServiceImpl implements QuizResultService {
    private final QuizResultRepository quizResultRepository;
    private final QuizResultMapper quizResultMapper;
    private final UserService userService;
    private final QuizRepository quizRepository;
    private final UserQuizCompletionRepository userQuizCompletionRepository;
    @Override
    public List<QuizResultResponse> getAllQuizResults() {
        return quizResultRepository.findAll().stream()
                .map(quizResultMapper::toQuizResultResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuizResultResponse> getResultsByCandidateId(String candidateUsername) {
        User user = userService.getUserByEmail(candidateUsername);
        return quizResultRepository.findByCandidateId(user.getId()).stream()
                .map(quizResultMapper::toQuizResultResponse)
                .collect(Collectors.toList());
    }

    @Override
    public int getUserQuizProgress(Long courseId, String candidateUsername) {
        User user = userService.getUserByEmail(candidateUsername);

        // Find the total number of quizzes in the course
        long totalQuizzes = quizRepository.countByCourseId(courseId);

        // Find the total number of completed quizzes by the user in the course
        long completedCount = userQuizCompletionRepository.countByUserAndQuiz_CourseIdAndCompletedTrue(user, courseId);
        if (totalQuizzes == 0) {
            return 0;
        }
        // Calculate progress percentage
        double QuizProgress = ((double) completedCount / totalQuizzes) * 100;

        return (int) Math.round(QuizProgress);
    }
}