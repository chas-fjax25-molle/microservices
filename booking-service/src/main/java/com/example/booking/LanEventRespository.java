package com.example.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.booking.model.Lan;

public interface LanEventRespository extends JpaRepository<Lan, String> {
    
}
