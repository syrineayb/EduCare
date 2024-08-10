package com.pfe.elearning.forum.answer.serviceImpl;

import com.pfe.elearning.forum.Message.MessageMapper;
import com.pfe.elearning.forum.Message.MessageRepository;
import com.pfe.elearning.forum.answer.AnswerMapper;
import com.pfe.elearning.forum.answer.AnswerRequest;
import com.pfe.elearning.forum.answer.AnswerResponse;
import com.pfe.elearning.forum.answer.Answer;
import com.pfe.elearning.forum.Message.Message;
import com.pfe.elearning.forum.answer.AnswerRepository;
import com.pfe.elearning.forum.answer.AnswerService;
import com.pfe.elearning.forum.answer.dto.ReplyResponse;
import com.pfe.elearning.user.entity.User;
import com.pfe.elearning.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {
    private final AnswerRepository answerRepository;
    private final MessageRepository discussionRepository;
    private final UserRepository userRepository;
    private final AnswerMapper answerMapper;


    @Override
    public AnswerResponse createAnswer(Integer discussionId, AnswerRequest request, Integer authorId) {
        Optional<Message> discussionOpt = discussionRepository.findById(discussionId);
        Optional<User> authorOpt = userRepository.findById(authorId);

        if (discussionOpt.isEmpty()) {
            throw new IllegalArgumentException("Discussion not found for id: " + discussionId);
        }
        if (authorOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found for id: " + authorId);
        }

        Message discussion = discussionOpt.get();
        User author = authorOpt.get();

        // Create the Answer entity and associate it with Discussion, User, and parent Answer if exists
        Answer answer = answerMapper.toAnswer(request, discussion, author, null);

        // Save the Answer entity to the database
        try {
            Answer savedAnswer = answerRepository.save(answer);
            return answerMapper.toAnswerResponse(savedAnswer);
        } catch (DataIntegrityViolationException e) {
            // Log the specific error message and handle appropriately
            throw new IllegalArgumentException("Failed to create answer due to data integrity violation: " + e.getMessage());
        }
    }

    @Override
    public AnswerResponse updateAnswer(Integer answerId, AnswerRequest request) {
        Optional<Answer> answerOpt = answerRepository.findById(answerId);

        if (answerOpt.isEmpty()) {
            throw new IllegalArgumentException("Answer not found");
        }

        Answer answer = answerOpt.get();
        answer.setMessageContent(request.getMessageContent());

        Answer updatedAnswer = answerRepository.save(answer);
        return answerMapper.toAnswerResponse(updatedAnswer);
    }

    @Override
    public void deleteAnswer(Integer answerId) {
        Optional<Answer> answerOpt = answerRepository.findById(answerId);

        if (answerOpt.isEmpty()) {
            throw new IllegalArgumentException("Answer not found");
        }

        answerRepository.deleteById(answerId);
    }


    @Override
    public AnswerResponse getAnswerById(Integer answerId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new IllegalArgumentException("Answer not found with id: " + answerId));
        return answerMapper.toAnswerResponse(answer);
    }





    @Override
    public List<AnswerResponse> getAnswersByDiscussion(Integer discussionId) {
        Message discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new IllegalArgumentException("Discussion not found with id: " + discussionId));

        List<Answer> answers = answerRepository.findByDiscussionOrderByCreatedAtAsc(discussion); // Assuming you have a method findByDiscussionOrderByCreatedAtAsc in your repository
        return answers.stream()
                .map(answerMapper::toAnswerResponse)
                .collect(Collectors.toList());
    }


    @Override
    public List<AnswerResponse> getAnswersByUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        List<Answer> answers = answerRepository.findByUser(user);
        return answers.stream()
                .map(answerMapper::toAnswerResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Integer getAnswersCountByDiscussion(Integer discussionId) {
        Message discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new IllegalArgumentException("Discussion not found with id: " + discussionId));

        return answerRepository.countByDiscussion(discussion);
    }

   /* @Override
    public List<AnswerResponse> getLatestAnswers(int limit) {
        List<Answer> latestAnswers = answerRepository.findAnswerOrderByCreatedAtDesc(limit);
        return latestAnswers.stream()
                .map(answerMapper::toAnswerResponse)
                .collect(Collectors.toList());
    }

    */

    @Override
    public List<AnswerResponse> searchAnswers(String keyword) {
        List<Answer> answers = answerRepository.findByMessageContentContainingIgnoreCase(keyword);
        return answers.stream()
                .map(answerMapper::toAnswerResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void likeAnswer(Integer answerId, Integer userId) {
        // Retrieve the answer from the database
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new IllegalArgumentException("Answer not found with id: " + answerId));

        // Perform logic to like the answer
        // For example, you can update the answer entity to increment the likes count
        answer.setLikes(answer.getLikes() + 1);

        // Save the updated answer entity back to the database
        answerRepository.save(answer);
    }


    @Override
    public Integer getAnswersCountByDiscussionId(Integer discussionId) {
        return answerRepository.countByDiscussionId(discussionId);
    }

    @Override
    public ReplyResponse createReply(Integer parentAnswerId, AnswerRequest request, Integer authorId) {
        // Get authenticated user from SecurityContextHolder

        // Retrieve user information if needed, e.g., author = userRepository.findByUsername(userDetails.getUsername());

        Optional<Answer> parentAnswerOpt = answerRepository.findById(parentAnswerId);
        Optional<User> authorOpt = userRepository.findById(authorId);

        if (authorOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found for id: " + authorId);
        }
        if (parentAnswerOpt.isEmpty()) {
            throw new IllegalArgumentException("Parent answer not found for ID: " + parentAnswerId);
        }

        Answer parentAnswer = parentAnswerOpt.get();
        Message discussion = parentAnswer.getDiscussion();
        User author = authorOpt.get();

        // Create a new reply
        Answer reply = answerMapper.toAnswer(request, discussion, author, parentAnswer); // Assuming you retrieve author separately

        // Add the reply to the parent object's reply list
        parentAnswer.addReply(reply);

        // Save the reply
        answerRepository.save(reply);

        // Map the reply to ReplyResponse
        return answerMapper.toReplyResponse(reply);
    }
    @Override
    public List<ReplyResponse> getRepliesByParentAnswerId(Integer parentAnswerId) {
        List<Answer> replies = answerRepository.findByParentAnswerId(parentAnswerId);
        if (replies.isEmpty()) {
            throw new IllegalStateException("No replies found for parent answer ID: " + parentAnswerId);
        }
        return replies.stream()
                .map(answerMapper::toReplyResponse)
                .collect(Collectors.toList());
    }


    @Override
    public AnswerResponse updateReply(Integer replyId, AnswerRequest request) {
        Optional<Answer> replyOpt = answerRepository.findById(replyId);

        if (replyOpt.isEmpty()) {
            throw new IllegalArgumentException("Reply not found");
        }

        Answer reply = replyOpt.get();
        reply.setMessageContent(request.getMessageContent());

        Answer updatedReply = answerRepository.save(reply);
        return answerMapper.toAnswerResponse(updatedReply);
    }

    @Override
    public void deleteReply(Integer replyId) {
        Optional<Answer> replyOpt = answerRepository.findById(replyId);

        if (replyOpt.isEmpty()) {
            throw new IllegalArgumentException("Reply not found");
        }

        answerRepository.deleteById(replyId);
    }




}

   /* @Override
    public void dislikeAnswer(Integer answerId, Integer userId) {
        // Retrieve the answer from the database
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new IllegalArgumentException("Answer not found with id: " + answerId));

        // Perform logic to dislike the answer
        // For example, you can update the answer entity to increment the dislikes count
        answer.setDislikes(answer.getDislikes() + 1);

        // Save the updated answer entity back to the database
        answerRepository.save(answer);
    }

    */
