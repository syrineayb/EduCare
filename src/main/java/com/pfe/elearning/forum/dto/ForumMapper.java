// ForumMapper.java
package com.pfe.elearning.forum.dto;

import com.pfe.elearning.forum.dto.ForumRequest;
import com.pfe.elearning.forum.dto.ForumResponse;
import com.pfe.elearning.forum.entity.Forum;
import org.springframework.stereotype.Service;

@Service
public class ForumMapper {

    public ForumResponse toForumResponse(Forum forum) {
        return ForumResponse.builder()
                .id(forum.getId())
                .title(forum.getTitle())
                .lessonDescription(forum.getLesson().getDescription())
                .courseTitle(forum.getLesson().getCourse().getTitle())
                .courseId(forum.getLesson().getCourse().getId())
                .build();
    }

    public Forum toForum(ForumRequest request) {
        Forum forum = new Forum();
        forum.setTitle(request.getTitle());
        return forum;
    }
}

