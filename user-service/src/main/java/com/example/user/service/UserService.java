package com.example.user.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.common.dto.UserRegisterDTO;
import com.example.common.dto.UserResponseDTO;
import com.example.common.dto.UserUpdateDTO;
import com.example.user.exception.UserException;
import com.example.user.model.User;
import com.example.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    // User validate
    public void validateUser(UserRegisterDTO userRegisterDTO) {
        if (userRepository.existsByUsername(userRegisterDTO.username())) {
            throw new UserException("Username already exists");
        }
        if (userRepository.existsByEmail(userRegisterDTO.email())) {
            throw new UserException("Email already exists");
        }
    }

    // Create user
    public UserResponseDTO registerUser(UserRegisterDTO userRegisterDTO) {
        User user = new User();
        user.setUsername(userRegisterDTO.username());
        user.setEmail(userRegisterDTO.email());
        user.setPassword(userRegisterDTO.password());

        User savedUser = userRepository.save(user);

        return new UserResponseDTO(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail());
    }

    // Get user by id
    public UserResponseDTO getUserById(UUID id) {
        return userRepository.findById(id)
                .map(user -> new UserResponseDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail()))
                .orElseThrow(() -> new UserException("User not found"));
    }

    // Update user
    public UserUpdateDTO updateUser(UUID id, UserUpdateDTO userUpdateDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException("User not found"));

        if (userUpdateDTO.newUsername() != null) {
            user.setUsername(userUpdateDTO.newUsername());
        }
        if (userUpdateDTO.newEmail() != null) {
            user.setEmail(userUpdateDTO.newEmail());
        }
        if (userUpdateDTO.newPassword() != null) {
            user.setPassword(userUpdateDTO.newPassword());
        }

        User updatedUser = userRepository.save(user);

        return new UserUpdateDTO(
                updatedUser.getUsername(),
                updatedUser.getEmail(),
                updatedUser.getPassword());
    }

    // Delete user
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new UserException("User not found");
        }
        userRepository.deleteById(id);
    }
}
