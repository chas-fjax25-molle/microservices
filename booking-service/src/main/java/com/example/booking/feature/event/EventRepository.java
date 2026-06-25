package com.example.booking.feature.event;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.booking.feature.event.model.Event;

public interface EventRepository extends JpaRepository<Event, UUID> {

}
