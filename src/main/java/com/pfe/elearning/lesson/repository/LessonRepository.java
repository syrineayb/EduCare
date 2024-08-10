package com.pfe.elearning.lesson.repository;

import com.pfe.elearning.course.entity.Course;
import com.pfe.elearning.lesson.entity.Lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface LessonRepository extends JpaRepository<Lesson,Integer> {
    List<Lesson> findByTitleContaining(String keyword);
    @Query("SELECT l FROM Lesson l WHERE l.course = :course")
    Page<Lesson> findAllByCourse(@Param("course") Course course, Pageable pageable);

    List<Lesson> findByCourseId(int courseId);

    long countByCourseId(Integer courseId);
    @Query("SELECT l FROM Lesson l WHERE l.course = :course")
    List<Lesson> findAllByCourse(@Param("course") Course course);
}
