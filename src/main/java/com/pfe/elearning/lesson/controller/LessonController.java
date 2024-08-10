package com.pfe.elearning.lesson.controller;

import com.pfe.elearning.UserResourceCompletion.UserlessonCompletion.UserCourseProgressResponse;
import com.pfe.elearning.common.PageResponse;
import com.pfe.elearning.lesson.dto.LessonRequest;
import com.pfe.elearning.lesson.dto.LessonResponse;
import com.pfe.elearning.lesson.service.LessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonController {
    private final LessonService lessonService;


    @GetMapping("/course/{courseId}/progress")
    public ResponseEntity<UserCourseProgressResponse> getUserCourseProgress(@PathVariable Integer courseId,
                                                                            @AuthenticationPrincipal UserDetails userDetails) {
        String candidateUsername = userDetails.getUsername();
        int progress = lessonService.getUserCourseProgress(candidateUsername, courseId);
        return ResponseEntity.ok(new UserCourseProgressResponse(progress));
    }



    @PostMapping("/{courseId}")
    public ResponseEntity<LessonResponse> createLesson(@PathVariable Integer courseId ,
                                                       @RequestBody @Valid LessonRequest lessonRequest) {
        LessonResponse lessonResponse = lessonService.createLesson(courseId, lessonRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonResponse);
    }
    @PutMapping("/{lessonId}")
    public ResponseEntity<LessonResponse> updateLesson(
            @PathVariable Integer lessonId,
            @Valid @RequestBody LessonRequest lessonRequest) {
        LessonResponse lessonResponse = lessonService.updateLesson(lessonId, lessonRequest);
        return ResponseEntity.ok(lessonResponse);
    }
    @DeleteMapping("/{lessonId}")
    public ResponseEntity<Void> deleteLesson(@PathVariable Integer lessonId) {
        lessonService.deleteLesson(lessonId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{lessonId}")
    public ResponseEntity<LessonResponse> getLessonById(@PathVariable Integer lessonId) {
        LessonResponse lesson = lessonService.getLessonById(lessonId);
        return ResponseEntity.ok(lesson);
    }
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<LessonResponse>> getAllLessonsByCourse(
            @PathVariable Integer courseId)
    {
        List<LessonResponse> lessons = lessonService.getAllLessonsByCourse(courseId);
        return ResponseEntity.ok(lessons);
    }

    // count lessons by course id
    @GetMapping("/lessons/count/{courseId}")
    public long countLessonsByCourseId(@PathVariable Integer courseId) {
        return lessonService.countLessonsByCourseId(courseId);
    }

    @GetMapping("/course/{courseId}/paginated")
    public ResponseEntity<PageResponse<LessonResponse>> getAllLessonsByCourse(
            @PathVariable Integer courseId,
            @RequestParam(name = "page", defaultValue = "1", required = false) int page,
            @RequestParam(name = "size", defaultValue = "4", required = false) int size) {
        PageResponse<LessonResponse> lessonPage = lessonService.getAllLessonsByCourse(courseId, page, size);
        return ResponseEntity.ok(lessonPage);
    }
    @GetMapping("/search")
    public ResponseEntity<List<LessonResponse>> findLessonByTitleContaining(@RequestParam String keyword) {
        List<LessonResponse> lessons = lessonService.findLessonByTitleContaining(keyword);
        return ResponseEntity.ok(lessons);
    }

    @GetMapping
    public ResponseEntity<List<LessonResponse>> getAllLessons() {
        List<LessonResponse> lessons = lessonService.getAllLessons();
        return ResponseEntity.ok(lessons);
    }
}
