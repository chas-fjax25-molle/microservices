package com.example.booking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.example.common.dto.BookingRegistarationDTO;
import com.example.common.dto.BookingResponseDTO;
import com.example.common.dto.EventRegistrationDTO;
import com.example.common.dto.EventResponseDTO;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String uri = "/api/booking-service/bookings";

    private MediaType mt = MediaType.APPLICATION_JSON;

    // ---- Happy path ----

    @Test
    void shouldCreateBookingWithExistingEventAndValidUserIdFormatSuccessfully() throws Exception {
        EventResponseDTO eventResponseDto = postTestEventRegistrationDTO();
        BookingRegistarationDTO bookingRegistrationDto = getTestBookingRegistrationDTO(eventResponseDto.id());

        ResultActions bookingResult = performPost(bookingRegistrationDto).andExpect(status().isCreated());
        BookingResponseDTO bookingResponse = getResultDTO(bookingResult);
        assertEquals(bookingResponse.eventId(), bookingRegistrationDto.eventId());
    }

    @Test
    void shouldFindBookingByIdSuccessfully() throws Exception {
        EventResponseDTO eventResponseDto = postTestEventRegistrationDTO();
        BookingRegistarationDTO bookingRegistrationDto = getTestBookingRegistrationDTO(eventResponseDto.id());
        ResultActions postedBookingResult = performPost(bookingRegistrationDto);
        BookingResponseDTO postedBookingResponse = getResultDTO(postedBookingResult);

        ResultActions foundBookingResult = performGetById(postedBookingResponse.id()).andExpect(status().isOk());
        BookingResponseDTO foundBookingResponse = getResultDTO(foundBookingResult);
        assertEquals(postedBookingResponse.id(), foundBookingResponse.id());
    }

    @Test
    void shouldFindAllEvents() throws Exception {
        EventResponseDTO eventResponseDto = postTestEventRegistrationDTO();
        BookingRegistarationDTO bookingRegistrationDto = getTestBookingRegistrationDTO(eventResponseDto.id());
        performPost(bookingRegistrationDto);
        eventResponseDto = postTestEventRegistrationDTO();
        bookingRegistrationDto = getTestBookingRegistrationDTO(eventResponseDto.id());
        performPost(bookingRegistrationDto);

        ResultActions foundBookingResult = performGetAllBookings().andExpect(status().isOk());
        List<BookingResponseDTO> bookings = getResultDTOList(foundBookingResult);
        assertEquals(2, bookings.size());
    }

    @Test
    void shouldUpdateBookingSuccessfully() throws Exception {
        EventResponseDTO eventResponseDto = postTestEventRegistrationDTO();
        BookingRegistarationDTO bookingRegistrationDto = getTestBookingRegistrationDTO(eventResponseDto.id());
        ResultActions postedBookingResult = performPost(bookingRegistrationDto);
        BookingResponseDTO postedBookingResponse = getResultDTO(postedBookingResult);

        UUID updatedUuid = UUID.randomUUID();
        BookingRegistarationDTO update = new BookingRegistarationDTO(updatedUuid, updatedUuid);

        ResultActions response = performPut(postedBookingResponse.id(), update).andExpect(status().isOk());
        BookingResponseDTO updated = getResultDTO(response);
        assertEquals(updated.eventId(), update.eventId());
    }

    @Test
    void shouldDeleteBookingSuccessfully() throws Exception {
        EventResponseDTO eventResponseDto = postTestEventRegistrationDTO();
        BookingRegistarationDTO bookingRegistrationDto = getTestBookingRegistrationDTO(eventResponseDto.id());
        ResultActions postedBookingResult = performPost(bookingRegistrationDto);
        BookingResponseDTO postedBookingResponse = getResultDTO(postedBookingResult);

        performDelete(postedBookingResponse.id()).andExpect(status().isNoContent());
    }

    // ---- Unhappy path ----

    // ---- 400 Bad Request ----

    @Test
    void shouldReturnBadRequestWhenMalformedJson() throws Exception {
        mockMvc.perform(post(uri)
                .contentType(mt)
                .content("{invalid json}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenEventIdIsNull() throws Exception {
        BookingRegistarationDTO badBooking = new BookingRegistarationDTO(null, UUID.randomUUID());
        performPost(badBooking).andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenUserIdIsNull() throws Exception {
        BookingRegistarationDTO badBooking = new BookingRegistarationDTO(UUID.randomUUID(), null);
        performPost(badBooking).andExpect(status().isBadRequest());
    }

@Test
void shouldReturnBadRequestWhenPathVariableIsNotUUID() throws Exception {
    mockMvc.perform(get(uri + "/not-a-uuid"))
            .andExpect(status().isBadRequest());
}

@Test
void shouldReturnBadRequestWhenPathVariableIsInvalidUUIDFormat() throws Exception {
    mockMvc.perform(get(uri + "/12345"))
            .andExpect(status().isBadRequest());
}

@Test
void shouldReturnBadRequestWhenUpdatingWithInvalidUUIDPathVariable() throws Exception {
    BookingRegistarationDTO update = new BookingRegistarationDTO(UUID.randomUUID(), UUID.randomUUID());
    mockMvc.perform(put(uri + "/invalid-uuid")
            .contentType(mt)
            .content(objectMapper.writeValueAsString(update)))
            .andExpect(status().isBadRequest());
}

@Test
void shouldReturnBadRequestWhenDeletingWithInvalidUUIDPathVariable() throws Exception {
    mockMvc.perform(delete(uri + "/abc-def-ghi"))
            .andExpect(status().isBadRequest());
}




    // ---- 404 Not Found ----

    @Test
    void shouldReturnNotFoundWhenGettingNonexistentBooking() throws Exception {
        UUID fakeId = UUID.randomUUID();
        performGetById(fakeId).andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonexistentBooking() throws Exception {
        UUID fakeId = UUID.randomUUID();
        BookingRegistarationDTO update = new BookingRegistarationDTO(UUID.randomUUID(), UUID.randomUUID());
        performPut(fakeId, update).andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonexistentBooking() throws Exception {
        UUID fakeId = UUID.randomUUID();
        performDelete(fakeId).andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenBookingWithNonexistentEvent() throws Exception {
        UUID nonexistentEventId = UUID.randomUUID();
        BookingRegistarationDTO bookingWithFakeEvent = new BookingRegistarationDTO(
                nonexistentEventId,
                UUID.randomUUID());
        performPost(bookingWithFakeEvent).andExpect(status().isNotFound());
    }

    // ---- 415 Unsupported Media Type ----
    
    @Test
    void shouldReturnUnsupportedMediaTypeWhenContentTypeNotSupported() throws Exception {
        mockMvc.perform(post(uri)
                .contentType("application/xml")
                .content("<booking/>"))
                .andExpect(status().isUnsupportedMediaType());
    }

    // ---- TestDto ----

    private BookingRegistarationDTO getTestBookingRegistrationDTO(UUID eventId) {
        return new BookingRegistarationDTO(
                eventId,
                UUID.randomUUID());
    }

    // ---- Controller calls -----

    private ResultActions performPost(BookingRegistarationDTO registration) throws Exception {
        return mockMvc.perform(post(uri)
                .contentType(mt)
                .content(objectMapper.writeValueAsString(registration)));
    }

    private ResultActions performGetById(UUID id) throws Exception {
        return mockMvc.perform(get(uri + "/" + id));
    }

    private ResultActions performGetAllBookings() throws Exception {
        return mockMvc.perform(get(uri));
    }

    private ResultActions performPut(UUID id, BookingRegistarationDTO update) throws Exception {
        return mockMvc.perform(put(uri + "/" + id)
                .contentType(mt)
                .content(objectMapper.writeValueAsString(update)));
    }

    private ResultActions performDelete(UUID id) throws Exception {
        return mockMvc.perform(delete(uri + "/" + id));
    }

    // ---- Read response DTO from result ----

    private BookingResponseDTO getResultDTO(ResultActions resultActions) throws Exception {
        String responseContent = resultActions
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(responseContent, BookingResponseDTO.class);
    }

    private List<BookingResponseDTO> getResultDTOList(ResultActions resultActions) throws Exception {
        String responseContent = resultActions
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(
                responseContent,
                new TypeReference<List<BookingResponseDTO>>() {
                });
    }

    // ---- Test Event ----

    private EventResponseDTO postTestEventRegistrationDTO() throws Exception {
        EventRegistrationDTO eventRegistrationDTO = new EventRegistrationDTO(
                "test",
                "testing",
                LocalDateTime.now().plusDays(1),
                "here",
                10);

        return performEventPost(eventRegistrationDTO);
    }

    private EventResponseDTO performEventPost(EventRegistrationDTO registration) throws Exception {
        ResultActions resultActions = mockMvc.perform(post("/api/booking-service/events")
                .contentType(mt)
                .content(objectMapper.writeValueAsString(registration)));

        return getResultEventDTO(resultActions);
    }

    private EventResponseDTO getResultEventDTO(ResultActions resultActions) throws Exception {
        String responseContent = resultActions
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(responseContent, EventResponseDTO.class);
    }

}
