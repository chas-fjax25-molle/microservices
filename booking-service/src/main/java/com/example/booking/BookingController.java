package com.example.booking;

import com.example.common.dto.EventRegistrationDTO;
import com.example.common.dto.EventResponseDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RequestMapping("/api/booking-service/bookings")
@RestController
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponseDTO createEvent(@RequestBody EventRegistrationDTO registration) {
        return bookingService.create(registration);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.FOUND)
    public EventResponseDTO getEventById(@PathVariable UUID id) {
        return bookingService.getById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.FOUND)
    public List<EventResponseDTO> getEvents() {
        return bookingService.getAllEvents();
    }
    
    

}
