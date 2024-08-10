package com.pfe.elearning.MeetingSchedule.service.serviceImpl;

import com.pfe.elearning.MeetingSchedule.MeetingSchedule;
import com.pfe.elearning.MeetingSchedule.MeetingScheduleRepository;
import com.pfe.elearning.MeetingSchedule.dto.MeetingScheduleMapper;
import com.pfe.elearning.MeetingSchedule.dto.MeetingScheduleRequest;
import com.pfe.elearning.MeetingSchedule.dto.MeetingScheduleResponse;
import com.pfe.elearning.MeetingSchedule.service.MeetingScheduleService;
import com.pfe.elearning.common.PageResponse;
import com.pfe.elearning.course.entity.Course;
import com.pfe.elearning.course.repository.CourseRepository;
import com.pfe.elearning.email.EmailService;
import com.pfe.elearning.lesson.entity.Lesson;
import com.pfe.elearning.lesson.repository.LessonRepository;
import com.pfe.elearning.user.entity.User;
import com.pfe.elearning.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeetingScheduleServiceImpl implements MeetingScheduleService {

    private final MeetingScheduleRepository meetingScheduleRepository;
    private final MeetingScheduleMapper meetingScheduleMapper;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final EmailService emailService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private Map<Integer, String> meetingScheduleEmailContentMap = new HashMap<>();

    @Override
    public MeetingScheduleResponse createMeetingSchedule(MeetingScheduleRequest meetingScheduleRequest, String instructorUsername) {
        // Get the instructor
        User instructor = userRepository.findByEmail(instructorUsername)
                .orElseThrow(() -> new EntityNotFoundException("Instructor not found"));

        // Get the course
        Course course = meetingScheduleRequest.getCourseId() != null ?
                courseRepository.findById(meetingScheduleRequest.getCourseId())
                        .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + meetingScheduleRequest.getCourseId())) :
                null;

        // Get the lesson (if applicable)
        Lesson lesson = meetingScheduleRequest.getLessonId() != null ?
                lessonRepository.findById(meetingScheduleRequest.getLessonId())
                        .orElseThrow(() -> new EntityNotFoundException("Lesson not found with ID: " + meetingScheduleRequest.getLessonId())) :
                null;

        // Create the MeetingSchedule entity
        MeetingSchedule meetingSchedule = new MeetingSchedule();
        meetingSchedule.setMeetingTitle(meetingScheduleRequest.getMeetingTitle());
        meetingSchedule.setDurationMinutes(meetingScheduleRequest.getDurationMinutes());
        meetingSchedule.setDateTime(meetingScheduleRequest.getDateTime());
        meetingSchedule.setMeetingUrl(meetingScheduleRequest.getMeetingUrl());
        meetingSchedule.setMeetingDescription(meetingScheduleRequest.getMeetingDescription());
        meetingSchedule.setInstructor(instructor);
        meetingSchedule.setCourse(course);
        meetingSchedule.setLesson(lesson);
        meetingSchedule.setDateTime(meetingScheduleRequest.getDateTime());
        // Save the meeting schedule
        MeetingSchedule savedMeetingSchedule = meetingScheduleRepository.save(meetingSchedule);

        // Send email to enrolled candidates
        List<String> candidateEmails = userRepository.findCandidateEmailsByCourseId(course.getId());
        for (String candidateEmail : candidateEmails) {
            // Customize the date format for email content
          //  String formattedDateTime = meetingScheduleRequest.getDateTime().format(formatter);

            // Construct the email body
            StringBuilder emailBody = new StringBuilder();
            emailBody.append("<h2>New Meeting Schedule</h2><br>");
            emailBody.append("<p>Dear Candidate,</p>");
            emailBody.append("<p>A new meeting schedule has been created:</p>");
            emailBody.append("<p><strong>Meeting Title:</strong> ").append(meetingScheduleRequest.getMeetingTitle()).append("</p>");
            emailBody.append("<p><strong>Duration:</strong> ").append(meetingScheduleRequest.getDurationMinutes()).append(" minutes</p>");
           // emailBody.append("<p><strong>Date and Time:</strong> ").append(formattedDateTime).append("</p>");
          //  emailBody.append("<p><strong>Date and Time:</strong> ").append(date).append("</p>");
            emailBody.append("<p><strong>Date and Time:</strong> ").append(meetingSchedule.getDateTime()).append("</p>");
            emailBody.append("<p><strong>Instructor:</strong> ").append(instructor.getFullname()).append("</p>");

// Include course title if available
            if (course != null) {
                emailBody.append("<p><strong>Course Title:</strong> ").append(course.getTitle()).append("</p>");
            }

// Include lesson title if available
            if (lesson != null) {
                emailBody.append("<p><strong>Lesson Title:</strong> ").append(lesson.getTitle()).append("</p>");
            }

// Include meeting URL if provided
            if (meetingScheduleRequest.getMeetingUrl() != null && !meetingScheduleRequest.getMeetingUrl().isEmpty()) {
                emailBody.append("<p><strong>Meeting URL:</strong> <a href='").append(meetingScheduleRequest.getMeetingUrl()).append("'>")
                        .append(meetingScheduleRequest.getMeetingUrl()).append("</a></p>");
            } else {
                emailBody.append("<p>The meeting URL will be sent to you soon.</p>");
            }

// Include meeting description if provided
            if (meetingScheduleRequest.getMeetingDescription() != null && !meetingScheduleRequest.getMeetingDescription().isEmpty()) {
                emailBody.append("<p><strong>Meeting Description:</strong> ").append(meetingScheduleRequest.getMeetingDescription()).append("</p>");
            }

/* Include confirmation request and buttons
            emailBody.append("<p>Please confirm your attendance:</p>");
            emailBody.append("<button style='background-color:green; color:white; padding:10px 20px; margin-right:10px;'>Yes</button>");
            emailBody.append("<button style='background-color:red; color:white; padding:10px 20px;'>No</button>");


 */
// Customize subject for each candidate
            Optional<String> candidateNameOptional = userRepository.findFirstNameByEmail(candidateEmail);
            String candidateName = candidateNameOptional.orElse("Unknown");
         //   String candidateSubject = "Invitation: " + candidateName + " - " + formattedDateTime + " (" + candidateEmail + ")";
            String candidateSubject = "Invitation: " + candidateName + " - " + meetingSchedule.getDateTime() + " (" + candidateEmail + ")";

// Send individual email
            emailService.sendEmailToCandidate(candidateEmail, instructor.getEmail(), instructor.getFullname(), candidateSubject, emailBody.toString());

        }

        // Map and return the response
        return meetingScheduleMapper.toMeetingScheduleResponse(savedMeetingSchedule);
    }
    @Override
    public List<MeetingScheduleResponse> getAllMeetingSchedules() {
        try {
            List<MeetingSchedule> meetingSchedules = meetingScheduleRepository.findAll();
            return meetingSchedules.stream()
                    .map(meetingScheduleMapper::toMeetingScheduleResponse)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            // Handle any exceptions or log errors
            ex.printStackTrace();
            throw new RuntimeException("Failed to fetch meeting schedules", ex);
        }
    }
    @Override
    public PageResponse<MeetingScheduleResponse> getInstructorMeetingSchedules(String instructorUsername, int page, int size) {
        // Find instructor by username
        User instructor = userRepository.findByEmail(instructorUsername)
                .orElseThrow(() -> new RuntimeException("Instructor not found with username: " + instructorUsername));

        // Retrieve meeting schedules for the instructor, sorted by dateTime in descending order
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateTime"));
        Page<MeetingSchedule> meetingSchedulePage = meetingScheduleRepository.findByInstructor(instructor, pageable);

        // Map meeting schedules to DTOs
        List<MeetingScheduleResponse> meetingSchedules = meetingSchedulePage.getContent()
                .stream()
                .map(meetingScheduleMapper::toMeetingScheduleResponse)
                .collect(Collectors.toList());

        // Build and return the page response
        return PageResponse.<MeetingScheduleResponse>builder()
                .content(meetingSchedules)
                .totalPages(meetingSchedulePage.getTotalPages())
                .totalElements(meetingSchedulePage.getTotalElements())
                .build();
    }
    @Override
    public List<MeetingScheduleResponse> getInstructorMeetingSchedules(String instructorUsername) {
        // Find instructor by username
        User instructor = userRepository.findByEmail(instructorUsername)
                .orElseThrow(() -> new RuntimeException("Instructor not found with username: " + instructorUsername));

        // Retrieve meeting schedules for the instructor, sorted by dateTime in descending order
        List<MeetingSchedule> meetingSchedules = meetingScheduleRepository.findByInstructor(instructor, Sort.by(Sort.Direction.DESC, "dateTime"));

        // Map meeting schedules to DTOs
        List<MeetingScheduleResponse> meetingScheduleResponses = meetingSchedules.stream()
                .map(meetingScheduleMapper::toMeetingScheduleResponse)
                .collect(Collectors.toList());

        return meetingScheduleResponses;
    }

    @Override
    public void deleteMeetingSchedule(Integer id) {
        // Retrieve the meeting schedule to get necessary details
        MeetingSchedule deletedMeetingSchedule = meetingScheduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("MeetingSchedule not found with id " + id));

        // Get enrolled candidates' emails
        List<String> candidateEmails = userRepository.findCandidateEmailsByMeetingScheduleId(deletedMeetingSchedule.getId());

        // Compose the email subject
        String emailSubject = "Meeting Cancellation: " + deletedMeetingSchedule.getMeetingTitle();

        // Send email notification to each candidate
        for (String candidateEmail : candidateEmails) {
            // Retrieve the full name of the candidate
            Optional<String> candidateFullNameOptional = userRepository.findFirstNameByEmail(candidateEmail);
            String candidateFullName = candidateFullNameOptional.orElse("Candidate");

            // Compose the email body
            StringBuilder emailBody = new StringBuilder();
            emailBody.append("<h2>Meeting Cancellation</h2><br>");
            emailBody.append("<p>Dear ").append(candidateFullName).append(",</p>");
            emailBody.append("<p>We regret to inform you that the following meeting has been cancelled:</p>");
            emailBody.append("<p>Meeting Title: ").append(deletedMeetingSchedule.getMeetingTitle()).append("</p>");
            emailBody.append("<p>Date and Time: ").append(deletedMeetingSchedule.getDateTime()).append("</p>");
            emailBody.append("<p>Instructor: ").append(deletedMeetingSchedule.getInstructor().getFullname()).append("</p>");

            // Include course title if available
            if (deletedMeetingSchedule.getCourse() != null) {
                emailBody.append("<p>Course Title: ").append(deletedMeetingSchedule.getCourse().getTitle()).append("</p>");
            }

            // Include lesson title if available
            if (deletedMeetingSchedule.getLesson() != null) {
                emailBody.append("<p>Lesson Title: ").append(deletedMeetingSchedule.getLesson().getTitle()).append("</p>");
            }

            emailBody.append("<p>We apologize for any inconvenience caused.</p>");
            emailBody.append("<p>Best regards,<br>EDUCARE</p>");

            // Send the email
            emailService.sendEmailToCandidate(
                    candidateEmail,
                    deletedMeetingSchedule.getInstructor().getEmail(),
                    "Meeting Cancellation",
                    emailSubject,
                    emailBody.toString()
            );
        }

        // Delete the meeting schedule from the database
        meetingScheduleRepository.deleteById(id);
    }

    @Override
    public MeetingScheduleResponse getMeetingScheduleById(Integer id) {
        MeetingSchedule meetingSchedule = meetingScheduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("MeetingSchedule not found with id " + id));
        return meetingScheduleMapper.toMeetingScheduleResponse(meetingSchedule);
    }

    // search all meetings by title / dateTime
  /*  @Override
    public PageResponse<MeetingScheduleResponse> searchMeetingSchedules(String searchTerm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // Perform search based on meeting title or date
        Page<MeetingSchedule> meetingSchedulePage = meetingScheduleRepository.findByMeetingTitleContainingIgnoreCase(searchTerm, pageable);

        // Map meeting schedules to DTOs
        List<MeetingScheduleResponse> meetingSchedules = meetingSchedulePage.getContent()
                .stream()
                .map(meetingScheduleMapper::toMeetingScheduleResponse)
                .collect(Collectors.toList());

        // Build and return the page response
        return PageResponse.<MeetingScheduleResponse>builder()
                .content(meetingSchedules)
                .totalPages(meetingSchedulePage.getTotalPages())
                .totalElements(meetingSchedulePage.getTotalElements())
                .build();
    }

   */
    @Override
    public PageResponse<MeetingScheduleResponse> searchMeetingSchedulesByAuthenticatedUser(String searchTerm, int page, int size) {
        // Get authenticated user (instructor)
        String instructorUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User instructor = userRepository.findByEmail(instructorUsername)
                .orElseThrow(() -> new RuntimeException("Instructor not found with username: " + instructorUsername));

        // Parse the searchTerm into LocalDateTime if it represents a date and time
        LocalDateTime parsedDateTime = null;
        try {
            parsedDateTime = LocalDateTime.parse(searchTerm, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            // Ignore parsing exception, searchTerm is not a valid LocalDateTime format
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<MeetingSchedule> meetingSchedulePage;
        if (parsedDateTime != null) {
            // searchTerm represents a valid LocalDateTime
            meetingSchedulePage = meetingScheduleRepository.findByInstructorAndDateTime(instructor, parsedDateTime, pageable);
        } else {
            // searchTerm does not represent a valid LocalDateTime, search by meeting title
            meetingSchedulePage = meetingScheduleRepository.findByInstructorAndMeetingTitleContainingIgnoreCase(instructor, searchTerm, pageable);
        }

        List<MeetingScheduleResponse> meetingSchedules = meetingSchedulePage.getContent()
                .stream()
                .map(meetingScheduleMapper::toMeetingScheduleResponse)
                .collect(Collectors.toList());

        return PageResponse.<MeetingScheduleResponse>builder()
                .content(meetingSchedules)
                .totalPages(meetingSchedulePage.getTotalPages())
                .totalElements(meetingSchedulePage.getTotalElements())
                .build();
    }

    @Override
    public MeetingScheduleResponse updateMeetingSchedule(Integer id, MeetingScheduleRequest meetingScheduleRequest) {
        MeetingSchedule existingMeetingSchedule = meetingScheduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("MeetingSchedule not found with id " + id));

        // Store the original meeting schedule details
        MeetingSchedule originalMeetingSchedule = existingMeetingSchedule.deepCopy();

        // Update the meeting schedule with the new details
        meetingScheduleMapper.updateEntityFromRequest(meetingScheduleRequest, existingMeetingSchedule);
        MeetingSchedule updatedMeetingSchedule = meetingScheduleRepository.save(existingMeetingSchedule);

        // Check if the date, duration, or URL has been updated
        if (isDateDurationOrUrlUpdated(updatedMeetingSchedule, originalMeetingSchedule)) {
            // Log the email notification action
            System.out.println("Sending email notifications for meeting update: " + updatedMeetingSchedule.getId());

            // Send email notification to enrolled candidates
            sendEmailToEnrolledCandidates(updatedMeetingSchedule, originalMeetingSchedule);
        }

        return meetingScheduleMapper.toMeetingScheduleResponse(updatedMeetingSchedule);
    }

    // Method to check if the date, duration, or URL has been updated
    private boolean isDateDurationOrUrlUpdated(MeetingSchedule updatedMeetingSchedule, MeetingSchedule originalMeetingSchedule) {
        return !Objects.equals(updatedMeetingSchedule.getDateTime(), originalMeetingSchedule.getDateTime()) ||
                !Objects.equals(updatedMeetingSchedule.getDurationMinutes(), originalMeetingSchedule.getDurationMinutes()) ||
                !Objects.equals(updatedMeetingSchedule.getMeetingUrl(), originalMeetingSchedule.getMeetingUrl());
    }

    // Method to generate email content with highlighted changes
    private String generateEmailContent(MeetingSchedule updatedMeetingSchedule, MeetingSchedule originalMeetingSchedule) {
        StringBuilder emailContent = new StringBuilder();

        // Always include the meeting URL, aligned to the right with hover effect
        emailContent.append("<p style='text-align:right;'>Meeting link<br>")
                .append("<a href='").append(updatedMeetingSchedule.getMeetingUrl()).append("' style='color:#824bc7; text-decoration:none; font-weight:bold;' ")
                .append("onmouseover=\"this.style.color='#333'; this.style.textDecoration='underline';\" ")
                .append("onmouseout=\"this.style.color='#824bc7'; this.style.textDecoration='none';\">")
                .append(updatedMeetingSchedule.getMeetingUrl())
                .append("</a></p><br>");

        // Add the heading for the email with the meeting title
        emailContent.append("<h2>Meeting Title: ").append(updatedMeetingSchedule.getMeetingTitle()).append("</h2><br>");

        // Check if the meeting title has been updated and highlight it
        if (!Objects.equals(updatedMeetingSchedule.getMeetingTitle(), originalMeetingSchedule.getMeetingTitle())) {
            emailContent.append("<p>Meeting Title has been updated from: <span style='color:red; text-decoration:underline;'>")
                    .append(originalMeetingSchedule.getMeetingTitle())
                    .append("</span> to: ")
                    .append(updatedMeetingSchedule.getMeetingTitle())
                    .append("</p><br>");
        }

        // Check and highlight updated fields
        if (!Objects.equals(updatedMeetingSchedule.getDateTime(), originalMeetingSchedule.getDateTime())) {
            emailContent.append("<p>Date <span style='background-color:green; color:white; padding:5px; border-radius:5px;'>UPDATED</span><br>")
                 //   .append(updatedMeetingSchedule.getDateTime().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy ⋅ h:mma", Locale.ENGLISH)))
                    .append(updatedMeetingSchedule.getDateTime())
                    .append(" (Central European Time - Tunis)<br>")
                    .append("<del><span style='color:red;'>")
                   // .append(originalMeetingSchedule.getDateTime().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy ⋅ h:mma", Locale.ENGLISH)))
                    .append(originalMeetingSchedule.getDateTime())
                    .append(" (Central European Time - Tunis)</span></del></p><br>");
        }

        if (!Objects.equals(updatedMeetingSchedule.getDurationMinutes(), originalMeetingSchedule.getDurationMinutes())) {
            emailContent.append("<p>Duration <span style='background-color:green; color:white; padding:5px; border-radius:5px;'>UPDATED</span><br>")
                    .append(updatedMeetingSchedule.getDurationMinutes())
                    .append(" minutes<br>")
                    .append("<del><span style='color:red;'>")
                    .append(originalMeetingSchedule.getDurationMinutes())
                    .append(" minutes</span></del></p><br>");
        }

        if (!Objects.equals(updatedMeetingSchedule.getMeetingUrl(), originalMeetingSchedule.getMeetingUrl())) {
            emailContent.append("<p>Meeting URL <span style='background-color:green; color:white; padding:5px; border-radius:5px;'>UPDATED</span><br>")
                    .append(updatedMeetingSchedule.getMeetingUrl())
                    .append("<br>")
                    .append("<del><span style='color:red;'>")
                    .append(originalMeetingSchedule.getMeetingUrl())
                    .append("</span></del></p><br>");
        }

        return emailContent.toString();
    }

    // Updated sendEmailToEnrolledCandidates method
    private void sendEmailToEnrolledCandidates(MeetingSchedule updatedMeetingSchedule, MeetingSchedule originalMeetingSchedule) {
        // Get enrolled candidates' emails
        List<String> candidateEmails = userRepository.findCandidateEmailsByMeetingScheduleId(updatedMeetingSchedule.getId());

        // Ensure unique email addresses
        Set<String> uniqueCandidateEmails = new HashSet<>(candidateEmails);

        // Generate email content with the updated meeting details
        String emailContent = generateEmailContent(updatedMeetingSchedule, originalMeetingSchedule);

        // Send updated email to each enrolled candidate separately
        for (String candidateEmail : uniqueCandidateEmails) {
            // Get the candidate's name (or default to "Candidate")
            Optional<String> candidateNameOptional = userRepository.findFirstNameByEmail(candidateEmail);
            String candidateName = candidateNameOptional.orElse("Candidate");

            // Format the subject line
            //String formattedDateTime = updatedMeetingSchedule.getDateTime().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy ⋅ h:mma", Locale.ENGLISH));
            String formattedDateTime = updatedMeetingSchedule.getDateTime() ;
            String subject = String.format("Updated Invitation: %s - %s (%s)", updatedMeetingSchedule.getMeetingTitle(), formattedDateTime, candidateEmail);

            // Send the updated email content to the candidate using the EmailService
            emailService.sendEmailToCandidate(
                    candidateEmail,
                    updatedMeetingSchedule.getInstructor().getEmail(),
                    updatedMeetingSchedule.getInstructor().getFullname(),
                    subject,
                    emailContent
            );
        }
    }

    /** candidate **/
    @Override
    public PageResponse<MeetingScheduleResponse> getMeetingsForCandidate(String candidateEmail, int page, int size) {
        // Get scheduled meetings for the candidate's enrolled courses sorted by datetime
        Pageable pageable = PageRequest.of(page, size);
        Page<MeetingSchedule> meetingSchedulePage = meetingScheduleRepository.findByCourse_EnrolledCandidates_EmailOrderByDateTimeDesc(candidateEmail, pageable);

        // Map meeting schedules to DTOs
        List<MeetingScheduleResponse> meetingResponses = meetingSchedulePage.getContent()
                .stream()
                .map(meetingScheduleMapper::toMeetingScheduleResponse)
                .collect(Collectors.toList());

        // Build and return the page response
        return PageResponse.<MeetingScheduleResponse>builder()
                .content(meetingResponses)
                .totalPages(meetingSchedulePage.getTotalPages())
                .totalElements(meetingSchedulePage.getTotalElements())
                .build();
    }

}
