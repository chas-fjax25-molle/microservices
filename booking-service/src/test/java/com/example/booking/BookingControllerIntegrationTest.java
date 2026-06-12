package com.example.booking;

import com.example.common.dto.EventRegistrationDTO;
import com.example.common.dto.EventResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String uri = "/api/booking-service/bookings";

    private MediaType mt = MediaType.APPLICATION_JSON;

    @BeforeEach
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void setUp() {
    }

    // ------- Happy Path --------

    @Test
    void contextLoads() {
    }

    @Test
    void shouldCreateEventSuccessfully() throws Exception {
        performPost(getTestEventRegistrationDTO()).andExpect(status().isCreated());
    }

    @Test
    void shouldGetEventByIdSuccessfully() throws Exception {
        ResultActions response = performPost(getTestEventRegistrationDTO());
        EventResponseDTO eventResponseDTO = getResultDTO(response);

        performGetById(eventResponseDTO.id()).andExpect(status().isFound());
    }

    @Test
    void shouldGetAllEventsSuccessfully() throws Exception {
        performPost(getTestEventRegistrationDTO());
        performPost(getTestEventRegistrationDTO());

        performGetAllEvents().andExpect(status().isFound());
    }

    // ---- TestDto ----

    private EventRegistrationDTO getTestEventRegistrationDTO() {
        return new EventRegistrationDTO(
                "test",
                "testing",
                LocalDateTime.now(),
                "here",
                10);
    }

    // ---- Controller calls -----

    private ResultActions performPost(EventRegistrationDTO registration) throws Exception {
        return mockMvc.perform(post(uri)
                .contentType(mt)
                .content(objectMapper.writeValueAsString(registration)));
    }

    private ResultActions performGetById(UUID id) throws Exception {
        return mockMvc.perform(get(uri + "/" + id));
    }

    private ResultActions performGetAllEvents() throws Exception {
        return mockMvc.perform(get(uri));
    }

    // ---- Read result content ----

    private EventResponseDTO getResultDTO(ResultActions resultActions) throws Exception {
        String responseContent = resultActions
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(responseContent, EventResponseDTO.class);
    }
}
