package com.pfe.elearning.MeetingSchedule.controller;

import com.pfe.elearning.MeetingSchedule.dto.MeetingScheduleRequest;
import com.pfe.elearning.MeetingSchedule.dto.MeetingScheduleResponse;
import com.pfe.elearning.MeetingSchedule.service.MeetingScheduleService;
import com.pfe.elearning.common.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meeting-schedules")
@RequiredArgsConstructor
public class MeetingScheduleController {

    private final MeetingScheduleService meetingScheduleService;

    @PostMapping
    public ResponseEntity<MeetingScheduleResponse> createMeetingSchedule(
            @RequestBody @Valid MeetingScheduleRequest meetingScheduleRequest) {
        // Get authenticated user (instructor)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String instructorUsername = authentication.getName();

        MeetingScheduleResponse response = meetingScheduleService.createMeetingSchedule(meetingScheduleRequest, instructorUsername);
        System.out.println("email of instructor: " + instructorUsername);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MeetingScheduleResponse>> getAllMeetingSchedules() {
        List<MeetingScheduleResponse> meetingSchedules = meetingScheduleService.getAllMeetingSchedules();
        return new ResponseEntity<>(meetingSchedules, HttpStatus.OK);
    }



    @GetMapping("/instructor-paginated")
    public ResponseEntity<PageResponse<MeetingScheduleResponse>> getAuthenticatedInstructorMeetingSchedules(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        // Get authenticated user (instructor)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String instructorUsername = authentication.getName();

        // Retrieve meeting schedules for the instructor
        PageResponse<MeetingScheduleResponse> meetingSchedules = meetingScheduleService.getInstructorMeetingSchedules(instructorUsername, page, size);
        return ResponseEntity.ok(meetingSchedules);
    }
    @GetMapping("/instructor")
    public ResponseEntity<List<MeetingScheduleResponse>> getAuthenticatedInstructorMeetingSchedules() {
        // Get authenticated user (instructor)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String instructorUsername = authentication.getName();

        // Retrieve meeting schedules for the instructor
        List<MeetingScheduleResponse> meetingSchedules = meetingScheduleService.getInstructorMeetingSchedules(instructorUsername);
        return ResponseEntity.ok(meetingSchedules);
    }
/*
    @GetMapping("/{id}")
    public ResponseEntity<MeetingScheduleResponse> getMeetingScheduleById(@PathVariable Integer id) {
        MeetingScheduleResponse response = meetingScheduleService.getMeetingScheduleById(id);
        return ResponseEntity.ok(response);
    }

 */

    @PutMapping("/{id}")
    public ResponseEntity<MeetingScheduleResponse> updateMeetingSchedule(
            @PathVariable Integer id,
            @RequestBody @Valid MeetingScheduleRequest meetingScheduleRequest) {
        MeetingScheduleResponse response = meetingScheduleService.updateMeetingSchedule(id, meetingScheduleRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeetingSchedule(@PathVariable Integer id) {
        meetingScheduleService.deleteMeetingSchedule(id);
        return ResponseEntity.noContent().build();
    }
   /* @GetMapping("/search")
    public ResponseEntity<PageResponse<MeetingScheduleResponse>> searchMeetingSchedules(
            @RequestParam("searchTerm") String searchTerm,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        // Perform the search and retrieve the page response
        PageResponse<MeetingScheduleResponse> meetingSchedules = meetingScheduleService.searchMeetingSchedules(searchTerm, page, size);

        return ResponseEntity.ok(meetingSchedules);
    }

    */
    @GetMapping("/search")
    public ResponseEntity<PageResponse<MeetingScheduleResponse>> searchMeetingSchedules(
            @RequestParam("searchTerm") String searchTerm,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        // Perform the search and retrieve the page response
        PageResponse<MeetingScheduleResponse> meetingSchedules = meetingScheduleService.searchMeetingSchedulesByAuthenticatedUser(searchTerm, page, size);

        return ResponseEntity.ok(meetingSchedules);
    }

    /** candidate **/
    @GetMapping("/candidate")
    public ResponseEntity<PageResponse<MeetingScheduleResponse>> getMeetingsForAuthenticatedCandidate(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        // Get authenticated candidate's email
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String candidateEmail = authentication.getName();

        // Retrieve meeting schedules for the authenticated candidate
        PageResponse<MeetingScheduleResponse> meetingSchedules = meetingScheduleService.getMeetingsForCandidate(candidateEmail, page, size);

        return ResponseEntity.ok(meetingSchedules);
    }
    @GetMapping("/{id}")
    public ResponseEntity<MeetingScheduleResponse> getMeetingScheduleById(@PathVariable Integer id) {
        MeetingScheduleResponse response = meetingScheduleService.getMeetingScheduleById(id);
        return ResponseEntity.ok(response);
    }

}
