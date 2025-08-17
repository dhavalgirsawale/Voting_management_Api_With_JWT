package com.example.votingapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;      // JWT
    private String userId;
    private boolean admin;
    private boolean canVote;
}
