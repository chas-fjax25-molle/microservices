package com.example.booking;

import com.example.common.dto.BookingRegistrationDTO;
import com.example.common.dto.EventRegistrationDTO;
import com.example.common.dto.EventResponseDTO;
import com.example.common.security.JwtUtil;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class EventControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtUtil jwtUtil;

    private String uri = "/api/booking-service/events";

    private MediaType mt = MediaType.APPLICATION_JSON;

    @BeforeEach
    void setUp() {
        when(jwtUtil.validateToken(anyString())).thenReturn(true);
        when(jwtUtil.getUsernameFromToken(anyString())).thenReturn("admin");
        when(jwtUtil.getIdFromToken(anyString())).thenReturn(UUID.randomUUID());
        when(jwtUtil.getRoleFromToken(anyString())).thenReturn(java.util.Optional.of("ADMIN"));
    }

    // ------- Happy Path --------

    @Test
    void shouldCreateEventSuccessfully() throws Exception {
        performPost(getTestEventRegistrationDTO()).andExpect(status().isCreated());
    }

    @Test
    void shouldGetEventByIdSuccessfully() throws Exception {
        ResultActions response = performPost(getTestEventRegistrationDTO());
        EventResponseDTO eventResponseDTO = getResultDTO(response);
        
        performGetById(eventResponseDTO.id()).andExpect(status().isOk());
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
        ResultActions response = performPost(getTestEventRegistrationDTO());
        EventResponseDTO eventResponseDTO = getResultDTO(response);

        performDelete(eventResponseDTO.id()).andExpect(status().isNoContent());
    }

    // ---- Unhappy Path ----

    // ---- 400 Bad Request ----

    @Test
    void shouldReturnBadRequestWhenNameIsEmpty() throws Exception {
        performPost(getBadTestEventRegistrationDTO()).andExpect(status().isBadRequest());
    }

    // @Test
    // void shouldReturnBadRequestWhenMalformedJson() throws Exception {
    //     mockMvc.perform(post(uri)
    //             .contentType(mt)
    //             .content("{invalid json}"))
    //             .andExpect(status().isBadRequest());
    // }

    @Test
    void shouldReturnBadRequestWhenEventDateIsInPast() throws Exception {
        EventRegistrationDTO pastEvent = new EventRegistrationDTO(
                "test",
                "testing",
                LocalDateTime.now().minusDays(1), // Past date
                "here",
                10);
        performPost(pastEvent).andExpect(status().isBadRequest());
    }

    // @Test
    // void shouldReturnBadRequestWhenPathVariableIsNotUUID() throws Exception {
    //     mockMvc.perform(get(uri + "/not-a-uuid"))
    //             .andExpect(status().isBadRequest());
    // }

    // @Test
    // void shouldReturnBadRequestWhenPathVariableIsInvalidUUIDFormat() throws Exception {
    //     mockMvc.perform(get(uri + "/12345"))
    //             .andExpect(status().isBadRequest());
    // }

    // @Test
    // void shouldReturnBadRequestWhenUpdatingWithInvalidUUIDPathVariable() throws Exception {
    //     BookingRegistrationDTO update = new BookingRegistrationDTO(UUID.randomUUID(), UUID.randomUUID());
    //     mockMvc.perform(put(uri + "/invalid-uuid")
    //             .contentType(mt)
    //             .content(objectMapper.writeValueAsString(update)))
    //             .andExpect(status().isBadRequest());
    // }

    // @Test
    // void shouldReturnBadRequestWhenDeletingWithInvalidUUIDPathVariable() throws Exception {
    //     mockMvc.perform(delete(uri + "/abc-def-ghi"))
    //             .andExpect(status().isBadRequest());
    // }

    // @Test
    // void shouldReturnBadRequestWhenFieldConversionFails() throws Exception {
    //     String malformedJson = "{\"eventId\": \"not-a-uuid\", \"userId\": null}";
    //     mockMvc.perform(post(uri)
    //             .contentType(mt)
    //             .content(malformedJson))
    //             .andExpect(status().isBadRequest());
    // }

    // ---- 404 Not Found ----

    @Test
    void shouldReturnNotFoundWhenGettingNonexistentEvent() throws Exception {
        UUID fakeId = UUID.randomUUID();
        performGetById(fakeId).andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonexistentEvent() throws Exception {
        UUID fakeId = UUID.randomUUID();
        EventRegistrationDTO update = getTestEventRegistrationDTO();
        performPut(fakeId, update).andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonexistentEvent() throws Exception {
        UUID fakeId = UUID.randomUUID();
        performDelete(fakeId).andExpect(status().isNotFound());
    }

    // ---- 403 Forbidden (if using auth) ----

    // @Test
    // @WithMockUser(roles = "USER") // Requires Spring Security Test
    // void shouldReturnForbiddenWhenUserLacksPermission() throws Exception {
    //     ResultActions response = performPost(getTestEventRegistrationDTO());
    //     EventResponseDTO eventResponseDTO = getResultDTO(response);

    //     // Assuming your controller has auth checks
    //     performDelete(eventResponseDTO.id()).andExpect(status().isForbidden());
    // }

    // ---- 406 Not Acceptable ----

    // @Test
    // void shouldReturnNotAcceptableWhenAcceptHeaderNotSupported() throws Exception {
    //     mockMvc.perform(get(uri)
    //             .accept("application/xml"))
    //             .andExpect(status().isNotAcceptable());
    // }

    // ---- TestDto ----

    private EventRegistrationDTO getTestEventRegistrationDTO() {
        return new EventRegistrationDTO(
                "test",
                "testing",
                LocalDateTime.now().plusDays(1),
                "here",
                10);
    }

    private EventRegistrationDTO getAlternetTestEventRegistrationDTO() {
        return new EventRegistrationDTO(
                "alternet",
                "alternating",
                LocalDateTime.now().plusDays(2),
                "there",
                1);
    }

    private EventRegistrationDTO getBadTestEventRegistrationDTO() {
        return new EventRegistrationDTO(
                "",
                "",
                LocalDateTime.now().plusDays(2),
                "there",
                1);
    }

    // ---- Controller calls -----

    private ResultActions performPost(EventRegistrationDTO registration) throws Exception {
        return mockMvc.perform(post(uri)
                .header("Authorization", "Bearer dummyToken")
                .contentType(mt)
                .content(objectMapper.writeValueAsString(registration)));
    }

    private ResultActions performGetById(UUID id) throws Exception {
        return mockMvc.perform(get(uri + "/" + id)
                .header("Authorization", "Bearer dummyToken"));
    }

    private ResultActions performGetAllEvents() throws Exception {
        return mockMvc.perform(get(uri)
                .header("Authorization", "Bearer dummyToken"));
    }

    private ResultActions performPut(UUID id, EventRegistrationDTO update) throws Exception {
        return mockMvc.perform(put(uri + "/" + id)
                .header("Authorization", "Bearer dummyToken")
                .contentType(mt)
                .content(objectMapper.writeValueAsString(update)));
    }

    private ResultActions performDelete(UUID id) throws Exception {
        return mockMvc.perform(delete(uri + "/" + id)
                .header("Authorization", "Bearer dummyToken"));
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
