package com.pfe.elearning.forum.Message;

import com.pfe.elearning.common.BaseEntity;
import com.pfe.elearning.forum.answer.Answer;
import com.pfe.elearning.forum.entity.Forum;
import com.pfe.elearning.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "messages")
public class Message extends BaseEntity {
    private String subject;
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String messageText;

    private String recordUrl;

    @Column(nullable = true)
    private String imageUrl;

    @Column(nullable = true)
    private String videoUrl;

    @Column(nullable = true)
    private String pdfUrl;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "forum_id", nullable = false)
    private Forum forum;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
/*
    @OneToOne(mappedBy = "discussion", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private Message message;
 */


    @OneToMany(mappedBy = "discussion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers;

}
