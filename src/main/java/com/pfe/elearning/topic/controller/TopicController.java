package com.pfe.elearning.topic.controller;

import com.pfe.elearning.common.PageResponse;
import com.pfe.elearning.course.dto.CourseResponse;
import com.pfe.elearning.exception.TopicNotFoundException;
import com.pfe.elearning.exception.TopicValidationException;
import com.pfe.elearning.file.services.FileService;
import com.pfe.elearning.topic.dto.TopicRequest;
import com.pfe.elearning.topic.dto.TopicResponse;
import com.pfe.elearning.topic.service.TopicService;
import com.pfe.elearning.user.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
public class TopicController {
    private final TopicService topicService;
    @Autowired
    @Qualifier("TopicImageServiceImpl")
    private   FileService fileService;

    @GetMapping("/latest-topics")
    public ResponseEntity<PageResponse<TopicResponse>> getLatestTopicsForCandidates(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<TopicResponse> latestCourses = topicService.getLatestTopicsForCandidates(page, size);
        return ResponseEntity.ok(latestCourses);
    }



    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // Specify consumes for multipart requests
    public ResponseEntity<TopicResponse> save(@Valid TopicRequest topicRequest) {
            String fileImageDownloadUrl = null; // Declare fileImageDownloadUrl here

            if (topicRequest.getImageFile() != null) {
                String filename = fileService.save(topicRequest.getImageFile());
                fileImageDownloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/api/topics/downloadTopicImage/")
                        .path(filename) // Use filename here instead of imageUrl
                        .toUriString();
            }

            TopicResponse createdTopic = topicService.createTopic(topicRequest, fileImageDownloadUrl);

            return ResponseEntity.accepted().body(createdTopic);
        }

    @PutMapping("/{topicId}")
    public ResponseEntity<?> update(
            @PathVariable("topicId") Integer topicId,
            @Valid @ModelAttribute TopicRequest topicRequest) {
        try {
            String imageUrl = null; // Initialize imageUrl

            // If a new image file is provided, save it and get the image URL
            if (topicRequest.getImageFile() != null) {
                String filename = fileService.save(topicRequest.getImageFile());
                imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/api/topics/downloadTopicImage/")
                        .path(filename)
                        .toUriString();
            }

            // Update the topic with the provided image URL
            TopicResponse updatedTopic = topicService.update(topicId, topicRequest, imageUrl);

            // Set the complete image file URL in the response
            updatedTopic.setImageFile(imageUrl);

            return ResponseEntity.accepted().body(updatedTopic);
        } catch (TopicNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (TopicValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }



    @GetMapping("/downloadTopicImage/{filename:.+}")
    public ResponseEntity<Resource> downloadTopicImage(@PathVariable String filename) {
        Resource fileResource = fileService.load(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileResource.getFilename() + "\"")
                .body(fileResource);
    }

    @GetMapping("/findTopicsPage")
    public ResponseEntity<PageResponse<TopicResponse>> getAllTopics(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<TopicResponse> topicsPage = topicService.findAll(page, size);
        return ResponseEntity.ok(topicsPage);
    }
    @GetMapping("/{topicId}")
    public ResponseEntity<TopicResponse> findById(@PathVariable("topicId") Integer topicId) {
        TopicResponse topicResponse = topicService.findById(topicId);
        if (topicResponse != null && topicResponse.getId() != null) {
            return ResponseEntity.ok(topicResponse);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/findAll")
    public ResponseEntity<PageResponse<TopicResponse>> findAll(
            @RequestParam(name = "page", defaultValue = "1", required = false) int page,
            @RequestParam(name = "size", defaultValue = "3", required = false) int size) {        return ResponseEntity.ok(topicService.findAll(page, size));
    }


    //OK
    @GetMapping("")
    public ResponseEntity<PageResponse<TopicResponse>> getTopicsInBatch(@RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "4") int size) {
        PageResponse<TopicResponse> topicsPage = topicService.findTopicsInBatch(page, size);
        return ResponseEntity.ok(topicsPage);
    }


    @DeleteMapping("/{topicId}")
    public ResponseEntity<String> deleteById(@PathVariable("topicId") Integer topicId) {
        if (topicService.existsById(topicId)) {
            topicService.deleteById(topicId);
            return ResponseEntity.noContent().build(); // Return 204 for successful deletion
        } else {
            throw new TopicNotFoundException(topicId);
        }
    }


    /*
      @GetMapping("/search")
      public ResponseEntity<PageResponse<TopicResponse>> findByTitleContaining(
              @RequestParam(name = "keyword") String keyword,
              @RequestParam(name = "page", defaultValue = "0", required = false) int page,
              @RequestParam(name = "size", defaultValue = "20", required = false) int size) {
          return ResponseEntity.ok(topicService.findByTitleContaining(keyword, page, size));
      }
   */
    @GetMapping("/search")
    @PreAuthorize("permitAll()")
    public ResponseEntity<PageResponse<TopicResponse>> findByTitleContaining(
            @RequestParam(name = "keyword") String keyword,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "20", required = false) int size) {
        return ResponseEntity.ok(topicService.findByTitleContaining(keyword, page, size));
    }

    @GetMapping("/search-by-title")
    public List<TopicResponse> findTopicsByTitleContaining(@RequestParam("keyword") String keyword) {
        return topicService.findTopicsByTitleContaining(keyword);
    }


    @GetMapping("/titles")
    public List<String> getAllTopicTitles() {
        return topicService.getAllTopicTitles();
    }

    @GetMapping("/all-with-id-and-title")
    public ResponseEntity<List<TopicResponse>> getAllTopicResponses() {
        List<TopicResponse> topicResponses = topicService.getListOfTopics();
        return ResponseEntity.ok(topicResponses);
    }
}