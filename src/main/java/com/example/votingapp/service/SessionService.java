package com.example.votingapp.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.votingapp.model.VotingSession;
import com.example.votingapp.repository.VotingSessionRepository;

@Service
public class SessionService {

    @Autowired
    private VotingSessionRepository votingSessionRepository;

    public Long createSession(String title, int durationMinutes) {
        VotingSession session = new VotingSession();
        session.setTitle(title);
        session.setStartTime(LocalDateTime.now());
        session.setEndTime(LocalDateTime.now().plusMinutes(durationMinutes)); // auto close
        return votingSessionRepository.save(session).getId();
    }
    @Scheduled(fixedRate = 60000) // runs every 1 min
    @Transactional
    public void autoCloseExpiredSessions() {
        List<VotingSession> sessions = votingSessionRepository.findByIsActiveTrue();
        LocalDateTime now = LocalDateTime.now();

        for (VotingSession session : sessions) {
            if (session.getEndTime() != null && now.isAfter(session.getEndTime())) {
                session.setActive(false);
                votingSessionRepository.save(session);
                System.out.println("Session " + session.getId() + " closed automatically.");
            }
        }
    }

    public List<VotingSession> getActiveSessions() {
        return votingSessionRepository.findByIsActiveTrue();
    }
}

