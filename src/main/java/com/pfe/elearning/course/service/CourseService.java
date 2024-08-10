package com.pfe.elearning.course.service;

import com.pfe.elearning.common.PageResponse;
import com.pfe.elearning.course.dto.CandidateCourseResponse;
import com.pfe.elearning.course.dto.CourseRequest;
import com.pfe.elearning.course.dto.CourseResponse;
import com.pfe.elearning.user.entity.User;

import java.util.List;

public interface CourseService {
    List<CourseResponse> getCoursesByPublisher(String publisherUsername);

    CourseResponse createCourse(CourseRequest courseRequest, String publisherUsername, String imageUrl);
    CourseResponse getCourseById(Integer courseId);
    CourseResponse updateCourse(Integer courseId, CourseRequest courseRequest,String imageUrl);
    void deleteCourse(Integer courseId);
    PageResponse<CourseResponse> findAll(int page, int size);
    List<CourseResponse> getAllCourses();
    boolean existsById(Integer id);
    List<CourseResponse> findCourseByTitleContaining(String keyword);
    List<CourseResponse> getCoursesByTopic(Integer topicId);

    PageResponse<CourseResponse> getCoursesByTopic(Integer topicId, int page, int size);

    List<CourseResponse> getAllCoursesByInstructor(String instructorUsername);
    PageResponse<CourseResponse> getAllCoursesByInstructor(String instructorUsername, int page, int size);

    List<CourseResponse> findInstructorCourseByTitleContaining(String keyword);
    CandidateCourseResponse enrollInCourse(Integer userId, Integer courseId);
    void unenrollFromCourse(Integer userId, Integer courseId);


    List<CourseResponse> getCoursesForCandidate(Integer candidateId);
    boolean isUserEnrolledInCourse(Integer userId, Integer courseId);
    long countUsersEnrolledInCourse(Integer courseId);

    PageResponse<CourseResponse> findCoursesInBatch(int page, int size);

    // Method to get recently added courses by the instructor
    PageResponse<CourseResponse> getRecentlyAddedCoursesByInstructor(String instructorUsername, int page, int size);

    PageResponse<CourseResponse> getLatestCoursesForCandidates(int page, int size);
    long countCoursesByInstructor(String instructorUsername);

    long countAllEnrolledCandidatesForAllCoursesByInstructor(String instructorUsername);

    long countEnrolledCandidatesByInstructorForCourse(String instructorUsername, Integer courseId);

    String getCourseTitleById(Integer courseId);


    // CourseResponse enrollUserInCourse(Integer userId, Integer courseId);

    // For example:
    // void assignCandidateToCourse(Integer candidateId, Integer courseId);
    // PageResponse<CourseResponse> getCoursesByPublisher(String publisherUsername, int page, int size);
    // PageResponse<CourseResponse> getCoursesByDuration(String duration, int page, int size);
    // PageResponse<CourseResponse> getCoursesByPrice(boolean isFree, int page, int size);
    // List<CourseResponse> getCoursesEnrolledByUser(Integer userId);
    // PageResponse<CourseResponse> searchCoursesByDescription(String keyword, int page, int size);
    // String getCourseEnrollmentStatus(Integer userId, Integer courseId);
}



