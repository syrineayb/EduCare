package com.pfe.elearning.forum.entity;

import com.pfe.elearning.common.BaseEntity;
import com.pfe.elearning.forum.Message.Message;
import com.pfe.elearning.lesson.entity.Lesson;
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
@Table(name = "forums")
@EqualsAndHashCode(callSuper = true)
public class Forum extends BaseEntity {
    private String title;

    @OneToMany(mappedBy = "forum", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> discussions = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;
/*
    @ManyToOne
    @JoinColumn(name = "instructor_id")
    private User instructor;

 */
}
