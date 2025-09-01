package com.example.votingapp.controller;

import com.example.votingapp.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class PasswordResetController {
    @Autowired
    private PasswordResetService resetService;

    @PostMapping("/request-password-reset")
    public ResponseEntity<String> requestReset(@RequestParam String userId) {
        try {
            resetService.sendResetOtp(userId);
            return ResponseEntity.ok("OTP sent to your email.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/verify-reset")
    public ResponseEntity<String> verifyReset(@RequestParam String userId, @RequestParam String otp, @RequestParam String newPassword) {
        try {
            resetService.verifyOtpAndResetPassword(userId, otp, newPassword);
            return ResponseEntity.ok("Password updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}