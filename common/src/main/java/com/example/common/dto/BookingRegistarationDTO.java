package com.example.common.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record BookingRegistarationDTO(
        @NotNull UUID eventId,
        @NotNull UUID userId) {

}
