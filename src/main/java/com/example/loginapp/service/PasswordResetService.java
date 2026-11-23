
package com.example.loginapp.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.loginapp.entity.PasswordResetToken;
import com.example.loginapp.entity.User;
import com.example.loginapp.repository.PasswordResetTokenRepository;
import com.example.loginapp.repository.UserRepository;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final SecureRandom secureRandom = new SecureRandom();

    public PasswordResetService(
            PasswordResetTokenRepository tokenRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Generate a secure random reset token
    @Transactional
    public String createResetToken(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                    new IllegalArgumentException("User not found"));

        // Remove previous reset tokens
        tokenRepository.deleteByEmail(user.getEmail());

        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        String rawToken = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        String tokenHash = hashToken(rawToken);

        PasswordResetToken resetToken = new PasswordResetToken(
                user.getEmail(),
                tokenHash,
                LocalDateTime.now().plusMinutes(15)
        );

        tokenRepository.save(resetToken);

        // Return original token for the reset link
        return rawToken;
    }

    // Validate reset token
    public PasswordResetToken validateResetToken(String rawToken) {

        String tokenHash = hashToken(rawToken);

        PasswordResetToken resetToken =
                tokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "Invalid or expired reset link"));

        if (resetToken.isUsed()
                || !LocalDateTime.now().isBefore(
                        resetToken.getExpiryTime())) {

            throw new IllegalArgumentException(
                    "Invalid or expired reset link");
        }

        return resetToken;
    }

    // Reset password
    @Transactional
    public void resetPassword(String rawToken, String newPassword) {

        PasswordResetToken resetToken =
                validateResetToken(rawToken);

        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException(
                    "Password cannot be empty");
        }

        validatePassword(newPassword);

        User user = userRepository.findByEmail(resetToken.getEmail())
                .orElseThrow(() ->
                    new IllegalArgumentException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }

    // Hash reset token using SHA-256
    private String hashToken(String token) {

        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(hash);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Unable to process reset token", e);
        }
    }

    // Password validation
    private void validatePassword(String password) {

        if (password.length() < 8) {
            throw new IllegalArgumentException(
                    "Password must contain at least 8 characters");
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException(
                    "Password must contain at least one uppercase letter");
        }

        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException(
                    "Password must contain at least one lowercase letter");
        }

        if (!password.matches(".*[0-9].*")) {
            throw new IllegalArgumentException(
                    "Password must contain at least one number");
        }

        if (!password.matches(".*[^a-zA-Z0-9].*")) {
            throw new IllegalArgumentException(
                    "Password must contain at least one special character");
        }
    }
}
