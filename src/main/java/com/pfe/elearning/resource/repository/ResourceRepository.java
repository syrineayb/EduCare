package com.pfe.elearning.resource.repository;

import com.pfe.elearning.lesson.entity.Lesson;
import com.pfe.elearning.resource.entity.Resourcee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResourceRepository extends JpaRepository<Resourcee,Integer> {
List <Resourcee>findByLesson(Lesson lesson);
Optional<Resourcee>findById(Integer resourceeId);

    int countByLessonIdAndVideoFileIsNotNull(Integer lessonId);
    int countByLessonIdAndPdfFileIsNotNull(Integer lessonId);
    int countByLessonIdAndImageFileIsNotNull(Integer lessonId);

    long countByLessonId(Integer lessonId);

    Page<Resourcee> findByLessonId(Integer lessonId, Pageable pageable);
}
