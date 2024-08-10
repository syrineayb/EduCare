package com.pfe.elearning.topic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//@Builder
public class TopicRequest {
    private String title;
    private MultipartFile imageFile; // Add image file field


}
