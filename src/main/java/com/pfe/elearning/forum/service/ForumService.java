package com.pfe.elearning.forum.service;

import com.pfe.elearning.common.PageResponse;
import com.pfe.elearning.forum.dto.ForumRequest;
import com.pfe.elearning.forum.dto.ForumResponse;
import com.pfe.elearning.forum.entity.Forum;

import java.util.List;

public interface ForumService {
    PageResponse<ForumResponse> getAllForums(int page, int size);
  //  ForumResponse getForumById(Integer id);
    Forum createForumForLesson(Forum forum); // For internal creation when a lesson is created

   ForumResponse createForum(Integer lessonId, ForumRequest forumRequest); // Changed courseId to lessonId
    ForumResponse getForumByLessonId(Integer lessonId); // Changed to return single ForumResponse


}
