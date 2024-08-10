package com.pfe.elearning.CourseEnrollment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseEnrollmentResponse {
    private Long id; // Assuming id is the primary key of CourseEnrollment entity
    private Long publisherId;
    private Long candidateId;
    private String candidateName;
    private String candidateLastname;
    private String candidateImage;
    private Long courseId;
    private String courseName;
    private int progress; // Add this field
    private int quizProgress;

}