package com.example.common.dto;

import java.util.Optional;

public record UserUpdateDTO(
        Optional<String> newUsername,
        Optional<String> newEmail,
        Optional<String> newPassword) {

}
