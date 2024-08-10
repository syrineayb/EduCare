package com.pfe.elearning.forum.Message;


import com.pfe.elearning.common.PageResponse;
import com.pfe.elearning.forum.Message.MessageRequest;
import com.pfe.elearning.forum.Message.MessageResponse;

import java.util.List;

public interface MessageService {
    PageResponse<MessageResponse> getAllDiscussionsByForumId(int forumId, int page, int size);
    PageResponse<MessageResponse> getAllDiscussionsByLessonId(int forumId, int page, int size);
    PageResponse<MessageResponse> getInstructorLessonDiscussions(String instructorUsername, int page, int size);

    List<MessageResponse> getAllDiscussionsByLessonId(int lessonId);
    MessageResponse updateDiscussion(Integer id, MessageRequest discussionRequest, String imageUrl, String videoUrl, String pdfUrl, String recordUrl, Integer authorId);
    void deleteDiscussion(Integer id);
   // DiscussionResponse createDiscussion(DiscussionRequest discussionRequest, Integer forumId, Integer authorId) ;
    MessageResponse createDiscussion(MessageRequest discussionRequest, String imageUrl,
                                     String videoUrl, String pdfUrl, String recordUrl,
                                     Integer forumId, Integer authorId) ;
    //<DiscussionResponse> searchDiscussionsBySubjectContaining(String keyword, int page, int size);
    PageResponse<MessageResponse> searchDiscussionsBySubjectContaining(int forumId,String keyword, int page, int size);
    List<MessageResponse> searchDiscussionsByLessonContaining(String keyword);


}