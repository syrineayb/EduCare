package com.pfe.elearning.topic.repository;

import com.pfe.elearning.topic.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TopicRepository extends JpaRepository<Topic,Integer> {
  /*  @Query("SELECT t FROM Topic t WHERE t.title LIKE %:keyword%")
    Page<Topic> findByTitleContainingCustom(@Param("keyword") String keyword, Pageable pageable);
   */
      @Query("SELECT t FROM Topic t WHERE t.title LIKE %:keyword%")
  Page<Topic> findByTitleContaining(String keyword, Pageable pageable);

    List<Topic> findByTitleContaining(String keyword);

    Page<Topic> findAllByOrderByCreatedAtDesc(PageRequest of);
}