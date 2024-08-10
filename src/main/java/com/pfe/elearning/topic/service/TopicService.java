package com.pfe.elearning.topic.service;

import com.pfe.elearning.common.PageResponse;
import com.pfe.elearning.topic.dto.TopicRequest;
import com.pfe.elearning.topic.dto.TopicResponse;
import org.springframework.core.io.Resource;

import java.util.List;


public interface TopicService {
    PageResponse<TopicResponse> getLatestTopicsForCandidates(int page, int size);
    TopicResponse createTopic(TopicRequest topicRequest, String imageUrl);
    TopicResponse findById(Integer id);
    PageResponse<TopicResponse> findAll(int page, int size);
    List<TopicResponse> getAllTopics();
    PageResponse<TopicResponse> findTopicsInBatch(int page, int size);

    void deleteById(Integer id);
    boolean existsById(Integer id);
    TopicResponse update(Integer id, TopicRequest topicRequest,String imageUrl);
   // TopicResponse update(Integer id, TopicRequest domainRequest); // New method for updating a domain
    PageResponse<TopicResponse> findByTitleContaining(String keyword, int page, int size);
    List<String> getAllTopicTitles(); // Nouvelle méthode pour obtenir tous les titres des sujets
    List<TopicResponse> findTopicsByTitleContaining(String keyword);
    List<TopicResponse> getListOfTopics();

}
