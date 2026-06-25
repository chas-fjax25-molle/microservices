package com.example.booking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.example.booking.client.UserClient;
import com.example.common.dto.BookingRegistrationDTO;
import com.example.common.dto.BookingResponseDTO;
import com.example.common.dto.EventRegistrationDTO;
import com.example.common.dto.EventResponseDTO;
import com.example.common.security.JwtUtil;
import com.example.common.dto.UserResponseDTO;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserClient userClient;

    private String uri = "/api/booking-service/bookings";

    private MediaType mt = MediaType.APPLICATION_JSON;

    @BeforeEach
    void setUp() {
        // Configure the mock JwtUtil to allow all token validations
        when(jwtUtil.validateToken(anyString())).thenReturn(true);
        UUID mockUserId = UUID.randomUUID();
        when(jwtUtil.getIdFromToken(anyString())).thenReturn(mockUserId);
        when(jwtUtil.getUsernameFromToken(anyString())).thenReturn(mockUserId.toString());
        when(jwtUtil.getRoleFromToken(anyString())).thenReturn(java.util.Optional.of("ADMIN"));
        Mockito.when(userClient.getUser(any())).thenReturn(getTestUserResponseDTO());
    }

    // ---- Happy path ----

    @Test
    void shouldCreateBookingWithExistingEventAndValidUserIdFormatSuccessfully() throws Exception {
        EventResponseDTO eventResponseDto = postTestEventRegistrationDTO();
        BookingRegistrationDTO bookingRegistrationDto = getTestBookingRegistrationDTO(eventResponseDto.id());

        ResultActions bookingResult = performPost(bookingRegistrationDto).andExpect(status().isCreated());
        BookingResponseDTO bookingResponse = getResultDTO(bookingResult);
        assertEquals(bookingResponse.eventId(), bookingRegistrationDto.eventId());
    }

    @Test
    void shouldFindBookingByIdSuccessfully() throws Exception {
        EventResponseDTO eventResponseDto = postTestEventRegistrationDTO();
        BookingRegistrationDTO bookingRegistrationDto = getTestBookingRegistrationDTO(eventResponseDto.id());
        ResultActions postedBookingResult = performPost(bookingRegistrationDto);
        BookingResponseDTO postedBookingResponse = getResultDTO(postedBookingResult);

        ResultActions foundBookingResult = performGetById(postedBookingResponse.id()).andExpect(status().isOk());
        BookingResponseDTO foundBookingResponse = getResultDTO(foundBookingResult);
        assertEquals(postedBookingResponse.id(), foundBookingResponse.id());
    }

    @Test
    void shouldFindAllBookings() throws Exception {
        EventResponseDTO eventResponseDto = postTestEventRegistrationDTO();
        BookingRegistrationDTO bookingRegistrationDto = getTestBookingRegistrationDTO(eventResponseDto.id());
        performPost(bookingRegistrationDto);
        eventResponseDto = postTestEventRegistrationDTO();
        bookingRegistrationDto = getTestBookingRegistrationDTO(eventResponseDto.id());
        performPost(bookingRegistrationDto);

        ResultActions foundBookingResult = performGetAllBookings().andExpect(status().isOk());
        List<BookingResponseDTO> bookings = getResultDTOList(foundBookingResult);
        assertEquals(2, bookings.size());
    }

    @Test
    void shouldFindTwoOfThreeBookingsByUserId() throws Exception {
        UUID userId = UUID.randomUUID();
        when(jwtUtil.getIdFromToken(anyString())).thenReturn(userId);
        when(jwtUtil.getUsernameFromToken(anyString())).thenReturn(userId.toString());

        EventResponseDTO eventResponseDto1 = postTestEventRegistrationDTO();
        BookingRegistrationDTO bookingRegistrationDto1 = new BookingRegistrationDTO(eventResponseDto1.id(), userId);
        performPost(bookingRegistrationDto1);

        EventResponseDTO eventResponseDto2 = postTestEventRegistrationDTO();
        BookingRegistrationDTO bookingRegistrationDto2 = new BookingRegistrationDTO(eventResponseDto2.id(), userId);
        performPost(bookingRegistrationDto2);

        UUID differentUser = UUID.randomUUID();
        when(jwtUtil.getIdFromToken(anyString())).thenReturn(differentUser);
        when(jwtUtil.getUsernameFromToken(anyString())).thenReturn(differentUser.toString());

        EventResponseDTO eventResponseDto3 = postTestEventRegistrationDTO();
        BookingRegistrationDTO bookingRegistrationDto3 = new BookingRegistrationDTO(eventResponseDto3.id(),
                differentUser);
        performPost(bookingRegistrationDto3);

        when(jwtUtil.getIdFromToken(anyString())).thenReturn(userId);
        when(jwtUtil.getUsernameFromToken(anyString())).thenReturn(userId.toString());

        ResultActions foundBookingResult = performGetAllBookingsByUserId(userId).andExpect(status().isOk());
        List<BookingResponseDTO> bookings = getResultDTOList(foundBookingResult);
        assertEquals(2, bookings.size());
    }

    @Test
    void shouldUpdateBookingSuccessfully() throws Exception {
        EventResponseDTO eventResponseDto = postTestEventRegistrationDTO();
        BookingRegistrationDTO bookingRegistrationDto = getTestBookingRegistrationDTO(eventResponseDto.id());
        ResultActions postedBookingResult = performPost(bookingRegistrationDto);
        BookingResponseDTO postedBookingResponse = getResultDTO(postedBookingResult);

        UUID updatedUuid = UUID.randomUUID();
        BookingRegistrationDTO update = new BookingRegistrationDTO(updatedUuid, updatedUuid);

        ResultActions response = performPut(postedBookingResponse.id(), update).andExpect(status().isOk());
        BookingResponseDTO updated = getResultDTO(response);
        assertEquals(updated.eventId(), update.eventId());
    }

    @Test
    void shouldDeleteBookingSuccessfully() throws Exception {
        EventResponseDTO eventResponseDto = postTestEventRegistrationDTO();
        BookingRegistrationDTO bookingRegistrationDto = getTestBookingRegistrationDTO(eventResponseDto.id());
        ResultActions postedBookingResult = performPost(bookingRegistrationDto);
        BookingResponseDTO postedBookingResponse = getResultDTO(postedBookingResult);

        performDelete(postedBookingResponse.id()).andExpect(status().isNoContent());
    }

    @Test
    void shouldLimitUserToOneBookingPerEvent() throws Exception {
        UUID userId = UUID.randomUUID();
        EventResponseDTO eventResponseDto = postTestEventRegistrationDTO();
        BookingRegistrationDTO bookingRegistrationDto = new BookingRegistrationDTO(eventResponseDto.id(), userId);
        performPost(bookingRegistrationDto);
        performPost(bookingRegistrationDto);

        ResultActions foundBookingResult = performGetAllBookingsByUserId(userId).andExpect(status().isOk());
        List<BookingResponseDTO> bookings = getResultDTOList(foundBookingResult);
        assertEquals(1, bookings.size());
    }

    // ---- Unhappy path ----

    // ---- 400 Bad Request ----

    @Test
    void shouldReturnBadRequestWhenMalformedJson() throws Exception {
        mockMvc.perform(post(uri)
                .header("Authorization", "Bearer dummyToken")
                .contentType(mt)
                .content("{invalid json}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenEventIdIsNull() throws Exception {
        BookingRegistrationDTO badBooking = new BookingRegistrationDTO(null, UUID.randomUUID());
        performPost(badBooking).andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenUserIdIsNull() throws Exception {
        BookingRegistrationDTO badBooking = new BookingRegistrationDTO(UUID.randomUUID(), null);
        performPost(badBooking).andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenPathVariableIsNotUUID() throws Exception {
        mockMvc.perform(get(uri + "/not-a-uuid")
                .header("Authorization", "Bearer dummyToken"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenPathVariableIsInvalidUUIDFormat() throws Exception {
        mockMvc.perform(get(uri + "/12345")
                .header("Authorization", "Bearer dummyToken"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenUpdatingWithInvalidUUIDPathVariable() throws Exception {
        BookingRegistrationDTO update = new BookingRegistrationDTO(UUID.randomUUID(), UUID.randomUUID());
        mockMvc.perform(put(uri + "/invalid-uuid")
                .header("Authorization", "Bearer dummyToken")
                .contentType(mt)
                .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenDeletingWithInvalidUUIDPathVariable() throws Exception {
        mockMvc.perform(delete(uri + "/abc-def-ghi")
                .header("Authorization", "Bearer dummyToken"))
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
        BookingRegistrationDTO update = new BookingRegistrationDTO(UUID.randomUUID(), UUID.randomUUID());
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
        BookingRegistrationDTO bookingWithFakeEvent = new BookingRegistrationDTO(
                nonexistentEventId,
                UUID.randomUUID());
        performPost(bookingWithFakeEvent).andExpect(status().isNotFound());
    }

    // ---- 415 Unsupported Media Type ----

    @Test
    void shouldReturnUnsupportedMediaTypeWhenContentTypeNotSupported() throws Exception {
        mockMvc.perform(post(uri)
                .header("Authorization", "Bearer dummyToken")
                .contentType("application/xml")
                .content("<booking/>"))
                .andExpect(status().isUnsupportedMediaType());
    }

    // ---- TestDto ----

    private BookingRegistrationDTO getTestBookingRegistrationDTO(UUID eventId) {
        return new BookingRegistrationDTO(
                eventId,
                UUID.randomUUID());
    }

    private ResponseEntity<UserResponseDTO> getTestUserResponseDTO() {
        return new ResponseEntity<UserResponseDTO>(
                new UserResponseDTO(UUID.randomUUID(), "John Doe", "john.doe@example.com", "USER"), HttpStatus.OK);
    }

    // ---- Controller calls -----

    private ResultActions performPost(BookingRegistrationDTO registration) throws Exception {
        return mockMvc.perform(post(uri)
                .header("Authorization", "Bearer dummyToken")
                .contentType(mt)
                .content(objectMapper.writeValueAsString(registration)));
    }

    private ResultActions performGetById(UUID id) throws Exception {
        return mockMvc.perform(get(uri + "/" + id)
                .header("Authorization", "Bearer dummyToken"));
    }

    private ResultActions performGetAllBookings() throws Exception {
        return mockMvc.perform(get(uri)
                .header("Authorization", "Bearer dummyToken"));
    }

    private ResultActions performGetAllBookingsByUserId(UUID userId) throws Exception {
        return mockMvc.perform(get(uri + "/user/" + userId)
                .header("Authorization", "Bearer dummyToken"));
    }

    private ResultActions performPut(UUID id, BookingRegistrationDTO update) throws Exception {
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
                .header("Authorization", "Bearer dummyToken")
                .contentType(mt)
                .content(objectMapper.writeValueAsString(registration))).andExpect(status().isCreated());

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
