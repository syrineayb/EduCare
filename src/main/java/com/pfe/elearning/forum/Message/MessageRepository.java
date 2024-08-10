package com.pfe.elearning.forum.Message;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message,Integer>

    {
        //    @Query("SELECT d FROM Discussion d LEFT JOIN FETCH d.author LEFT JOIN FETCH d.message LEFT JOIN FETCH d.forum WHERE d.forum.id = :forumId")
        @Query("SELECT d FROM Message d LEFT JOIN FETCH d.author LEFT JOIN FETCH d.forum WHERE d.forum.id = :forumId")
        Page<Message> findByForumId(int forumId, Pageable pageable);
        Page<Message> findByForum_LessonId(int lessonId, Pageable pageable);
        List<Message> findByForum_LessonId(int lessonId);
        Page<Message> findByForumIdAndSubjectContainingIgnoreCaseOrderByCreatedAtDesc(int forumId,String keyword, Pageable pageable);

        List<Message>  findByForum_Lesson_titleContainingIgnoreCaseOrderByCreatedAtDesc(String keyword);

        Page<Message> findByForumIdOrderByCreatedAtDesc(int forumId, Pageable pageable);
    }
