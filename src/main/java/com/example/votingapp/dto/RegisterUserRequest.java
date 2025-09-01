package com.example.votingapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterUserRequest {

    @NotBlank
    private String userId;  

    @NotBlank
    private String password;

    private boolean isAdmin; 
    
}
