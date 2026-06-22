package com.example.common.dto;

import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String accountName,
        String email,
        String role) {
}