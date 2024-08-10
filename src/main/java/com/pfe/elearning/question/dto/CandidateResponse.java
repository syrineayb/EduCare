package com.pfe.elearning.question.dto;

import com.pfe.elearning.Option.entity.Option;
import com.pfe.elearning.question.entity.Question;
import com.pfe.elearning.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class CandidateResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Question question;

    @ManyToOne
    private Option option;

    private boolean correct;

    // Getters and setters
}

