package com.pfe.elearning.QuizResult.entity;

import com.pfe.elearning.quiz.entity.Quiz;
import com.pfe.elearning.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserQuizCompletion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "Quiz_id", nullable = false)
    private Quiz quiz;

    private boolean completed;
}