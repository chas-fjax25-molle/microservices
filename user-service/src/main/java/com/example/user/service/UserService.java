package com.example.user.service;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.common.dto.LoginRequestDTO;
import com.example.common.dto.UserRegisterDTO;
import com.example.common.dto.UserResponseDTO;
import com.example.common.dto.UserUpdateDTO;
import com.example.user.exception.UserNotFoundException;
import com.example.user.model.User;
import com.example.user.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // User validate
    public UserResponseDTO validateUser(LoginRequestDTO loginRequest) {
        if (loginRequest.accountName().isEmpty() || loginRequest.password().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username and password cannot be blank");
        }
        User user = userRepository.findByUsername(loginRequest.accountName())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));
        if (!user.getPassword().equals(loginRequest.password())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail());
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
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    // Update user
    public UserResponseDTO updateUser(UUID id, UserUpdateDTO userUpdateDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

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

        return new UserResponseDTO(
                id,
                updatedUser.getUsername(),
                updatedUser.getEmail());
    }

    // Delete user
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }
}
