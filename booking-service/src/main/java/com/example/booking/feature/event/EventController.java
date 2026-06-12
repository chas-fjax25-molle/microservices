package com.example.booking.feature.event;

import com.example.common.dto.EventRegistrationDTO;
import com.example.common.dto.EventResponseDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;



@RequestMapping("/api/booking-service/events")
@RestController
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponseDTO createEvent(@RequestBody EventRegistrationDTO registration) {
        return eventService.create(registration);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.FOUND)
    public EventResponseDTO getEventById(@PathVariable UUID id) {
        return eventService.getById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventResponseDTO> getEvents() {
        return eventService.getAllEvents();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventResponseDTO updateEvent(@PathVariable UUID id, @RequestBody EventRegistrationDTO update) {
        return eventService.update(id, update);
    }
    
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEventById(@PathVariable UUID id) {
        eventService.delete(id);
    }
}
