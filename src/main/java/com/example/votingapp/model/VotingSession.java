package com.example.votingapp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "voting_sessions")
@Data
public class VotingSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private boolean isActive = true;
    
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
    @JsonIgnore // Add this to break the cycle
    private List<VoteOption> options;
    
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
    @JsonIgnore // Add this to break the cycle
    private List<Vote> votes;
}