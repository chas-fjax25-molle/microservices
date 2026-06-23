package com.example.common.dto;

import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String userName,
        String email) {

}
