package com.example.common.dto;

import java.time.LocalDateTime;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record EventRegistrationDTO(
        @NotBlank @Max(100) String name,
        @Nullable @Max(1000) String description,
        @NotNull @FutureOrPresent LocalDateTime time,
        @NotBlank @Max(200) String place,
        @Positive int capacity) {

}
