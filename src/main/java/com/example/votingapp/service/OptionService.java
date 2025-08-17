package com.example.votingapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.votingapp.model.VoteOption;
import com.example.votingapp.model.VotingSession;
import com.example.votingapp.repository.VoteOptionRepository;
import com.example.votingapp.repository.VotingSessionRepository;

@Service
public class OptionService {

    @Autowired
    private VoteOptionRepository voteOptionRepository;

    @Autowired
    private VotingSessionRepository votingSessionRepository;

    public void addOption(Long sessionId, String option) {
        VotingSession session = votingSessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found"));

        VoteOption voteOption = new VoteOption();
        voteOption.setOptionName(option);
        voteOption.setVoteCount(0);
        voteOption.setSession(session);
        voteOptionRepository.save(voteOption);
    }

    public List<VoteOption> getOptions(Long sessionId) {
        return voteOptionRepository.findBySessionId(sessionId);
    }
}
