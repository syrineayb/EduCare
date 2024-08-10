package com.pfe.elearning.forum.answer;

import com.pfe.elearning.forum.answer.AnswerRequest;
import com.pfe.elearning.forum.answer.AnswerResponse;
import com.pfe.elearning.forum.answer.dto.ReplyResponse;

import java.util.List;

public interface AnswerService {

    AnswerResponse createAnswer(Integer discussionId, AnswerRequest request, Integer authorId) ;
    AnswerResponse getAnswerById(Integer answerId);

    AnswerResponse updateAnswer(Integer answerId, AnswerRequest request);

    void deleteAnswer(Integer answerId);

    List<AnswerResponse> getAnswersByDiscussion(Integer discussionId);

    List<AnswerResponse> getAnswersByUser(Integer userId);

    Integer getAnswersCountByDiscussion(Integer discussionId);

    //  List<AnswerResponse> getLatestAnswers(int limit);

    List<AnswerResponse> searchAnswers(String keyword);

    void likeAnswer(Integer answerId, Integer userId);

    Integer getAnswersCountByDiscussionId(Integer messageId);


    // Ajouter ces méthodes pour gérer les répliques

    AnswerResponse updateReply(Integer replyId, AnswerRequest request);

    void deleteReply(Integer replyId);

    ReplyResponse createReply(Integer parentAnswerId, AnswerRequest request, Integer authorId) ;
    List<ReplyResponse> getRepliesByParentAnswerId(Integer parentAnswerId);
    //retreive last answer  List<AnswerResponse> getLatestAnswers(int limit);
    // get count answers by message id

    // void dislikeAnswer(Integer answerId, Integer userId);
}