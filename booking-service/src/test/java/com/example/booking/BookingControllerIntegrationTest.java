package com.example.booking;

import com.example.common.dto.EventRegistrationDTO;

import tools.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    public EventRegistrationDTO getTestEventRegistrationDTO() {
        return new EventRegistrationDTO(
                "test",
                "testing",
                LocalDateTime.now(),
                "here",
                10);
    }

    @Test
    void shouldCreateEventSuccessfully() throws Exception {
        EventRegistrationDTO registration = getTestEventRegistrationDTO();

        mockMvc.perform(post("/api/booking-service/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registration)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    void shouldReturnBadRequestForInvalidInput() throws Exception {
        mockMvc.perform(post("/api/booking-service/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
