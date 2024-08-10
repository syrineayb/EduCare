package com.pfe.elearning.forum.controller.answer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfe.elearning.forum.answer.AnswerController;
import com.pfe.elearning.forum.answer.AnswerRequest;
import com.pfe.elearning.forum.answer.AnswerResponse;
import com.pfe.elearning.forum.answer.AnswerService;
import com.pfe.elearning.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AnswerControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private AnswerService answerService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AnswerController answerController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(answerController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testUpdateAnswer() throws Exception {
        // Mocking the necessary entities
        AnswerRequest answerRequest = new AnswerRequest();
        answerRequest.setMessageContent("Updated answer content");

        AnswerResponse answerResponse = new AnswerResponse();
        answerResponse.setMessageContent("Updated answer content");

        // Mocking the service method call
        when(answerService.updateAnswer(anyInt(), any())).thenReturn(answerResponse);

        // Performing the request with MockMvc
        mockMvc.perform(put("/api/answers/update/{answerId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answerRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(answerResponse)));
    }
}
