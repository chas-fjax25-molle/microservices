package com.example.booking.feature.event;

import com.example.booking.client.UserClient;
import com.example.common.dto.EventRegistrationDTO;
import com.example.common.dto.EventResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RequestMapping("/booking-service/events")
@RestController
public class EventController {
    private final EventService eventService;
    private final UserClient userClient;

    public EventController(EventService eventService, UserClient userClient) {
        this.eventService = eventService;
        this.userClient = userClient;
    }

    @Operation(summary = "Create a new event", description = "Create a new event. Requires ADMIN role only.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Event created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN can create events")
    })
    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponseDTO createEvent(@RequestBody @Valid EventRegistrationDTO registration) {
        return eventService.create(registration);
    }

    @Operation(summary = "Get event by ID", description = "Retrieve an event by its ID. Publicly accessible - all users can view event details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid event ID"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventResponseDTO getEventById(@PathVariable @NotNull UUID id) {
        return eventService.getById(id);
    }

    @Operation(summary = "Get all events", description = "Retrieve a list of all events. Publicly accessible - all users can browse available events.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Events retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Events not found")
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventResponseDTO> getEvents() {
        return eventService.getAllEvents();
    }

    @Operation(summary = "Update an event", description = "Update an existing event by its ID. Requires ADMIN role only.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid event ID or input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN can update events"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public EventResponseDTO updateEvent(@PathVariable @NotNull UUID id,
            @RequestBody @Valid EventRegistrationDTO update) {
        return eventService.update(id, update);
    }

    @Operation(summary = "Delete an event", description = "Delete an existing event by its ID. Requires ADMIN role only.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Event deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid event ID"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN can delete events"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEventById(@PathVariable @NotNull UUID id) {
        eventService.delete(id);
    }
}
