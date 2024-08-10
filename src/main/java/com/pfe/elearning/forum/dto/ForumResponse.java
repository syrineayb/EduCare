package com.pfe.elearning.forum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForumResponse {
    private Integer id;
    private String title;
    private String lessonDescription;
    private String courseTitle;
    private int courseId;
}
