package com.pfe.elearning.course.repository;

import com.pfe.elearning.course.entity.Course;
import com.pfe.elearning.question.entity.Question;
import com.pfe.elearning.topic.entity.Topic;
import com.pfe.elearning.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Integer> {


    List<Course> findByTopic(Topic topic);
    Page<Course> findByTopicId(Integer topicId, Pageable pageable);

    List<Course> findByTitleContaining(String keyword);

    List<Course> findByPublisher(User instructor);
    Optional<Course> findById(Integer courseId);
    Page<Course> findByPublisher(User publisher, Pageable pageable);

    Page<Course> findByPublisherOrderByCreatedAtDesc(User instructor, PageRequest of);

    Page<Course> findAllByOrderByCreatedAtDesc(PageRequest of);

    // List<Course> findByTitleContainingAndPublisher(String keyword, User publisher);
    // List<Course> findByEnrolledCandidatesId(Integer candidateId);
    @Query("SELECT c FROM Course c JOIN c.enrolledCandidates ec WHERE ec.email = :candidateEmail")
    List<Course> findEnrolledCoursesByCandidateEmail(String candidateEmail);

    List<Course> findByPublisherUsername(String publisherUsername);
}
