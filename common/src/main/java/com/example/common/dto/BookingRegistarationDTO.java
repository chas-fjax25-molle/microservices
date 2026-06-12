package com.example.common.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotEmpty;

public record BookingRegistarationDTO(
        @NotEmpty UUID eventId,
        @NotEmpty UUID userId) {

}
