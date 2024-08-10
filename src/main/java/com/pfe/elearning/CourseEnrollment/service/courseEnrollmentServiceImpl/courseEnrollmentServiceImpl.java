package com.pfe.elearning.CourseEnrollment.service.courseEnrollmentServiceImpl;

import com.pfe.elearning.CourseEnrollment.entity.CourseEnrollment;
import com.pfe.elearning.CourseEnrollment.repository.CourseEnrollmentRepository;
import com.pfe.elearning.CourseEnrollment.service.CourseEnrollmentService;
import com.pfe.elearning.user.entity.User;
import com.pfe.elearning.user.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class courseEnrollmentServiceImpl implements CourseEnrollmentService {
    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final UserService userService;

    @Override
    public List<CourseEnrollment> getAllCourseEnrollments(String instructorUsername) {
        User instructor = userService.getUserByEmail(instructorUsername);

        // If the instructor is not found, return 0 as there are no courses
        if (instructor == null) {
            return null;
        }
        return courseEnrollmentRepository.findByPublisher(instructor);
    }


}