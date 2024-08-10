package com.pfe.elearning.course.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class CandidateCourseResponse {
    private Integer userId;
    private Integer courseId;


}
