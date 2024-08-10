package com.pfe.elearning.MeetingSchedule.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.Duration;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetingScheduleResponse {

    private Integer id;
    private String meetingTitle;
    private Integer durationMinutes;
 //   @JsonFormat(pattern = "yyyy-MM-dd")
  //  private LocalDateTime dateTime;
  private String dateTime;

    private String meetingUrl;
    private String meetingDescription;
    private Integer courseId; // Course ID
    private String courseTitle; // Course title
    private String instructorFullName;
    private Integer lessonId; // Lesson ID
    private String lessonTitle; // Lesson title
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
