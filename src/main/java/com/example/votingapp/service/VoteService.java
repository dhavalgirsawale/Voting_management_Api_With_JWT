package com.example.votingapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.example.votingapp.model.VoteOption;
import com.example.votingapp.strategy.VotingStrategy;

@Service
public class VoteService {
    
    private final VotingStrategy votingStrategy;

    @Autowired
    public VoteService(@Qualifier("defaultVotingStrategy") VotingStrategy votingStrategy) {
        this.votingStrategy = votingStrategy;
    }

    public void castVote(String userId, Long sessionId, String option) {
        votingStrategy.castVote(userId, sessionId, option);
    }

    public boolean hasUserVoted(String userId, Long sessionId) {
        return votingStrategy.hasUserVoted(userId, sessionId);
    }

    public List<VoteOption> getResults(Long sessionId) {
        return votingStrategy.getResults(sessionId);
    }
}

