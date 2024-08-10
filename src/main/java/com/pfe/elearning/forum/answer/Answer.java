package com.pfe.elearning.forum.answer;

import com.pfe.elearning.common.BaseEntity;
import com.pfe.elearning.forum.Message.Message;
import com.pfe.elearning.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "answers")
@EqualsAndHashCode(callSuper = true)
public class Answer extends BaseEntity {
    private String messageContent;
    private int likes; // Number of likes for the answer

    @ManyToOne
    @JoinColumn(name = "discussion_id")
    private Message discussion;  // This is the association causing the issue

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_answer_id")
    private Answer parentAnswer;
    @OneToMany(mappedBy = "parentAnswer", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Answer> replies;


    public void addReply(Answer reply) {
        this.replies.add(reply);
    }
/*
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private Message parentMessage;


 */

}
