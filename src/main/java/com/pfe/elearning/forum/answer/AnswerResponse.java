package com.pfe.elearning.forum.answer;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerResponse {
    private Integer id;
    private Integer messageId;
    private Integer forumId;
    private String messageContent;
    private String message;
    private Integer authorId;
    private Integer parentAnswerId;
    private String authorFullName; // Assuming you want to include the author's name
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<AnswerResponse> replies; // Liste pour stocker les réponses des réponses
}