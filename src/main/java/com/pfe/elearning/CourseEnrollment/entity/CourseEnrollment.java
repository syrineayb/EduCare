package com.pfe.elearning.CourseEnrollment.entity;

import com.pfe.elearning.common.BaseEntity;
import com.pfe.elearning.course.entity.Course;
import com.pfe.elearning.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CourseEnrollment extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "publisher_id")
    private User publisher;

    @ManyToOne
    @JoinColumn(name = "candidate_id")
    private User candidate;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    private String courseName;
}
