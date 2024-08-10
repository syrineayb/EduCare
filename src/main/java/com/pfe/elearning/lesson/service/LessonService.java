package com.pfe.elearning.lesson.service;

import com.pfe.elearning.common.PageResponse;
import com.pfe.elearning.lesson.dto.LessonRequest;
import com.pfe.elearning.lesson.dto.LessonResponse;

import java.util.List;

public interface LessonService {
   long countLessonsByCourseId(Integer courseId);
 long countCompletedLessonsByUserAndCourseId(String candidateUsername, Integer courseId);
   void updateCourseCompletionStatus(String candidateUsername, Integer courseId);  // New method to update course completion status


    int getUserCourseProgress(String candidateUsername, Integer courseId) ;

    //LessonResponse createLesson(LessonRequest lessonRequest,String publisherUsername);
   // PageResponse<LessonResponse> findAll(int page, int size);
    // LessonResponse updateLesson(Long lessonId, LessonRequest lessonRequest, String publisherUsername);
    // void deleteLesson(Long lessonId, String publisherUsername);
    // PageResponse<LessonResponse> getAllLessons(int page, int size);
   LessonResponse createLesson(Integer courseId,LessonRequest lessonRequest);
    LessonResponse updateLesson(Integer lessonId, LessonRequest lessonRequest);
    void deleteLesson(Integer lessonId);
    LessonResponse getLessonById(Integer lessonId);

    PageResponse<LessonResponse> getAllLessonsByCourse(Integer courseId,int page, int size);
    List<LessonResponse> getAllLessonsByCourse(Integer courseId);
    List<LessonResponse> findLessonByTitleContaining(String keyword);
    List<LessonResponse> getAllLessons();


}
