package com.pfe.elearning.topic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfe.elearning.common.PageResponse;
import com.pfe.elearning.file.services.Impl.TopicImageServiceImpl;
import com.pfe.elearning.topic.dto.TopicResponse;
import com.pfe.elearning.topic.service.TopicService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TopicControllerTest {
    private MockMvc mockMvc;


    @Mock
    private TopicImageServiceImpl topicImageService;
    @Mock
    private TopicService topicService;
    @InjectMocks
    private TopicController controller;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testGetLatestTopicsForCandidates() throws Exception {
        // Arrange
        int page = 0;
        int size = 10;
        List<TopicResponse> expectedTopicResponses = new ArrayList<>();
        expectedTopicResponses.add(new TopicResponse());
        expectedTopicResponses.add(new TopicResponse());
        PageResponse<TopicResponse> expectedPageResponse = PageResponse.<TopicResponse>builder()
                .content(expectedTopicResponses)
                .totalPages(2)
                .totalElements(20L)
                .build();

        when(topicService.getLatestTopicsForCandidates(page, size)).thenReturn(expectedPageResponse);

        // Act
        mockMvc.perform(MockMvcRequestBuilders.get("/api/topics/latest-topics")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    PageResponse<TopicResponse> response = new ObjectMapper().readValue(result.getResponse().getContentAsString(), PageResponse.class);
                    assertNotNull(response);
                    assertEquals(expectedPageResponse.getContent().size(), response.getContent().size());
                    assertEquals(expectedPageResponse.getTotalPages(), response.getTotalPages());
                    assertEquals(expectedPageResponse.getTotalElements(), response.getTotalElements());
                });
    }
}