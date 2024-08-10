package com.pfe.elearning.forum.Message;

import com.pfe.elearning.common.PageResponse;
import com.pfe.elearning.course.dto.CourseResponse;
import com.pfe.elearning.course.service.CourseService;

import com.pfe.elearning.forum.entity.Forum;
import com.pfe.elearning.forum.repository.ForumRepository;
import com.pfe.elearning.lesson.dto.LessonResponse;
import com.pfe.elearning.lesson.service.LessonService;
import com.pfe.elearning.user.entity.User;
import com.pfe.elearning.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final ForumRepository forumRepository;
    private final UserRepository userRepository;
  //  private final MessageService messageService;
    private final MessageMapper discussionMapper;
    private final CourseService courseService;
    private final LessonService lessonService;
 /*   @Override
    public DiscussionResponse createDiscussion(DiscussionRequest discussionRequest, Integer forumId, Integer authorId) {
        // Retrieve the author from the database
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + authorId));

        // Retrieve the forum associated with the discussion
        Forum forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new EntityNotFoundException("Forum not found with id: " + forumId));

        // Map the discussion request to a discussion entity
        Discussion discussion = discussionMapper.toDiscussion(discussionRequest, forum, author);

        // Save the discussion entity to the database
        discussion = discussionRepository.save(discussion);

        // Map the saved discussion entity to a discussion response and return it
        return discussionMapper.toDiscussionResponse(discussion);
    }

  */
 @Override
 public MessageResponse createDiscussion(MessageRequest discussionRequest, String imageUrl, String videoUrl, String pdfUrl, String recordUrl, Integer forumId, Integer authorId) {
     // Retrieve the author from the database
     User author = userRepository.findById(authorId)
             .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + authorId));
     // Retrieve the forum associated with the discussion
     Forum forum = forumRepository.findById(forumId)
             .orElseThrow(() -> new EntityNotFoundException("Forum not found with id: " + forumId));

     // Map the discussion request to a discussion entity
     Message discussion = discussionMapper.toDiscussion(discussionRequest, forum, author);
     discussion.setRecordUrl(recordUrl);
     discussion.setImageUrl(imageUrl);
     discussion.setVideoUrl(videoUrl);
     discussion.setPdfUrl(pdfUrl);
     Message savedDiscussion = messageRepository.save(discussion);
     return discussionMapper.toDiscussionResponse(savedDiscussion);
 }

    @Override
    public PageResponse<MessageResponse> searchDiscussionsBySubjectContaining(int forumId,String keyword, int page, int size) {
        Forum forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new EntityNotFoundException("Forum not found with id: " + forumId));

        Page<Message> discussionsPage = messageRepository.findByForumIdAndSubjectContainingIgnoreCaseOrderByCreatedAtDesc(forumId,keyword, PageRequest.of(page, size));

        List<MessageResponse> discussionResponses = discussionsPage.getContent().stream()
                .map(discussionMapper::toDiscussionResponse)
                .collect(Collectors.toList());

        return PageResponse.<MessageResponse>builder()
                .content(discussionResponses)
                .totalPages(discussionsPage.getTotalPages())
                .totalElements(discussionsPage.getTotalElements())
                .build();
    }


    @Override
    public List<MessageResponse> searchDiscussionsByLessonContaining(String keyword) {
        List<Message> discussions = messageRepository.findByForum_Lesson_titleContainingIgnoreCaseOrderByCreatedAtDesc(keyword);
        return discussions.stream()
                .map(discussionMapper::toDiscussionResponse)
                .collect(Collectors.toList());
    }



    @Override
    public PageResponse<MessageResponse> getAllDiscussionsByForumId(int forumId, int page, int size) {
        Page<Message> discussionPage = messageRepository.findByForumIdOrderByCreatedAtDesc(forumId, PageRequest.of(page, size));
        List<MessageResponse> discussionResponses = discussionPage.getContent().stream()
                .map(discussionMapper::toDiscussionResponse)
                .collect(Collectors.toList());

        return PageResponse.<MessageResponse>builder()
                .content(discussionResponses)
                .totalPages(discussionPage.getTotalPages())
                .totalElements(discussionPage.getTotalElements())
                .build();
    }

    @Override
    public PageResponse<MessageResponse> getAllDiscussionsByLessonId(int lessonId, int page, int size) {
        // Retrieve authenticated user information
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();

        // Retrieve discussions from repository
        Page<Message> discussionPage = messageRepository.findByForum_LessonId(lessonId, PageRequest.of(page, size));

        // Map Discussion entities to DiscussionResponse objects and include authenticated user
        List<MessageResponse> discussionResponses = discussionPage.getContent().stream()
                .map(discussion -> {
                    MessageResponse response = discussionMapper.toDiscussionResponse(discussion);
                    return response;
                })
                .collect(Collectors.toList());

        return PageResponse.<MessageResponse>builder()
                .content(discussionResponses)
                .totalPages(discussionPage.getTotalPages())
                .totalElements(discussionPage.getTotalElements())
                .build();
    }

    @Override
    public PageResponse<MessageResponse> getInstructorLessonDiscussions(String instructorUsername, int page, int size) {
        // Retrieve all courses created by the instructor
        List<CourseResponse> courses = courseService.getAllCoursesByInstructor(instructorUsername);

        // Initialize a list to store discussions
        List<Message> allDiscussions = new ArrayList<>();

        // Iterate through courses to get lessons and discussions
        for (CourseResponse course : courses) {
            // Retrieve lessons associated with the course
            List<LessonResponse> lessons = lessonService.getAllLessonsByCourse(course.getId());

            // Iterate through lessons to get discussions
            for (LessonResponse lesson : lessons) {
                // Retrieve discussions associated with the lesson
                List<Message> lessonDiscussions = messageRepository.findByForum_LessonId(lesson.getId());
                // Add the discussions to the list
                allDiscussions.addAll(lessonDiscussions);
            }
        }

        // Sort all discussions by createdAt in descending order
        allDiscussions.sort((d1, d2) -> d2.getCreatedAt().compareTo(d1.getCreatedAt()));

        // Apply pagination
        int start = Math.min(page * size, allDiscussions.size());
        int end = Math.min(start + size, allDiscussions.size());
        List<MessageResponse> paginatedDiscussions = allDiscussions.subList(start, end)
                .stream()
                .map(discussionMapper::toDiscussionResponse)
                .collect(Collectors.toList());

        // Create a PageResponse object
        return PageResponse.<MessageResponse>builder()
                .content(paginatedDiscussions)
                .totalPages((allDiscussions.size() + size - 1) / size)
                .totalElements((long) allDiscussions.size())
                .build();
    }


    @Override
    public List<MessageResponse> getAllDiscussionsByLessonId(int lessonId) {
        // Retrieve discussions from the repository based on the lesson ID
        List<Message> discussions = messageRepository.findByForum_LessonId(lessonId);

        // Map Discussion entities to DiscussionResponse objects
        return discussions.stream()
                .map(discussionMapper::toDiscussionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MessageResponse updateDiscussion(Integer id, MessageRequest discussionRequest, String imageUrl, String videoUrl, String pdfUrl, String recordUrl, Integer authorId) {
        // Retrieve the discussion from the database
        Message discussion = messageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Discussion not found with id: " + id));

        // Update the discussion fields based on the request
        discussion.setSubject(discussionRequest.getSubject());
        discussion.setMessageText(discussionRequest.getMessageText());
        // Update other fields as needed

        // Update URLs if provided
        if (imageUrl != null) {
            discussion.setImageUrl(imageUrl);
        }
        if (videoUrl != null) {
            discussion.setVideoUrl(videoUrl);
        }
        if (pdfUrl != null) {
            discussion.setPdfUrl(pdfUrl);
        }
        if (recordUrl != null) {
            discussion.setRecordUrl(recordUrl);
        }

        // Retrieve the author from the database (optional step, if you need to update author)
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + authorId));
        discussion.setAuthor(author);

        // Save the updated discussion entity back to the database
        Message updatedDiscussion = messageRepository.save(discussion);

        // Map the updated discussion entity to a response DTO
        MessageResponse response = discussionMapper.toDiscussionResponse(updatedDiscussion);

        return response;
    }


    /* @Override
    public DiscussionResponse updateDiscussion(Integer id, DiscussionRequest discussionRequest) {
        // Retrieve the discussion from the database
        Discussion discussion = discussionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Discussion not found with id: " + id));

        // Update the discussion with the new details
        discussion.setSubject(discussionRequest.getSubject());

        // Save the updated discussion entity
        discussion = discussionRepository.save(discussion);

        // Map the updated discussion entity to a response and return it
        return discussionMapper.toDiscussionResponse(discussion);
    }


    */
    @Override
    public void deleteDiscussion(Integer id) {
        // Check if the discussion exists
        Message discussion = messageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Discussion not found with id: " + id));

        // Delete the discussion from the database
        messageRepository.delete(discussion);
    }



}
    /*

    @Override
    public DiscussionResponse getDiscussionById(Integer id) {
        Discussion discussion = discussionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Discussion not found " + id));
        return discussionMapper.toDiscussionResponse(discussion);
    }

   /*@Override
    public DiscussionResponse createDiscussionWithMessage(Integer forumId, DiscussionRequest discussionRequest, MessageRequest messageRequest, User author) {
        Forum forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new EntityNotFoundException("Forum not found " + forumId));

        Discussion discussion = discussionMapper.toDiscussion(discussionRequest, forum, author);
        Discussion savedDiscussion = discussionRepository.save(discussion);

        // Create message
        Message message = messageMapper.toMessage(messageRequest, savedDiscussion, author);
        Message savedMessage = messageRepository.save(message);

        savedDiscussion.setMessages(Collections.singletonList(savedMessage));

        return discussionMapper.toDiscussionResponse(savedDiscussion);
    }

     */
  /* @Override
   public DiscussionResponse createDiscussion(DiscussionRequest discussionRequest, Integer authorId) {
       // Retrieve the user entity from the repository
       User author = userRepository.findById(authorId)
               .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + authorId));

       // Retrieve the forum entity from the repository
       Forum forum = forumRepository.findById(discussionRequest.getForumId())
               .orElseThrow(() -> new IllegalArgumentException("Forum not found with id: " + discussionRequest.getForumId()));

       // Map and save the discussion entity
       Discussion discussion = discussionMapper.toDiscussion(discussionRequest, forum, author);
       discussionRepository.save(discussion);

       // Create the associated message
       MessageRequest messageRequest = discussionRequest.getMessage();
       MessageResponse messageResponse = messageService.createMessage(discussion.getId(), authorId, messageRequest);

       // Set the message to the discussion and save again
       discussion.setMessage(messageService.getMessageEntityById(messageResponse.getId()));
       discussionRepository.save(discussion);

       // Map the discussion and message to a response DTO and return it
       return discussionMapper.toDiscussionResponse(discussion);
   }


    @Override
    public DiscussionResponse updateDiscussion(Integer id, DiscussionRequest discussionRequest) {
        Discussion existingDiscussion = discussionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Discussion not found " + id));
        existingDiscussion.setSubject(discussionRequest.getSubject());
        Discussion updatedDiscussion = discussionRepository.save(existingDiscussion);
        return discussionMapper.toDiscussionResponse(updatedDiscussion);
    }

    @Override
    public void deleteDiscussion(Integer id) {
        Discussion discussion = discussionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Discussion not found " + id));
        discussionRepository.delete(discussion);
    }

   */
