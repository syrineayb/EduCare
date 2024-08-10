package com.pfe.elearning.topic.dto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class TopicResponse {
    private Integer id;
    private String title;
    private String imageFile;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    public TopicResponse(Integer id, String title,String imageFile, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.imageFile=imageFile;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

}
