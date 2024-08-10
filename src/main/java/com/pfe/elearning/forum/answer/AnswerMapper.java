package com.pfe.elearning.forum.answer;

import com.pfe.elearning.forum.Message.Message;
import com.pfe.elearning.forum.answer.dto.ReplyResponse;
import com.pfe.elearning.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AnswerMapper {

    public AnswerResponse toAnswerResponse(Answer answer) {
        AnswerResponse.AnswerResponseBuilder builder = AnswerResponse.builder()
                .id(answer.getId())
                .messageId(answer.getDiscussion() != null ? answer.getDiscussion().getId() : null)
                .forumId(answer.getDiscussion() != null && answer.getDiscussion().getForum() != null ? answer.getDiscussion().getForum().getId() : null)
                .messageContent(answer.getMessageContent())
                .authorId(answer.getUser() != null ? answer.getUser().getId() : null)
                .authorFullName(answer.getUser() != null ? answer.getUser().getFullname() : null)
                .message(answer.getDiscussion() != null ? answer.getDiscussion().getMessageText() : null)
                .parentAnswerId(answer.getParentAnswer() != null ? answer.getParentAnswer().getId() : null)
                .createdAt(answer.getCreatedAt())
                .updatedAt(answer.getUpdatedAt());

        // Mapping nested replies recursively
        List<AnswerResponse> nestedReplies = mapNestedReplies(answer.getReplies());
        builder.replies(nestedReplies);

        return builder.build();
    }

    public List<AnswerResponse> mapNestedReplies(List<Answer> replies) {
        if (replies == null) {
            return null;
        }
        return replies.stream()
                .map(this::toAnswerResponse)
                .collect(Collectors.toList());
    }

    public Answer toAnswer(AnswerRequest request, Message discussion, User author, Answer parentAnswer) {
        Answer answer = new Answer();
        answer.setMessageContent(request.getMessageContent());
        answer.setDiscussion(discussion);
        answer.setUser(author);
        answer.setParentAnswer(parentAnswer);
        return answer;
    }

    public ReplyResponse toReplyResponse(Answer answer) {
        User user = answer.getUser();
        return ReplyResponse.builder()
                .id(answer.getId())
                .messageContent(answer.getMessageContent())
                .parentAnswerId(answer.getParentAnswer() != null ? answer.getParentAnswer().getId() : null)
                .authorId(answer.getUser() != null ? answer.getUser().getId() : null)
                .authorFullName(answer.getUser() != null ? answer.getUser().getFullname() : null)
                .createdAt(answer.getCreatedAt())
                .updatedAt(answer.getUpdatedAt())
                .build();
    }


    public Answer toAnswer(ReplyResponse response, Message discussion, User author, Answer parentAnswer) {
        Answer answer = new Answer();
        answer.setMessageContent(response.getMessageContent());
        answer.setDiscussion(discussion);
        answer.setUser(author);
        answer.setParentAnswer(parentAnswer);
        return answer;
    }
}
