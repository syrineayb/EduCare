package com.pfe.elearning.CourseEnrollment.controller;

import com.pfe.elearning.CourseEnrollment.dto.CourseEnrollmentMapper;
import com.pfe.elearning.CourseEnrollment.dto.CourseEnrollmentResponse;
import com.pfe.elearning.CourseEnrollment.entity.CourseEnrollment;
import com.pfe.elearning.CourseEnrollment.service.CourseEnrollmentService;
import com.pfe.elearning.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/course-enrollments")
@RequiredArgsConstructor
public class CourseEnrollmentController {
    private final CourseEnrollmentService courseEnrollmentService;
    private final CourseEnrollmentMapper courseEnrollmentMapper;



    @GetMapping
    public ResponseEntity<List<CourseEnrollmentResponse>> getAllCourseEnrollments(
            @AuthenticationPrincipal UserDetails userDetails

    ) {
        String instructorUsername = userDetails.getUsername();

        List<CourseEnrollment> enrollments = courseEnrollmentService.getAllCourseEnrollments(instructorUsername);
        List<CourseEnrollmentResponse> response = enrollments.stream()
                .map(courseEnrollmentMapper::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

}
