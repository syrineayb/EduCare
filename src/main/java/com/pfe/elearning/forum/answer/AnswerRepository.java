package com.pfe.elearning.forum.answer;

import com.pfe.elearning.forum.answer.Answer;
import com.pfe.elearning.forum.Message.Message;
import com.pfe.elearning.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer,Integer> {
    List<Answer> findByDiscussion(Message discussion);

    List<Answer> findByUser(User user);

    Integer countByDiscussion(Message discussion);

    //
 //   List<Answer> findAnswerOrderByCreatedAtDesc(int limit);

    List<Answer> findByMessageContentContainingIgnoreCase(String keyword);

    Integer countByDiscussionId(Integer discussionId);

    List<Answer> findByDiscussionOrderByCreatedAtAsc(Message discussion);
    List<Answer> findByParentAnswerId(Integer parentAnswerId);

}