package com.example.votingapp.strategy;

import com.example.votingapp.model.*;
import com.example.votingapp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component("defaultVotingStrategy")
public class DefaultVotingStrategy implements VotingStrategy {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VotingSessionRepository votingSessionRepository;
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private VoteOptionRepository voteOptionRepository;

    @Override
    @Transactional
    public void castVote(String userId, Long sessionId, String option) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        VotingSession session = votingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        
        if (!session.isActive()) {
            throw new RuntimeException("Voting session is closed");
        }

        if (voteRepository.existsByUserUserIdAndSessionId(userId, sessionId)) {
            throw new RuntimeException("User has already voted in this session");
        }

        List<VoteOption> options = voteOptionRepository.findBySessionIdAndOptionName(sessionId, option);
        if (options.isEmpty()) {
            throw new RuntimeException("Invalid option '" + option + "' for session " + sessionId);
        }

        VoteOption voteOption = options.get(0);
        Vote vote = new Vote();
        vote.setUser(user);
        vote.setVoteOption(voteOption);
        vote.setSession(session);
        voteRepository.save(vote);

        voteOption.setVoteCount(voteOption.getVoteCount() + 1);
        voteOptionRepository.save(voteOption);
    }

    @Override
    public boolean hasUserVoted(String userId, Long sessionId) {
        return voteRepository.existsByUserUserIdAndSessionId(userId, sessionId);
    }

    @Override
    public List<VoteOption> getResults(Long sessionId) {
        return voteOptionRepository.findBySessionId(sessionId);
    }
}