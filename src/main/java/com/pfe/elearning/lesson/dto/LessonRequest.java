package com.pfe.elearning.lesson.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class LessonRequest {
    private String title;
    private String description;
   // private Integer courseId;
 //   private List<MultipartFile> resources  ;
}
