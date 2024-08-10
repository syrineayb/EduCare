package com.pfe.elearning.forum.repository;

import com.pfe.elearning.forum.entity.Forum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ForumRepository extends JpaRepository<Forum,Integer> {

    List<Forum> findByLessonId(Integer lessonId);
}
