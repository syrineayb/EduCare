package com.pfe.elearning.resource.dto;

import com.pfe.elearning.lesson.entity.Lesson;
import com.pfe.elearning.resource.entity.Resourcee;
import org.springframework.stereotype.Service;

@Service
public class ResourceMapper {
    public ResourceResponse toResourceResponse(Resourcee resource) {
        return ResourceResponse.builder()
                .lessonTitle(resource.getLesson().getTitle())
                .id(resource.getId())
                .pdfFile(resource.getPdfFile())
                .imageFile(resource.getImageFile())
                .videoFile(resource.getVideoFile())

                .build();
    }

    public Resourcee toResource(ResourceRequest resourceRequest, Lesson lesson) {
        if (resourceRequest == null) {
            throw new NullPointerException("The resource request should not be null");
        }

        Resourcee resource = new Resourcee();
        // Set other properties of the resource if needed
        // Set the lesson for the resource
        resource.setLesson(lesson);

        return resource;
    }
}