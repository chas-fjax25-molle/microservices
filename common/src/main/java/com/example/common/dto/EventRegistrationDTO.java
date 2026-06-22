package com.example.common.dto;

import java.time.LocalDateTime;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record EventRegistrationDTO(
        @NotBlank @Size(max = 100) String name,
        @Nullable @Size(max = 1000) String description,
        @NotNull @FutureOrPresent LocalDateTime time,
        @NotBlank @Size(max = 200) String place,
        @Positive int capacity) {

}
