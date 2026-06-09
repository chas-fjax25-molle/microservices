package com.example.common.dto;

import java.security.Timestamp;
import java.util.Optional;

public record EventRegistrationDTO(
        String name,
        Optional<String> description,
        Timestamp time,
        String place,
        int capacity) {

}
