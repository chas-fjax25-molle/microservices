package com.example.booking;

import com.example.booking.model.Event;
import com.example.common.dto.EventRegistrationDTO;
import com.example.common.dto.EventResponseDTO;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class BookingService {
    private final EventRespository eventRepository;

    public BookingService(EventRespository eventRepository) {
        this.eventRepository = eventRepository;
    }
     
    public EventResponseDTO create(EventRegistrationDTO registration) {
        Event event = toEvent(registration);
        Event createdEvent = eventRepository.save(event);
        return toDto(createdEvent);
    }

    public EventResponseDTO getById(UUID id) {
        return toDto(eventRepository.findById(id).orElseThrow());
    }

    public List<EventResponseDTO> getAllEvents(){
        List<Event> events = eventRepository.findAll();
        return events.stream().map(this::toDto).toList();
    }
    

    // Helpermethods
    
    private Event toEvent(EventRegistrationDTO registration) {
        Event event = new Event();
        event.setName(registration.name());
        event.setCapacity(registration.capacity());
        event.setTime(registration.time());
        event.setDescription(registration.description());
        event.setPlace(registration.place());
        return event;
    }

    private EventResponseDTO toDto(Event event) {
        return new EventResponseDTO(
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getTime(),
                event.getPlace(),
                event.getCapacity(),
                event.getCapacity());
    }
}
  