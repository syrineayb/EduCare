package com.pfe.elearning.MeetingSchedule.service.serviceImpl;

import com.pfe.elearning.MeetingSchedule.MeetingSchedule;
import com.pfe.elearning.MeetingSchedule.MeetingScheduleRepository;
import com.pfe.elearning.MeetingSchedule.dto.MeetingScheduleMapper;
import com.pfe.elearning.MeetingSchedule.dto.MeetingScheduleRequest;
import com.pfe.elearning.MeetingSchedule.dto.MeetingScheduleResponse;
import com.pfe.elearning.common.PageResponse;
import com.pfe.elearning.course.entity.Course;
import com.pfe.elearning.course.repository.CourseRepository;
import com.pfe.elearning.email.EmailService;
import com.pfe.elearning.lesson.entity.Lesson;
import com.pfe.elearning.lesson.repository.LessonRepository;
import com.pfe.elearning.user.entity.User;
import com.pfe.elearning.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MeetingScheduleServiceImplTest {

    @Mock
    private MeetingScheduleRepository meetingScheduleRepository;
    @Mock
    private MeetingScheduleMapper meetingScheduleMapper;
    @Mock
    private UserRepository userRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private MeetingScheduleServiceImpl meetingScheduleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

        @Test
        void testCreateSecurityMeetingSchedule() {
            // Mocking necessary entities
            User instructor = new User();
            instructor.setEmail("instructor@example.com");
            instructor.setFullname("Isaa Malloug");

            Course course = new Course();
            course.setId(1);
            course.setTitle("Security Fundamentals");

            MeetingScheduleRequest request = new MeetingScheduleRequest();
            request.setMeetingTitle("Introduction to Cybersecurity");
            request.setDurationMinutes(90);
           // request.setDateTime(LocalDateTime.now());
            request.setDateTime("12-06-2024");
            request.setMeetingUrl("https://meet.jit.si/SecurityMeeting");
            request.setMeetingDescription("An introductory session on cybersecurity covering basic concepts and practices.");
            request.setCourseId(1);

            // Mocking repository method calls
            when(userRepository.findByEmail(any())).thenReturn(Optional.of(instructor));
            when(courseRepository.findById(anyInt())).thenReturn(Optional.of(course));
            MeetingSchedule savedMeetingSchedule = new MeetingSchedule();
            savedMeetingSchedule.setId(1);
            when(meetingScheduleRepository.save(any())).thenReturn(savedMeetingSchedule);
            when(userRepository.findCandidateEmailsByCourseId(anyInt())).thenReturn(Collections.singletonList("student@example.com"));

            // Call the method
            MeetingScheduleResponse response = meetingScheduleService.createMeetingSchedule(request, "instructor@example.com");

            // Verify that the meeting schedule was saved
            verify(meetingScheduleRepository, times(1)).save(any());

            // Verify that email was sent to enrolled candidates
            verify(emailService, times(1)).sendEmailToCandidate(any(), any(), any(), any(), any());
        }

    @Test
    void testGetInstructorMeetingSchedules() {
        // Mock the instructor
        User instructor = new User();
        instructor.setEmail("instructor@example.com");

        // Mock the list of meeting schedules for the instructor
        List<MeetingSchedule> instructorMeetingSchedules = Arrays.asList(
                new MeetingSchedule(),
                new MeetingSchedule()
        );
        Page<MeetingSchedule> page = new PageImpl<>(instructorMeetingSchedules);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(instructor));
        when(meetingScheduleRepository.findByInstructor(any(), any(Pageable.class))).thenReturn(page);

        // Call the method
        PageResponse<MeetingScheduleResponse> response = meetingScheduleService.getInstructorMeetingSchedules
                ("instructor@example.com", 0, 10);

        // Verify that all meeting schedules for the instructor are retrieved
        assertEquals(2, response.getContent().size());
    }
    @Test
    void testCreateMeetingSchedule_CourseNotFound() {
        // Mocking repository method calls to return an empty optional for course
        when(courseRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Creating a meeting schedule request
        MeetingScheduleRequest request = new MeetingScheduleRequest();
        request.setMeetingTitle("Test Meeting");
        request.setDurationMinutes(60);
       // request.setDateTime(LocalDateTime.now());
        request.setDateTime("12-06-2024");
        request.setMeetingUrl("https://meet.jit.si/TestMeeting");
        request.setMeetingDescription("Test Meeting Description");
        request.setCourseId(1);

        // Call the method and expect EntityNotFoundException
        assertThrows(EntityNotFoundException.class, () -> meetingScheduleService.createMeetingSchedule
                (request, "instructor@example.com"));
    }

}