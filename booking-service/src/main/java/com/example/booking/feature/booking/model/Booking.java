package com.example.booking.feature.booking.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID eventId;
    private UUID userId;

    
    public Booking() {
    }


    public Booking(UUID id, @NotBlank UUID eventId, @NotBlank UUID userId) {
        this.id = id;
        this.eventId = eventId;
        this.userId = userId;
    }


    public UUID getId() {
        return id;
    }


    public void setId(UUID id) {
        this.id = id;
    }


    public UUID getEventId() {
        return eventId;
    }


    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }


    public UUID getUserId() {
        return userId;
    }


    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    
    
}
