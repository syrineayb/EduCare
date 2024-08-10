package com.pfe.elearning.resource.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ResourceRequest {
    private MultipartFile imageFile;
    private MultipartFile pdfFile;
    private MultipartFile videoFile;
}
