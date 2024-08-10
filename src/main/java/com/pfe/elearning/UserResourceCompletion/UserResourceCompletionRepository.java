package com.pfe.elearning.UserResourceCompletion;


import com.pfe.elearning.lesson.entity.Lesson;
import com.pfe.elearning.resource.entity.Resourcee;
import com.pfe.elearning.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserResourceCompletionRepository extends JpaRepository<UserResourceCompletion, Integer> {
    Optional<UserResourceCompletion> findByUserAndResource(User user, Resourcee resource);
    long countByUserAndResource_Lesson_IdAndCompleted(User user, Integer lessonId, boolean completed);

}