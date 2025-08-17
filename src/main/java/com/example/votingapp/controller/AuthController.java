package com.example.votingapp.controller;

import com.example.votingapp.dto.LoginRequest;
import com.example.votingapp.dto.LoginResponse;
import com.example.votingapp.model.User;
import com.example.votingapp.security.JwtService;
import com.example.votingapp.service.AuthService;
import com.example.votingapp.service.VoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
//    private final VotingService votingService;
    private final JwtService jwtService;
    
    @Autowired
    private VoteService voteService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        User user = authService.authenticateUser(request.getUserId(), request.getPassword());
        if (user == null) {
            return ResponseEntity.badRequest().body("Invalid credentials");
        }
        if (!user.isCanVote() && !user.isAdmin()) {
            return ResponseEntity.status(403).body("Voting access denied");
        }

        // Build a Spring Security UserDetails shape just for token creation
        org.springframework.security.core.userdetails.UserDetails principal =
                org.springframework.security.core.userdetails.User
                        .withUsername(user.getUserId())
                        .password(user.getPassword())
                        .roles(user.isAdmin() ? "ADMIN" : "USER")
                        .build();

        String token = jwtService.generateToken(principal, user.isAdmin());
        return ResponseEntity.ok(new LoginResponse(token, user.getUserId(), user.isAdmin(), user.isCanVote()));
    }

    // Optional convenience endpoint to check if a user has voted in a session
    @GetMapping("/check-voted/{sessionId}")
    public ResponseEntity<?> hasUserVotedInSession(
            @RequestParam String userId,
            @PathVariable Long sessionId) {
        boolean hasVoted = voteService.hasUserVoted(userId, sessionId);
        return ResponseEntity.ok(hasVoted);
    }
}
