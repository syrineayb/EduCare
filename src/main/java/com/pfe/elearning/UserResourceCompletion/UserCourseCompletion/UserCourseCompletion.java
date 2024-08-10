package com.pfe.elearning.UserResourceCompletion.UserCourseCompletion;

import com.pfe.elearning.course.entity.Course;
import com.pfe.elearning.resource.entity.Resourcee;
import com.pfe.elearning.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserCourseCompletion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    private boolean completed;
}
