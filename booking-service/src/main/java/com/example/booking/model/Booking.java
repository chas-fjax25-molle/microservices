package com.example.booking.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @NotBlank
    private String bookingId;
    @NotBlank
    private String userId;

    
    public Booking() {
    }


    public Booking(String id, @NotBlank String bookingId, @NotBlank String userId) {
        this.id = id;
        this.bookingId = bookingId;
        this.userId = userId;
    }


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getBookingId() {
        return bookingId;
    }


    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }


    public String getUserId() {
        return userId;
    }


    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    
}
