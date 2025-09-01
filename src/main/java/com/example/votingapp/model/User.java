package com.example.votingapp.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
	@Id
    @Column(name = "user_id")  
    private String userId;
	@Column(name = "password", length = 60)  
	private String password;
    private boolean isAdmin;
    private boolean canVote=true;
    //private boolean hasVoted;
    public boolean isAdmin() {
        return isAdmin;
    }
}