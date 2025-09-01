package com.example.votingapp.service;

import com.example.votingapp.model.VotingSession;
import com.example.votingapp.repository.VotingSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class SessionService {
    @Autowired
    private VotingSessionRepository votingSessionRepository;

    public VotingSession createSession(String title, int durationMinutes) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        VotingSession session = new VotingSession();
        session.setTitle(title);
        session.setStartTime(now);
        session.setEndTime(now.plusMinutes(durationMinutes));
        return votingSessionRepository.save(session);
    }

    public List<VotingSession> getActiveSessions() {
        return votingSessionRepository.findByEndTimeAfter(ZonedDateTime.now(ZoneId.of("UTC")));
    }
}