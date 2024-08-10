package com.pfe.elearning.forum.answer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerRequest {
    private Integer parentAnswerId;
    private String messageContent;
}