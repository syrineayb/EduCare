package com.pfe.elearning.quiz.entity;

import com.pfe.elearning.QuizResult.entity.QuizResult;
import com.pfe.elearning.common.BaseEntity;
import com.pfe.elearning.course.entity.Course;
import com.pfe.elearning.question.entity.Question;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Quiz extends BaseEntity {
    private String title;
    private int duration;
private int mark;
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;


    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
    private List<Question> questions;


    @OneToMany(mappedBy = "quiz")
    private List<QuizResult> quizResults;

}