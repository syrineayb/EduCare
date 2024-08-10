package com.pfe.elearning.MeetingSchedule.service;

import com.pfe.elearning.MeetingSchedule.MeetingSchedule;
import com.pfe.elearning.MeetingSchedule.dto.MeetingScheduleRequest;
import com.pfe.elearning.MeetingSchedule.dto.MeetingScheduleResponse;
import com.pfe.elearning.common.PageResponse;

import java.util.List;

public interface MeetingScheduleService {
    MeetingScheduleResponse createMeetingSchedule(MeetingScheduleRequest meetingScheduleRequest,String instructor_name);
    List<MeetingScheduleResponse> getAllMeetingSchedules();
    PageResponse<MeetingScheduleResponse> getInstructorMeetingSchedules(String instructorUsername, int page, int size);
    MeetingScheduleResponse updateMeetingSchedule(Integer id, MeetingScheduleRequest meetingScheduleRequest);
    void deleteMeetingSchedule(Integer id);
    MeetingScheduleResponse getMeetingScheduleById(Integer id);
    PageResponse<MeetingScheduleResponse> searchMeetingSchedulesByAuthenticatedUser(String searchTerm, int page, int size);
    //PageResponse<MeetingScheduleResponse> searchMeetingSchedulesForAuthenticatedUser(String searchTerm, int page, int size);


    PageResponse<MeetingScheduleResponse> getMeetingsForCandidate(String candidateEmail, int page, int size);
    List<MeetingScheduleResponse> getInstructorMeetingSchedules(String instructorUsername);

}
