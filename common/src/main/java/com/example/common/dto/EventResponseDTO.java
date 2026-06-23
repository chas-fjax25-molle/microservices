package com.example.common.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record EventResponseDTO(
        UUID id,
        String name,
        String description,
        LocalDateTime time,
        String place,
        int capacity,
        int freeSpots) {
}