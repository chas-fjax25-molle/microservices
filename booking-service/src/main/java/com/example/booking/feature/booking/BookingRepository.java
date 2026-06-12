package com.example.booking.feature.booking;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.booking.feature.booking.model.Booking;

public interface BookingRepository extends JpaRepository<Booking, UUID>{
    
}
