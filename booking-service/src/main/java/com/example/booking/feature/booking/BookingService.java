package com.example.booking.feature.booking;

import java.util.List;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.booking.feature.booking.model.Booking;
import com.example.booking.feature.event.EventService;
import com.example.common.dto.BookingRegistrationDTO;
import com.example.common.dto.BookingResponseDTO;

@Service
public class BookingService {

    private final EventService eventService;

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository, EventService eventService) {
        this.bookingRepository = bookingRepository;
        this.eventService = eventService;
    }

    public BookingResponseDTO createBooking(BookingRegistrationDTO booking) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UUID userId = UUID.fromString(username);

        Booking newBooking = new Booking();
        newBooking.setEventId(booking.eventId());
        newBooking.setUserId(userId);
        bookingRepository.save(newBooking);
        return toDto(newBooking);
    }

    public BookingResponseDTO getBookingById(UUID id) {
        Booking booking = bookingRepository.findById(id).orElseThrow();
        validateOwnership(booking.getUserId());
        return toDto(booking);
    }

    public List<BookingResponseDTO> getAllBookings() {
        List<Booking> events = bookingRepository.findAll();
        return events.stream().map(this::toDto).toList();
    }

    public BookingResponseDTO update(UUID id, BookingRegistrationDTO update) {
        Booking booking = bookingRepository.findById(id).orElseThrow();
        validateOwnership(booking.getUserId());
        booking = updateBooking(booking, update);
        bookingRepository.save(booking);
        return toDto(booking);
    }

    public void delete(UUID id) {
        Booking booking = bookingRepository.findById(id).orElseThrow();
        validateOwnership(booking.getUserId());
        bookingRepository.deleteById(id);
    }

    public List<BookingResponseDTO> getBookingsByUserId(UUID userId) {
        UUID currentUserId = getCurrentUserId();
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!currentUserId.equals(userId) && !isAdmin) {
            throw new AccessDeniedException("You can only view your own bookings");
        }
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
                booking.getEventId());
    }

    private Booking updateBooking(Booking booking, BookingRegistrationDTO update) {
        booking.setEventId(update.eventId());
        return booking;
    }

    private void validateOwnership(UUID bookingUserId) {
        UUID currentUserId = getCurrentUserId();
        if (!bookingUserId.equals(currentUserId)) {
            throw new AccessDeniedException("You can only access your own bookings");
        }
    }

    private UUID getCurrentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return UUID.fromString(username);
    }
}
