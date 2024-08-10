package com.pfe.elearning.contact.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactResponse {
    private Integer id;
    private String name;
    private String email;
    private String subject;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}