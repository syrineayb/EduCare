package com.pfe.elearning.forum.service.impl;

import com.pfe.elearning.common.PageResponse;
import com.pfe.elearning.forum.dto.ForumMapper;
import com.pfe.elearning.forum.dto.ForumRequest;
import com.pfe.elearning.forum.dto.ForumResponse;
import com.pfe.elearning.forum.entity.Forum;
import com.pfe.elearning.forum.repository.ForumRepository;
import com.pfe.elearning.forum.service.ForumService;
import com.pfe.elearning.lesson.entity.Lesson;
import com.pfe.elearning.lesson.repository.LessonRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ForumServiceImpl implements ForumService {
    private final ForumRepository forumRepository;
    private final ForumMapper forumMapper;
    private final LessonRepository lessonRepository;

    @Override
    public PageResponse<ForumResponse> getAllForums(int page, int size) {
        Page<Forum> forumPage = forumRepository.findAll(PageRequest.of(page, size));
        List<ForumResponse> forumResponses = forumPage.getContent().stream()
                .map(forumMapper::toForumResponse)
                .collect(Collectors.toList());

        return PageResponse.<ForumResponse>builder()
                .content(forumResponses)
                .totalPages(forumPage.getTotalPages())
                .totalElements(forumPage.getTotalElements())
                .build();
    }

  /*  @Override
    public ForumResponse getForumById(Integer id) {
        Forum forum = forumRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Forum not found with id: " + id));
        return forumMapper.toForumResponse(forum);
    }
*/
    @Override
    public ForumResponse createForum(Integer lessonId, ForumRequest forumRequest) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found with id: " + lessonId));

        Forum forum = forumMapper.toForum(forumRequest);
        forum.setLesson(lesson);

        Forum savedForum = forumRepository.save(forum);
        return forumMapper.toForumResponse(savedForum);
    }

    @Override
    public ForumResponse getForumByLessonId(Integer id) {
        Forum forum = forumRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Forum not found with id: " + id));
        return forumMapper.toForumResponse(forum);
    }
    @Override
    public Forum createForumForLesson(Forum forum) {
        return forumRepository.save(forum);
    }


}
