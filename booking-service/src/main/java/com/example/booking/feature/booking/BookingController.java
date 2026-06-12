package com.example.booking.feature.booking;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.common.dto.BookingRegistarationDTO;
import com.example.common.dto.BookingResponseDTO;
import com.example.common.dto.EventRegistrationDTO;
import com.example.common.dto.EventResponseDTO;

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
    public BookingResponseDTO postMethodName(@RequestBody BookingRegistarationDTO booking) {
        return bookingService.createBooking(booking);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.FOUND)
    public BookingResponseDTO getBookingById(@PathVariable UUID id) {
        return bookingService.getBookingById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponseDTO> getBookings() {
        return bookingService.getAllBookings();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponseDTO updateBooking(@PathVariable UUID id, @RequestBody BookingRegistarationDTO update) {
        return bookingService.update(id, update);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBookingById(@PathVariable UUID id) {
        bookingService.delete(id);
    }
}
