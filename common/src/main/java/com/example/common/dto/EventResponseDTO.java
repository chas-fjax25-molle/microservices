package com.example.common.dto;

import java.security.Timestamp;
import java.util.UUID;

public record EventResponseDTO(
        UUID id,
        String name,
        String description,
        Timestamp time,
        String place,
        int capacity,
        int freeSpots) {
}