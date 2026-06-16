package com.example.booking.feature.booking;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.booking.feature.booking.model.Booking;
import com.example.booking.feature.event.EventService;
import com.example.common.dto.BookingRegistrarationDTO;
import com.example.common.dto.BookingResponseDTO;

@Service
public class BookingService {

    private final EventService eventService;

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository, EventService eventService) {
        this.bookingRepository = bookingRepository;
        this.eventService = eventService;
    }

    public BookingResponseDTO createBooking(BookingRegistrarationDTO booking) {
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

    public BookingResponseDTO update(UUID id, BookingRegistrarationDTO update) {
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
        return bookingRepository.findAllByUserId(userId).stream().map(this::toDto).toList();
    }

    // Helpermethods

    private Booking toBooking(BookingRegistrarationDTO bookingDTO) {
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

    private Booking updateBooking(Booking booking, BookingRegistrarationDTO update) {
        booking.setEventId(update.eventId());
        booking.setUserId(update.userId());
        return booking;
    }
}
