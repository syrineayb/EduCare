package com.pfe.elearning.forum.Message;

import com.pfe.elearning.forum.answer.AnswerMapper;
import com.pfe.elearning.forum.answer.AnswerResponse;
import com.pfe.elearning.forum.answer.Answer;
import com.pfe.elearning.forum.entity.Forum;
import com.pfe.elearning.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageMapper {
    private final AnswerMapper answerMapper;

    public MessageResponse toDiscussionResponse(Message discussion) {
        if (discussion == null) {
            return null; // Or throw an exception if required
        }

        // Map the direct answers of the discussion
        List<AnswerResponse> directAnswers = Optional.ofNullable(discussion.getAnswers())
                .orElse(Collections.emptyList())
                .stream()
                .map(this::mapAnswerWithReplies) // Use a method to map answers with associated replies
                .collect(Collectors.toList());

        // Map the discussion entity to a DiscussionResponse
        return MessageResponse.builder()
                .id(discussion.getId())
                .subject(discussion.getSubject())
                .messageText(discussion.getMessageText())
                .recordUrl(discussion.getRecordUrl())
                .imageUrl(discussion.getImageUrl())
                .videoUrl(discussion.getVideoUrl())
                .pdfUrl(discussion.getPdfUrl())
                .lessonTitle(discussion.getForum().getLesson() != null ? discussion.getForum().getLesson().getTitle() : null)
                .createdAt(discussion.getCreatedAt())
                .updatedAt(discussion.getUpdatedAt())
                .authorId(discussion.getAuthor().getId())
                .authorFullName(discussion.getAuthor().getFullname())
                .forumId(discussion.getForum().getId())
                .answers(directAnswers)
                .build();
    }

    private AnswerResponse mapAnswerWithReplies(Answer answer) {
        AnswerResponse answerResponse = answerMapper.toAnswerResponse(answer);
        if (answer.getReplies() != null && !answer.getReplies().isEmpty()) {
            List<AnswerResponse> replies = answer.getReplies().stream()
                    .map(this::mapAnswerWithReplies)
                    .collect(Collectors.toList());
            answerResponse.setReplies(replies);
        }
        return answerResponse;
    }

    public Message toDiscussion(MessageRequest request, Forum forum, User author) {
        Message discussion = new Message();
        discussion.setSubject(request.getSubject());
        discussion.setMessageText(request.getMessageText());
        //discussion.setRecordUrl(request.getRecordFile() != null ? saveFile(request.getRecordFile()) : null);
        //discussion.setImageUrl(request.getImageFile() != null ? saveFile(request.getImageFile()) : null);
        //discussion.setVideoUrl(request.getVideoFile() != null ? saveFile(request.getVideoFile()) : null);
        //discussion.setPdfUrl(request.getPdfFile() != null ? saveFile(request.getPdfFile()) : null);
        discussion.setForum(forum);
        discussion.setAuthor(author);
        return discussion;
    }


}
