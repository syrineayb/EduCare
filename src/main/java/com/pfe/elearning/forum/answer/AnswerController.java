package com.pfe.elearning.forum.answer;

import com.pfe.elearning.forum.Message.MessageMapper;
import com.pfe.elearning.forum.Message.MessageRepository;
import com.pfe.elearning.forum.answer.dto.ReplyResponse;
import com.pfe.elearning.user.entity.User;
import com.pfe.elearning.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/answers")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;
    private final UserRepository userRepository;
    private final MessageRepository discussionRepository;
        @PostMapping("/create/{discussionId}")
        public ResponseEntity<AnswerResponse> createAnswer(@PathVariable Integer discussionId,
                                                           @RequestBody AnswerRequest answerRequest) {
            // Get the logged-in user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User author = userRepository.findByEmail(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

            AnswerResponse answerResponse = answerService.createAnswer(discussionId, answerRequest,author.getId());
            return ResponseEntity.ok(answerResponse);
        }

    @PutMapping("/update/{answerId}")
    public ResponseEntity<AnswerResponse> updateAnswer(@PathVariable Integer answerId,
                                                       @RequestBody AnswerRequest answerRequest) {
        AnswerResponse answerResponse = answerService.updateAnswer(answerId, answerRequest);
        return ResponseEntity.ok(answerResponse);
    }

    @DeleteMapping("/delete/{answerId}")
    public ResponseEntity<Void> deleteAnswer(@PathVariable Integer answerId) {
        answerService.deleteAnswer(answerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{answerId}")
    public ResponseEntity<AnswerResponse> getAnswerById(@PathVariable Integer answerId) {
        AnswerResponse answer = answerService.getAnswerById(answerId);
        return ResponseEntity.ok(answer);
    }




    @GetMapping("/discussion/{discussionId}")
    public ResponseEntity<List<AnswerResponse>> getAnswersByDiscussion(@PathVariable Integer discussionId) {
        List<AnswerResponse> answers = answerService.getAnswersByDiscussion(discussionId);
        return ResponseEntity.ok(answers);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AnswerResponse>> getAnswersByUser(@PathVariable Integer userId) {
        List<AnswerResponse> answers = answerService.getAnswersByUser(userId);
        return ResponseEntity.ok(answers);
    }

    @GetMapping("/discussion/{discussionId}/count")
    public ResponseEntity<Integer> getAnswersCountByDiscussion(@PathVariable Integer discussionId) {
        Integer count = answerService.getAnswersCountByDiscussion(discussionId);
        return ResponseEntity.ok(count);
    }

   /* @GetMapping("/latest")
    public ResponseEntity<List<AnswerResponse>> getLatestAnswers(@RequestParam int limit) {
        List<AnswerResponse> answers = answerService.getLatestAnswers(limit);
        return ResponseEntity.ok(answers);
    }

    */

    @GetMapping("/search")
    public ResponseEntity<List<AnswerResponse>> searchAnswers(@RequestParam String keyword) {
        List<AnswerResponse> answers = answerService.searchAnswers(keyword);
        return ResponseEntity.ok(answers);
    }

    @PostMapping("/{answerId}/like")
    public ResponseEntity<Void> likeAnswer(@PathVariable Integer answerId, @RequestParam Integer userId) {
        answerService.likeAnswer(answerId, userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    /*
    Replies methods
     */



    @PostMapping("/{parentAnswerId}/reply")
    public ResponseEntity<ReplyResponse> createReply(
            @PathVariable Integer parentAnswerId,
            @RequestBody AnswerRequest request) {
        // Get the logged-in user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User author = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        ReplyResponse createdResponse = answerService.createReply(parentAnswerId, request,author.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdResponse);
    }

    @GetMapping("/{parentAnswerId}/replies")
    public ResponseEntity<List<ReplyResponse>> getRepliesByParentAnswerId(@PathVariable Integer parentAnswerId) {
        List<ReplyResponse> replies = answerService.getRepliesByParentAnswerId(parentAnswerId);
        return ResponseEntity.ok(replies);
    }



}

/*
    @GetMap
    ping("/discussion/{discussionId}/count")
    public ResponseEntity<Integer> getAnswersCountByDiscussionId(@PathVariable Integer discussionId) {
        Integer count = answerService.getAnswersCountByDiscussionId(discussionId);
        return ResponseEntity.ok(count);
    }


 */
    /*@PostMapping("/{answerId}/dislike")
    public ResponseEntity<Void> dislikeAnswer(@PathVariable Integer answerId, @RequestParam Integer userId) {
        answerService.dislikeAnswer(answerId, userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

     */
