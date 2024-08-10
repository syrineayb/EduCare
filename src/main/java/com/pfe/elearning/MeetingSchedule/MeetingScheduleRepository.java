package com.pfe.elearning.MeetingSchedule;

import com.pfe.elearning.course.entity.Course;
import com.pfe.elearning.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public interface MeetingScheduleRepository extends JpaRepository<MeetingSchedule,Integer> {
    Page<MeetingSchedule> findByInstructor(User instructor, Pageable pageable);

    List<MeetingSchedule> findByInstructor(User instructor, Sort sort);

    Page<MeetingSchedule> findByMeetingTitleContainingIgnoreCase(String searchTerm, Pageable pageable);

    Page<MeetingSchedule> findByInstructorAndDateTime(User instructor, LocalDateTime dateTime, Pageable pageable);

    Page<MeetingSchedule> findByInstructorAndMeetingTitleContainingIgnoreCase(User instructor, String searchTerm, Pageable pageable);

    Page<MeetingSchedule> findByCourse_EnrolledCandidates_EmailOrderByDateTimeDesc(String candidateEmail, Pageable pageable);
}