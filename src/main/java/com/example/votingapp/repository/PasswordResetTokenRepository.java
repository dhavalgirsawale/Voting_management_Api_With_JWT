package com.example.votingapp.repository;

import com.example.votingapp.model.PasswordResetToken;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByUserIdAndOtp(String userId, String otp);
    @Transactional
    void deleteByUserId(String userId);
}
