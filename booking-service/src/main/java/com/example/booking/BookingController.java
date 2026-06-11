package com.example.booking;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

}
