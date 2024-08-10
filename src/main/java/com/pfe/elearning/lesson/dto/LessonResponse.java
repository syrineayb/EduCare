package com.pfe.elearning.lesson.dto;

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
public class LessonResponse {
    private Integer id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String courseTitle;



    //  private List<String> resourceTitles; // List of resource titles associated with the lesson
 //   private String publisherName; // Nouveau champ pour le nom de l'éditeur
}
