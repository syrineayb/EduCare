package com.pfe.elearning.MeetingSchedule.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
public class MeetingScheduleRequest {

    private String meetingTitle;
    private Integer durationMinutes;
    //@JsonFormat(pattern = "yyyy-MM-dd")
    private String dateTime;
    private String meetingUrl;
    private String meetingDescription;
    private Integer instructorId;
    private Integer courseId;
    private Integer lessonId; // Lesson ID selected by the instructor
}
