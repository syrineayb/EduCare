package com.pfe.elearning.topic.service.ServiceImpl;

import com.pfe.elearning.common.PageResponse;

import com.pfe.elearning.file.services.FileService;
import com.pfe.elearning.topic.dto.TopicMapper;
import com.pfe.elearning.topic.dto.TopicRequest;
import com.pfe.elearning.topic.dto.TopicResponse;
import com.pfe.elearning.topic.entity.Topic;
import com.pfe.elearning.topic.repository.TopicRepository;
import com.pfe.elearning.topic.service.TopicService;
import com.pfe.elearning.user.dto.UserResponse;
import com.pfe.elearning.user.entity.User;
import com.pfe.elearning.validator.ObjectsValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class TopicServiceImpl implements TopicService {
    private final TopicRepository topicRepository;
    private final TopicMapper topicMapper;
    private final ObjectsValidator<TopicRequest> validator;
     // Inject FileService

@Autowired
@Qualifier("TopicImageServiceImpl")
private FileService fileService ;

@Override
    public TopicResponse createTopic(TopicRequest topicRequest, String imageUrl) {
        validator.validate(topicRequest);
        Topic newTopic = topicMapper.toTopic(topicRequest);
        newTopic.setImageFile(imageUrl);
        Topic savedTopic = topicRepository.save(newTopic);
        return topicMapper.toTopicResponse(savedTopic);
    }
    @Override
    @Transactional
    public TopicResponse update(Integer id, TopicRequest topicRequest,String imageUrl) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topic not found with id: " + id));
        // Update the topic fields
        topic.setTitle(topicRequest.getTitle());
        topic.setImageFile(imageUrl);

        // Save the updated topic
        topicRepository.save(topic);

        return topicMapper.toTopicResponse(topic);
    }


    @Override
    public PageResponse<TopicResponse> findTopicsInBatch(int page, int size) {
        Page<Topic> topicPage = topicRepository.findAll(PageRequest.of(page, size));
        List<TopicResponse> topicResponses = topicPage.getContent().stream()
                .map(topicMapper::toTopicResponse)
                .collect(Collectors.toList());
        return PageResponse.<TopicResponse>builder()
                .content(topicResponses)
                .totalPages(topicPage.getTotalPages())
                .totalElements(topicPage.getTotalElements())
                .build();
    }
    @Override
    public PageResponse<TopicResponse> findAll(int page, int size) {
        Page<Topic> pageResult = topicRepository.findAll(PageRequest.of(page, size));
        List<TopicResponse> topicResponses = pageResult.getContent().stream()
                .map(topicMapper::toTopicResponse)
                .collect(Collectors.toList());

        return PageResponse.<TopicResponse>builder()
                .content(topicResponses)
                .totalPages(pageResult.getTotalPages())
                .totalElements(pageResult.getTotalElements())
                .build();
    }

    @Override
    public TopicResponse findById(Integer id) {
        return this.topicRepository.findById(id)
                .map(topicMapper::toTopicResponse)
                .orElse(new TopicResponse());
    }

//    @Override
//    public PageResponse<TopicResponse> findAll(int page, int size) {
//        var pageResult = this.topicRepository.findAll(PageRequest.of(page, size));
//        return PageResponse.<TopicResponse>builder()
//                .content(
//                        pageResult.getContent()
//                                .stream()
//                                .map(topicMapper::toTopicResponse)
//                                .toList()
//                )
//                .totalPages(pageResult.getTotalPages())
//                .build();
//    }

    @Override
    public List<TopicResponse> getAllTopics() {
        List<Topic> topics = topicRepository.findAll();
        return topics.stream()
                .map(topicMapper::toTopicResponse)
                .collect(Collectors.toList());
    }


    @Override
    public void deleteById(Integer id) {
        this.topicRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return topicRepository.existsById(id);
    }






 @Override
    public PageResponse<TopicResponse> findByTitleContaining(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Topic> topicPage = topicRepository.findByTitleContaining(keyword, pageable);
        return mapToPageResponse(topicPage);
    }
    @Override
    public List<TopicResponse> findTopicsByTitleContaining(String keyword) {
        List<Topic> topics = topicRepository.findByTitleContaining(keyword);
        return topics.stream()
                .map(topicMapper::toTopicResponse)
                .collect(Collectors.toList());
    }
    @Override
    public PageResponse<TopicResponse> getLatestTopicsForCandidates(int page, int size) {
        try {
            // Fetch the latest added courses
            Page<Topic> topicPage = topicRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size));

            // Map the courses to CourseResponse objects
            List<TopicResponse> topicResponses = topicPage.getContent().stream()
                    .map(topicMapper::toTopicResponse)
                    .collect(Collectors.toList());

            // Return the page response object
            return PageResponse.<TopicResponse>builder()
                    .content(topicResponses)
                    .totalPages(topicPage.getTotalPages())
                    .totalElements(topicPage.getTotalElements())
                    .build();
        } catch (Exception ex) {
            throw new RuntimeException("Error occurred while fetching latest topics for candidates: " + ex.getMessage(), ex);
        }
    }
    @Override
    public List<TopicResponse> getListOfTopics() {
        List<Topic> topics = topicRepository.findAll();
        return topics.stream()
                .map(topicMapper::toTopicResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllTopicTitles() {
        List<Topic> topics = topicRepository.findAll();
        return topics.stream()
                .map(Topic::getTitle)
                .collect(Collectors.toList());
    }

    private PageResponse<TopicResponse> mapToPageResponse(Page<Topic> topicPage) {
        List<TopicResponse> topicResponses = topicPage.getContent().stream()
                .map(topicMapper::toTopicResponse)
                .collect(Collectors.toList());
        return new PageResponse<>(topicResponses, topicPage.getTotalPages(), topicPage.getTotalElements());
    }

}
