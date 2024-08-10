package com.pfe.elearning.question.entity;

import com.pfe.elearning.Option.entity.Option;
import com.pfe.elearning.common.BaseEntity;
import com.pfe.elearning.quiz.entity.Quiz;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "questions")
public class Question extends BaseEntity {
    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    private QuestionType type;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Option> options;
}