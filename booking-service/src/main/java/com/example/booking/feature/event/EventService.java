package com.example.booking.feature.event;

import com.example.booking.feature.event.model.Event;
import com.example.common.dto.EventRegistrationDTO;
import com.example.common.dto.EventResponseDTO;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
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

    public List<EventResponseDTO> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        return events.stream().map(this::toDto).toList();
    }
    
    public EventResponseDTO update(UUID id, EventRegistrationDTO update) {
        Event event = eventRepository.findById(id).orElseThrow();
        event = updateEvent(event, update);
        eventRepository.save(event);
        return toDto(event);
    }

    public void delete(UUID id) {
        eventRepository.findById(id).orElseThrow();
        eventRepository.deleteById(id);
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

    private Event updateEvent(Event event, EventRegistrationDTO update) {
        event.setName(update.name());
        event.setCapacity(update.capacity());
        event.setTime(update.time());
        event.setDescription(update.description());
        event.setPlace(update.place());
        return event;
    }
}
  