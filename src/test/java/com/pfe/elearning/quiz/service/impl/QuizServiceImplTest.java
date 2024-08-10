package com.pfe.elearning.quiz.service.impl;
import com.pfe.elearning.quiz.entity.Quiz;
import com.pfe.elearning.quiz.repository.QuizRepository;
import com.pfe.elearning.quiz.service.impl.QuizServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.webjars.NotFoundException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuizServiceImplTest {
    @Mock
    private QuizRepository quizRepository;

    @InjectMocks
    private QuizServiceImpl quizService;
    @Test
    public void testDeleteNonExistentQuiz() {
        // Given
        long nonExistentQuizId = 1; // ID of a non-existent quiz
        when(quizRepository.findById(nonExistentQuizId)).thenThrow(NotFoundException.class);

        // Then
        assertThrows(NotFoundException.class, () -> quizService.deleteQuiz(nonExistentQuizId));
    }
}
