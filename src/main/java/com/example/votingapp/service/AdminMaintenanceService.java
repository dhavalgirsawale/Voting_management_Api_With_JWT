package com.example.votingapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.votingapp.repository.UserRepository;
import com.example.votingapp.repository.VoteOptionRepository;
import com.example.votingapp.repository.VoteRepository;
import com.example.votingapp.repository.VotingSessionRepository;

import jakarta.transaction.Transactional;

@Service
public class AdminMaintenanceService {

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private VoteOptionRepository voteOptionRepository;

    @Autowired
    private VotingSessionRepository votingSessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void resetAllSessions() {
        voteRepository.deleteAll();

        voteOptionRepository.findAll().forEach(option -> {
            option.setVoteCount(0);
            voteOptionRepository.save(option);
        });
    }

    @Transactional
    public void hardResetAllVotingData() {
        voteRepository.deleteAll();
        voteOptionRepository.deleteAll();
        votingSessionRepository.deleteAll();

        userRepository.findAll().forEach(user -> {
            if (!user.isAdmin()) {
                user.setCanVote(true);
                userRepository.save(user);
            }
        });
    }
}
