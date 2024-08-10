package com.pfe.elearning.user.repository;
import com.pfe.elearning.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByEmail(String email);

    Page<User> findAllByActive(boolean active, Pageable pageable);
    Optional<User> findById(Integer userId);
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRole(@Param("roleName") String roleName);
    Page<User> findByRolesName(String roleName, Pageable pageable);
    List<User> findByFirstnameContainingIgnoreCase(String name);

    List<User> findByEmailContaining(String email);
    List<User> findByLastnameContainingIgnoreCase(String name);

    List<User> findByLastLoginBeforeAndActiveIsTrue(LocalDateTime lastLoginBefore);

    boolean existsByEmail(String email);

    @Query("SELECT COUNT(u) FROM User u")
    int countTotalUsers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.active = ?1")
    int countActiveUsers(boolean active);


    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = 'ROLE_CANDIDATE'")
    int countCandidateUsers();
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = 'ROLE_INSTRUCTOR'")
    int countInstructorUsers();
    @Query("SELECT u FROM User u JOIN FETCH u.roles r LEFT JOIN u.enrolledCourses ec " +
            "WHERE r.name = 'ROLE_INSTRUCTOR' " +
            "GROUP BY u " +
            "ORDER BY COUNT(ec) DESC")
    Page<User> findInstructorsOrderByEnrolledCandidates(Pageable pageable);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.enrolledCourses ec WHERE u IN :users")
    List<User> findInstructorsWithEnrolledCourses(@Param("users") List<User> users);
    @Query("SELECT DISTINCT u FROM User u JOIN u.enrolledCourses c WHERE c.publisher.id = :publisherId")
    List<User> findAllCandidatesOfPublisherCourses(@Param("publisherId") Integer publisherId);
    @Query("SELECT DISTINCT u.email FROM User u JOIN u.enrolledCourses c WHERE c.id = :courseId")
    List<String> findCandidateEmailsByCourseId(@Param("courseId") Integer courseId);

    @Query("SELECT u.firstname FROM User u WHERE u.email = :email")
    Optional<String> findFirstNameByEmail(@Param("email") String email);

    @Query("SELECT DISTINCT u.email FROM User u JOIN u.enrolledCourses c JOIN c.meetingSchedules m WHERE m.id = :meetingScheduleId")
    List<String> findCandidateEmailsByMeetingScheduleId(@Param("meetingScheduleId") Integer meetingScheduleId);
}

  /*  @Query("SELECT u FROM User u JOIN u.roles r WHERE lower(r.name) LIKE lower(concat('%', :roleName, '%'))")
    List<UserResponse> findByRoleContaining(@Param("roleName") String roleName);
*/

