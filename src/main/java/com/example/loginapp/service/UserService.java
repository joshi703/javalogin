
package com.example.loginapp.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.loginapp.entity.User;
import com.example.loginapp.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // User Registration
    public User registerUser(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException(
                "Email is already registered"
            );
        }

        validatePassword(user.getPassword());

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    // Find user by email
    public User getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                    new IllegalArgumentException("User not found")
                );
    }

    // Update user profile
    public User updateProfile(String currentEmail,
                              String username,
                              String newEmail) {

        User user = getUserByEmail(currentEmail);

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Username cannot be empty"
            );
        }

        if (newEmail == null || newEmail.trim().isEmpty()
                || !newEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException(
                "Please enter a valid email address"
            );
        }

        username = username.trim();
        newEmail = newEmail.trim();

        if (!currentEmail.equalsIgnoreCase(newEmail)
                && userRepository.existsByEmailAndIdNot(
                    newEmail, user.getId())) {

            throw new IllegalArgumentException(
                "Email is already registered"
            );
        }

        user.setUsername(username);
        user.setEmail(newEmail);

        return userRepository.save(user);
    }

    // Server-side password validation
    private void validatePassword(String password) {

        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException(
                "Password must contain at least 8 characters"
            );
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException(
                "Password must contain at least one uppercase letter"
            );
        }

        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException(
                "Password must contain at least one lowercase letter"
            );
        }

        if (!password.matches(".*[0-9].*")) {
            throw new IllegalArgumentException(
                "Password must contain at least one number"
            );
        }

        if (!password.matches(".*[^a-zA-Z0-9].*")) {
            throw new IllegalArgumentException(
                "Password must contain at least one special character"
            );
        }
    }
}
