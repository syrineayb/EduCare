package com.pfe.elearning.UserResourceCompletion.UserlessonCompletion;

import com.pfe.elearning.lesson.entity.Lesson;
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
public class UserLessonCompletion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "Lesson_id", nullable = false)
    private Lesson lesson;

    private boolean completed;
}
