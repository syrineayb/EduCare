package com.pfe.elearning.CourseEnrollment.dto;

import com.pfe.elearning.CourseEnrollment.entity.CourseEnrollment;
import com.pfe.elearning.QuizResult.entity.QuizResult;
import com.pfe.elearning.QuizResult.repository.QuizResultRepository;
import com.pfe.elearning.QuizResult.service.QuizResultService;
import com.pfe.elearning.lesson.service.LessonService;
import com.pfe.elearning.profile.entity.Profile;
import com.pfe.elearning.quiz.service.QuizService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class CourseEnrollmentMapper {
    private final LessonService lessonService;
    private final QuizService quizService;
    private final QuizResultRepository quizResultRepository;
    private final QuizResultService quizResultService;
    public List<CourseEnrollmentResponse> mapToResponse(List<CourseEnrollment> courseEnrollments) {
        return courseEnrollments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CourseEnrollmentResponse mapToResponse(CourseEnrollment enrollment) {
        CourseEnrollmentResponse response = new CourseEnrollmentResponse();
        response.setId(Long.valueOf(enrollment.getId()));
        response.setPublisherId(Long.valueOf(enrollment.getPublisher().getId()));
        response.setCandidateId(Long.valueOf(enrollment.getCandidate().getId()));

        response.setCourseId(Long.valueOf(enrollment.getCourse().getId()));
        response.setCourseName(enrollment.getCourseName());

        // Handle null check for profile image
        if (enrollment.getCandidate() != null && enrollment.getCandidate().getProfile() != null) {
            Profile profile = enrollment.getCandidate().getProfile();
            if (profile.getProfileImage() != null || profile.getFirstName() != null || profile.getLastName() != null) {
                response.setCandidateImage(profile.getProfileImage());
                response.setCandidateName(profile.getFirstName());
                response.setCandidateLastname(profile.getLastName());
            }
        }
        double progress = lessonService.getUserCourseProgress(enrollment.getCandidate().getEmail(), enrollment.getCourse().getId());
        response.setProgress((int) progress);

        // Calculate quiz progress
        double quizProgress = quizResultService.getUserQuizProgress(Long.valueOf(enrollment.getCourse().getId()), enrollment.getCandidate().getEmail());
        response.setQuizProgress((int) quizProgress);

        return response;
    }
}