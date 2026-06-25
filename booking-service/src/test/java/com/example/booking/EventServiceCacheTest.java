package com.example.booking;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.bean.override.mockito.*;

import com.example.booking.common.security.JwtAuthenticationFilter;
import com.example.booking.feature.event.EventRepository;
import com.example.booking.feature.event.EventService;
import com.example.booking.feature.event.model.Event;
import com.example.common.dto.EventRegistrationDTO;
import com.example.common.security.JwtUtil;

@SpringBootTest
public class EventServiceCacheTest {

    @Autowired
    private EventService eventService;

    @Autowired
    private CacheManager cacheManager;

    @MockitoBean
    private EventRepository eventRepository;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void clearCache() {
        cacheManager.getCache("events").clear();
        cacheManager.getCache("event").clear();
    }

    @Test
    void getAllEvents_shouldUseCacheOnSecondCall() {
        Event event = createEvent();

        when(eventRepository.findAll()).thenReturn(List.of(event));

        eventService.getAllEvents();
        eventService.getAllEvents();

        verify(eventRepository, times(1)).findAll();
    }

    @Test
    void getById_shouldUseCacheOnSecondCall() {
        UUID eventId = UUID.randomUUID();
        Event event = createEventWithId(eventId);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        eventService.getById(eventId);
        eventService.getById(eventId);

        verify(eventRepository, times(1)).findById(eventId);
    }

    @Test
    void create_shouldEvictCache() {
        Event event = createEvent();
        EventRegistrationDTO dto = new EventRegistrationDTO(
            "Concert", "Nice event yeah", LocalDateTime.now().plusDays(1), "Malmö", 100
        );

        when(eventRepository.findAll()).thenReturn(List.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        eventService.getAllEvents();
        eventService.create(dto);
        eventService.getAllEvents();

        verify(eventRepository, times(2)).findAll();
    }

    private Event createEvent() {
        return new Event(
            UUID.randomUUID(),
            "Concert",
            "Nice event yeah",
            LocalDateTime.now().plusDays(1),
            "Malmö",
            100
        );
    }

    private Event createEventWithId(UUID id) {
        return new Event(
            id,
            "Concert",
            "Nice event yeah",
            LocalDateTime.now().plusDays(1),
            "Malmö",
            100
        );
    }
}