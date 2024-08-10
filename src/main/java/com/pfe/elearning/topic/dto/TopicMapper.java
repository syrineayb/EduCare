package com.pfe.elearning.topic.dto;

import com.pfe.elearning.topic.entity.Topic;
import org.springframework.stereotype.Service;
@Service
public class TopicMapper {
    public TopicResponse toTopicResponse(Topic topic) {
        return TopicResponse.builder()
                .id(topic.getId())
                .title(topic.getTitle())
                .imageFile(topic.getImageFile())
                .createdAt(topic.getCreatedAt())
                .updatedAt(topic.getUpdatedAt())
                .build();
    }

    public Topic toTopic(TopicRequest topicRequest) {
        if(topicRequest == null){
            throw new NullPointerException("The topic request should not be null");
        }
        Topic topic = new Topic();
        topic.setTitle(topicRequest.getTitle());
        //topic.setImageFile(topicRequest.getImageFile());
        return topic;
    }
/*
    public Topic toTopic(TopicRequest topicRequest, String imageFile) {
        Topic topic = toTopic(topicRequest);
        topic.setImageFile(imageFile); // Set the image URL
        return topic;
    }

 */
}
