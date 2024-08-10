package com.pfe.elearning.resource.service;


import com.pfe.elearning.common.PageResponse;
import com.pfe.elearning.resource.dto.ResourceRequest;
import com.pfe.elearning.resource.dto.ResourceResponse;
import com.pfe.elearning.resource.entity.Resourcee;

import java.util.List;

public interface ResourceService {
    long countCompletedResourcesByUserAndLessonId(String candidateUsername, Integer lessonId); // New method to count completed resources by a user

    int getUserLessonProgress(String candidateUsername, Integer lessonId) ;


    void markResourceAsCompleted(Integer resourceId, String candidateUsername);

    Resourcee findById(Integer resourceId);


    ResourceResponse createResource(ResourceRequest request, Integer lessonId,String imageUrl,String vedioUrl,String PdfUrl);

    ResourceResponse updateResource(Integer resourceId, ResourceRequest request, String imageUrl, String pdfUrl, String videoUrl);

    ResourceResponse getResourceById(Integer resourceId);

    void deleteResource(Integer resourceId);

    List<ResourceResponse> getAllResourcesByLessonId(Integer lessonId);
    PageResponse<ResourceResponse> getResourcesByLessonIdWithPagination(int lessonId, int page, int size);

    int countVideosByLessonId(Integer lessonId);

    int countImagesByLessonId(Integer lessonId);

    int countPDFsByLessonId(Integer lessonId);

    PageResponse<ResourceResponse>findAll(int page, int size);

    List<ResourceResponse> getAllResources();

    PageResponse<ResourceResponse> findResourcesInBatch(int page, int size);
}
