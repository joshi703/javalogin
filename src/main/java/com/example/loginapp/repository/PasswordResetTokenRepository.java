
package com.example.loginapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.loginapp.entity.PasswordResetToken;

@Repository
public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, Long> {

    // Find token using its hash
    Optional<PasswordResetToken> findByTokenHash(String tokenHash);

    // Find reset tokens belonging to an email
    Optional<PasswordResetToken> findByEmail(String email);

    // Delete existing reset tokens for an email
    void deleteByEmail(String email);
}
