package com.example.common.dto;

import java.util.UUID;

public record BookingResponseDTO(
        UUID id,
        UUID eventId,
        UUID userId) {

}
