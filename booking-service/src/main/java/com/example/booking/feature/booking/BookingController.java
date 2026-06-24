package com.example.booking.feature.booking;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.common.dto.BookingRegistrationDTO;
import com.example.common.dto.BookingResponseDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequestMapping("/api/booking-service/bookings")
@RestController
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDTO createBooking(@RequestBody @Valid BookingRegistrationDTO booking) {
        return bookingService.createBooking(booking);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponseDTO getBookingById(@PathVariable @NotNull UUID id) {
        return bookingService.getBookingById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponseDTO> getBookings() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponseDTO> getBookings(@PathVariable @NotNull UUID userId) {
        return bookingService.getBookingsByUserId(userId);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponseDTO updateBooking(@PathVariable @NotNull UUID id,
            @RequestBody @Valid BookingRegistrationDTO update) {
        return bookingService.update(id, update);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBookingById(@PathVariable @NotNull UUID id) {
        bookingService.delete(id);
    }
}
