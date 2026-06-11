package com.example.booking;

import org.springframework.stereotype.Service;

@Service
public class BookingService {
    private final EventRespository eventRepository;

    public BookingService(EventRespository eventRepository) {
        this.eventRepository = eventRepository;
    }

    
    
}
