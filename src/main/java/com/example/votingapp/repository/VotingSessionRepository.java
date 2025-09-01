package com.example.votingapp.repository;

import com.example.votingapp.model.VotingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.ZonedDateTime;
import java.util.List;

public interface VotingSessionRepository extends JpaRepository<VotingSession, Long> {
    List<VotingSession> findByEndTimeAfter(ZonedDateTime now);
}