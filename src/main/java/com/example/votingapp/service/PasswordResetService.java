package com.example.votingapp.service;

import com.example.votingapp.model.PasswordResetToken;
import com.example.votingapp.model.User;
import com.example.votingapp.repository.PasswordResetTokenRepository;
import com.example.votingapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void sendResetOtp(String userId) {
    	Optional<User> userOpt = userRepository.findById(userId); if (userOpt.isEmpty()) { throw new RuntimeException("User not found with id: " + userId); }

        // Generate 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(999999));

        // Remove any existing tokens for this user
        tokenRepository.deleteByUserId(userId);

        // Save new token
        PasswordResetToken token = new PasswordResetToken();
        token.setUserId(userId);
        token.setOtp(otp);
        token.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        tokenRepository.save(token);

        // Send OTP via email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(userId);
        message.setSubject("VotingApp Password Reset");
        message.setText("Your OTP is: " + otp + " (valid for 5 minutes)");
        mailSender.send(message);
    }

    @Transactional
    public void verifyOtpAndResetPassword(String userId, String otp, String newPassword) {
        PasswordResetToken token = tokenRepository.findByUserIdAndOtp(userId, otp)
                .orElseThrow(() -> new RuntimeException("Invalid OTP"));

        if (token.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Encode new password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Remove used token
        tokenRepository.deleteByUserId(userId);
    }
}
