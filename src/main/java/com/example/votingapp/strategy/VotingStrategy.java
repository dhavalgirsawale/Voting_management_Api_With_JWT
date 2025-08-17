package com.example.votingapp.strategy;

import java.util.List;

import com.example.votingapp.model.VoteOption;

public interface VotingStrategy {
    void castVote(String userId, Long sessionId, String option);
    boolean hasUserVoted(String userId, Long sessionId);
    List<VoteOption> getResults(Long sessionId);
}
