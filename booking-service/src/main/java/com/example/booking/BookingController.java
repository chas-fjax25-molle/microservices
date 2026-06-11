package com.example.booking;

import com.example.common.dto.EventRegistrationDTO;
import com.example.common.dto.EventResponseDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/booking-service/bookings")
@RestController
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping()
    public EventResponseDTO createEvent(@RequestBody EventRegistrationDTO registration) {
        return bookingService.create(registration);
    }

}
