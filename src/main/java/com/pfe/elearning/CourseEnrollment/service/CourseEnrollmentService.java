package com.pfe.elearning.CourseEnrollment.service;

import com.pfe.elearning.CourseEnrollment.entity.CourseEnrollment;

import java.util.List;

public interface CourseEnrollmentService {

    List<CourseEnrollment> getAllCourseEnrollments(String instructorUsername);


}