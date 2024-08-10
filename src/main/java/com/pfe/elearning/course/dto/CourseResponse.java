package com.pfe.elearning.course.dto;

import com.pfe.elearning.lesson.dto.LessonResponse;
import com.pfe.elearning.topic.dto.TopicResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class CourseResponse {
    private Integer id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer topicId;
    private String topicTitle;
    private String imageFile;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean free;
    private Float price;
    private String timeCommitment;
    private String publisherName;
    //private TopicResponse topic;
    private List<LessonResponse> lessons;


    private int courseCount;



}