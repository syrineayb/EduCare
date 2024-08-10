package com.pfe.elearning.forum.answer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReplyResponse {
    private Integer id;
    private String messageContent;
    private Integer authorId;
    private String authorFullName;
    private Integer parentAnswerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ReplyResponse> replies;

}
