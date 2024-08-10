package com.pfe.elearning.forum.controller;

import com.pfe.elearning.common.PageResponse;
import com.pfe.elearning.forum.dto.ForumResponse;
import com.pfe.elearning.forum.service.ForumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/forums")
@RequiredArgsConstructor
public class ForumController {
    private final ForumService forumService;

    @GetMapping
    public ResponseEntity<PageResponse<ForumResponse>> getAllForums(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<ForumResponse> forumPage = forumService.getAllForums(page, size);
        return ResponseEntity.ok(forumPage);
    }


    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<ForumResponse> getForumByLessonId(@PathVariable Integer lessonId) {
        ForumResponse forumResponse = forumService.getForumByLessonId(lessonId);
        return ResponseEntity.ok(forumResponse);
    }

/*
    @GetMapping("/{id}")
    public ResponseEntity<ForumResponse> getForumById(@PathVariable Integer id) {
        ForumResponse forumResponse = forumService.getForumById(id);
        return ResponseEntity.ok(forumResponse);
    }

 */

/*    @PostMapping("/{lessonId}")
    public ResponseEntity<ForumResponse> createForum(@PathVariable Integer lessonId,
                                                     @RequestBody ForumRequest forumRequest) {
        ForumResponse createdForum = forumService.createForum(lessonId, forumRequest);
        return new ResponseEntity<>(createdForum, HttpStatus.CREATED);
    }

 */


}
