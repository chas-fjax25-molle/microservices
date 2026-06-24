package com.example.booking.feature.booking;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.booking.client.UserClient;
import com.example.booking.feature.booking.model.Booking;
import com.example.booking.feature.event.EventService;
import com.example.common.dto.BookingRegistrationDTO;
import com.example.common.dto.BookingResponseDTO;
import com.example.common.dto.UserResponseDTO;

@Service
public class BookingService {

    private final EventService eventService;

    private final BookingRepository bookingRepository;

    private final UserClient userClient;

    public BookingService(BookingRepository bookingRepository, EventService eventService, UserClient userClient) {
        this.bookingRepository = bookingRepository;
        this.eventService = eventService;
        this.userClient = userClient;
    }

    public BookingResponseDTO createBooking(BookingRegistrationDTO booking) {
        validateUser(booking.userId());
        eventService.getById(booking.eventId());
        Booking savedBooking = bookingRepository.save(toBooking(booking));
        return toDto(savedBooking);
    }

    public BookingResponseDTO getBookingById(UUID id) {
        Booking booking = bookingRepository.findById(id).orElseThrow();
        return toDto(booking);
    }

    public List<BookingResponseDTO> getAllBookings() {
        List<Booking> events = bookingRepository.findAll();
        return events.stream().map(this::toDto).toList();
    }

    public BookingResponseDTO update(UUID id, BookingRegistrationDTO update) {
        validateUser(update.userId());
        Booking booking = bookingRepository.findById(id).orElseThrow();
        booking = updateBooking(booking, update);
        bookingRepository.save(booking);
        return toDto(booking);
    }

    public void delete(UUID id) {
        bookingRepository.findById(id).orElseThrow();
        bookingRepository.deleteById(id);
    }

    public List<BookingResponseDTO> getBookingsByUserId(UUID userId) {
        validateUser(userId);
        return bookingRepository.findAllByUserId(userId).stream().map(this::toDto).toList();
    }

    // Helpermethods

    private Booking toBooking(BookingRegistrationDTO bookingDTO) {
        Booking booking = new Booking();
        booking.setEventId(bookingDTO.eventId());
        booking.setUserId(bookingDTO.userId());
        return booking;
    }

    private BookingResponseDTO toDto(Booking booking) {
        return new BookingResponseDTO(
                booking.getId(),
                booking.getEventId(),
                booking.getUserId());
    }

    private Booking updateBooking(Booking booking, BookingRegistrationDTO update) {
        booking.setEventId(update.eventId());
        booking.setUserId(update.userId());
        return booking;
    }

    private void validateUser(UUID id) {
        ResponseEntity<UserResponseDTO> user = userClient.getUser(id);
        HttpStatusCode status = user.getStatusCode();
        System.out.println(status);
        if (status != HttpStatusCode.valueOf(200)) {
            throw new NoSuchElementException("User not found with ID: " + id);
        }
    }
}
