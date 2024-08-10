package com.pfe.elearning.forum.Message;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class MessageRequest {
    private String subject;
    //private MessageRequest message;
    private Integer forumId; // Ensure this field is present
    private String messageText;
    private MultipartFile recordFile;
    private MultipartFile imageFile;
    private MultipartFile videoFile;
    private MultipartFile pdfFile;
    private int authorId;

    //private String message;
 //   private Integer forumId;
 //   private Integer authorId; // ID of the user creating the discussion
}