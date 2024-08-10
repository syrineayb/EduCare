package com.pfe.elearning.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceResponse {
    private Integer id;
    private String imageFile;
    private String pdfFile;
    private String videoFile;
    private String lessonTitle;
}
