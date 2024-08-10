package com.pfe.elearning.QuizResult.entity;

import com.pfe.elearning.common.BaseEntity;
import com.pfe.elearning.quiz.entity.Quiz;
import com.pfe.elearning.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "quiz_results")
public class QuizResult extends BaseEntity {
    private int score;
    private double percentage;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User candidate;
}
