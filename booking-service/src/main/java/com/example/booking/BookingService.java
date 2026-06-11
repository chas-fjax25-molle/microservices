package com.example.booking;

import com.example.booking.model.Event;
import com.example.common.dto.EventRegistrationDTO;
import com.example.common.dto.EventResponseDTO;
import org.springframework.stereotype.Service;

@Service
public class BookingService {
    private final EventRespository eventRepository;

    public BookingService(EventRespository eventRepository) {
        this.eventRepository = eventRepository;
    }
     
    public EventResponseDTO create(EventRegistrationDTO registration){
        Event event = new Event();
        event.setName(registration.name());
        event.setCapacity(registration.capacity());
        event.setTime(registration.time());
        event.setDescription(registration.description());
        event.setPlace(registration.place());
        
        eventRepository.save(event);
         return new EventResponseDTO(
             event.getId(),
             event.getName(),
             event.getDescription(),
             event.getTime(),
             event.getPlace(),
             event.getCapacity(),
             event.getCapacity()
         );
        
    }
    
}
  