package com.pfe.elearning.CourseEnrollment.repository;

import com.pfe.elearning.CourseEnrollment.entity.CourseEnrollment;
import com.pfe.elearning.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Integer> {
    List<CourseEnrollment> findByPublisher(User instructor);

    void deleteByCourseId(Integer courseId);
}
