package com.pfe.elearning.MeetingSchedule.dto;

import com.pfe.elearning.MeetingSchedule.MeetingSchedule;
import org.springframework.stereotype.Service;

@Service
public class MeetingScheduleMapper {

    public MeetingScheduleResponse toMeetingScheduleResponse(MeetingSchedule meetingSchedule) {
        Integer courseId = meetingSchedule.getCourse() != null ? meetingSchedule.getCourse().getId() : null;
        String courseTitle = meetingSchedule.getCourse() != null ? meetingSchedule.getCourse().getTitle() : null;

        Integer lessonId = meetingSchedule.getLesson() != null ? meetingSchedule.getLesson().getId() : null;
        String lessonTitle = meetingSchedule.getLesson() != null ? meetingSchedule.getLesson().getTitle() : null;

        return MeetingScheduleResponse.builder()
                .id(meetingSchedule.getId())
                .meetingTitle(meetingSchedule.getMeetingTitle())
                .meetingDescription(meetingSchedule.getMeetingDescription())
                .durationMinutes(meetingSchedule.getDurationMinutes())
                .dateTime(meetingSchedule.getDateTime())
                .meetingUrl(meetingSchedule.getMeetingUrl())
                .courseId(courseId)
                .courseTitle(courseTitle)
                .instructorFullName(meetingSchedule.getInstructor().getFullname())
                .lessonId(lessonId)
                .lessonTitle(lessonTitle)
                .createdAt(meetingSchedule.getCreatedAt())
                .updatedAt(meetingSchedule.getUpdatedAt())
                .build();
    }

    public MeetingSchedule toMeetingSchedule(MeetingScheduleRequest meetingScheduleRequest) {
        MeetingSchedule meetingSchedule = new MeetingSchedule();
        meetingSchedule.setMeetingTitle(meetingScheduleRequest.getMeetingTitle());
        meetingSchedule.setMeetingDescription(meetingScheduleRequest.getMeetingDescription());
        meetingSchedule.setMeetingUrl(meetingScheduleRequest.getMeetingUrl());
        meetingSchedule.setDurationMinutes(meetingScheduleRequest.getDurationMinutes());
        meetingSchedule.setDateTime(meetingScheduleRequest.getDateTime());
     //   meetingSchedule.setDateTime(meetingScheduleRequest.getDateTime());
        return meetingSchedule;
    }
    public void updateEntityFromRequest(MeetingScheduleRequest meetingScheduleRequest, MeetingSchedule meetingSchedule) {
        if (meetingScheduleRequest.getMeetingTitle() != null) {
            meetingSchedule.setMeetingTitle(meetingScheduleRequest.getMeetingTitle());
        }
        if (meetingScheduleRequest.getMeetingDescription() != null) {
            meetingSchedule.setMeetingDescription(meetingScheduleRequest.getMeetingDescription());
        }
        if (meetingScheduleRequest.getMeetingUrl() != null) {
            meetingSchedule.setMeetingUrl(meetingScheduleRequest.getMeetingUrl());
        }
        if (meetingScheduleRequest.getDurationMinutes() != null) {
            meetingSchedule.setDurationMinutes(meetingScheduleRequest.getDurationMinutes());
        }
        if (meetingScheduleRequest.getDateTime() != null) {
            meetingSchedule.setDateTime(meetingScheduleRequest.getDateTime());
        }
    }
}
