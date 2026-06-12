package com.example.booking;

import com.example.common.dto.BookingRegistarationDTO;
import com.example.common.dto.BookingResponseDTO;
import com.example.common.dto.EventRegistrationDTO;
import com.example.common.dto.EventResponseDTO;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class EventControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String uri = "/api/booking-service/events";

    private MediaType mt = MediaType.APPLICATION_JSON;

    // ------- Happy Path --------

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

        ResultActions respones = performGetAllEvents().andExpect(status().isOk());
        List<EventResponseDTO> events = getResultDTOList(respones);
        assertEquals(2, events.size());
    }

    @Test
    void shouldUpdateEventSuccessfully() throws Exception {
        ResultActions toUpdateResponse = performPost(getTestEventRegistrationDTO());
        EventResponseDTO toUpdateDTO = getResultDTO(toUpdateResponse);
        EventRegistrationDTO update = getAlternetTestEventRegistrationDTO();

        ResultActions response = performPut(toUpdateDTO.id(), update).andExpect(status().isOk());
        EventResponseDTO updated = getResultDTO(response);
        assertEquals(update.name(), updated.name());
    }

    @Test
    void shouldDeleteEventSuccessfully() throws Exception {

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

    private EventRegistrationDTO getAlternetTestEventRegistrationDTO() {
        return new EventRegistrationDTO(
                "alternet",
                "alternating",
                LocalDateTime.now(),
                "there",
                1);
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

    private ResultActions performPut(UUID id, EventRegistrationDTO update) throws Exception {
        return mockMvc.perform(put(uri + "/" + id)
                .contentType(mt)
                .content(objectMapper.writeValueAsString(update)));
    }

    private ResultActions performDelete(UUID id) throws Exception {
        return mockMvc.perform(delete(uri + "/" + id));
    }

    // ---- Read response DTO from result ----

    private EventResponseDTO getResultDTO(ResultActions resultActions) throws Exception {
        String responseContent = resultActions
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(responseContent, EventResponseDTO.class);
    }

    private List<EventResponseDTO> getResultDTOList(ResultActions resultActions) throws Exception {
        String responseContent = resultActions
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(
                responseContent,
                new TypeReference<List<EventResponseDTO>>() {
                });
    }

}
