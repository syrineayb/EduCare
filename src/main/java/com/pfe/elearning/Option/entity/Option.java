package com.pfe.elearning.Option.entity;

import com.pfe.elearning.common.BaseEntity;
import com.pfe.elearning.question.entity.Question;
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
@Table(name = "options")
public class Option extends BaseEntity {

    private String text;
    private boolean correct;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

}
