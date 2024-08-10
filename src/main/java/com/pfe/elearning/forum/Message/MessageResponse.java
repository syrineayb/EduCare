package com.pfe.elearning.forum.Message;

import com.pfe.elearning.forum.answer.AnswerResponse;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private Integer id;
    private String subject;
    private String messageText;
    private String recordUrl;
    private String imageUrl;
    private String videoUrl;
    private String pdfUrl;
  //  private MessageResponse message; // Ensure this field is present
    private String lessonTitle;
    //private String message;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer authorId;
    private String authorFullName; // Username of the discussion creator
    private Integer forumId;
    private List<AnswerResponse> answers; // Add list of answers

}