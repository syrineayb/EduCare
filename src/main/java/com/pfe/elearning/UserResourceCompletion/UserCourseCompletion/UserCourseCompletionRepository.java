package com.pfe.elearning.UserResourceCompletion.UserCourseCompletion;

import com.pfe.elearning.course.entity.Course;
import com.pfe.elearning.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCourseCompletionRepository extends JpaRepository<UserCourseCompletion, Integer> {
    Optional<UserCourseCompletion> findByUserAndCourse(User user, Course course);

}
