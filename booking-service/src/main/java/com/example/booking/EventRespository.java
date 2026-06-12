package com.example.booking;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.booking.model.Event;

public interface EventRespository extends JpaRepository<Event, UUID> {

}
