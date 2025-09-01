//package com.example.votingapp.service;
//
//
//import com.example.votingapp.model.*;
//import com.example.votingapp.repository.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Service
//public class VotingService {
//    
//    @Autowired
//    private VoteOptionRepository voteOptionRepository;
//    
//    @Autowired
//    private UserRepository userRepository;
//    
//    @Autowired
//    private VoteRepository voteRepository;
//    
//    @Autowired
//    private VotingSessionRepository votingSessionRepository;
//    
//    public Long createSession(String title, int durationMinutes) {
//        VotingSession session = new VotingSession();
//        session.setTitle(title);
//        session.setStartTime(LocalDateTime.now());
//        session.setEndTime(LocalDateTime.now().plusMinutes(durationMinutes)); 
//        return votingSessionRepository.save(session).getId();
//    }
//    @Scheduled(fixedRate = 60000) // runs every 1 min
//    @Transactional
//    public void autoCloseExpiredSessions() {
//        List<VotingSession> sessions = votingSessionRepository.findByIsActiveTrue();
//        LocalDateTime now = LocalDateTime.now();
//
//        for (VotingSession session : sessions) {
//            if (session.getEndTime() != null && now.isAfter(session.getEndTime())) {
//                session.setActive(false);
//                votingSessionRepository.save(session);
//                System.out.println("Session " + session.getId() + " closed automatically.");
//            }
//        }
//    }
//
//    
////    public void endSession(Long sessionId) {
////        VotingSession session = votingSessionRepository.findById(sessionId)    
////            .orElseThrow(() -> new RuntimeException("Session not found"));
////        session.setActive(false);
////        votingSessionRepository.save(session);
////    }
//    
//    public void addOption(Long sessionId, String option) {
//        VotingSession session = votingSessionRepository.findById(sessionId)
//            .orElseThrow(() -> new RuntimeException("Session not found"));
//        
//        VoteOption voteOption = new VoteOption();
//        voteOption.setOptionName(option);
//        voteOption.setVoteCount(0);
//        voteOption.setSession(session);
//        voteOptionRepository.save(voteOption);
//    }
//    
//    public List<VoteOption> getOptions(Long sessionId) {
//        return voteOptionRepository.findBySessionId(sessionId);
//    }
//    
//    @Transactional
//    public void castVote(String userId, Long sessionId, String option) {
//        
//        User user = userRepository.findById(userId)
//            .orElseThrow(() -> new RuntimeException("User not found"));
//        
//        
//        VotingSession session = votingSessionRepository.findById(sessionId)
//            .orElseThrow(() -> new RuntimeException("Session not found"));
//        if (!session.isActive()) {
//            throw new RuntimeException("Voting session is closed");
//        }
//        
//        
//        if (voteRepository.existsByUserUserIdAndSessionId(userId, sessionId)) {
//            throw new RuntimeException("User has already voted in this session");
//        }
//        
//        List<VoteOption> options = voteOptionRepository.findBySessionIdAndOptionName(sessionId, option);
//        if (options.isEmpty()) {
//            throw new RuntimeException("Invalid option '" + option + "' for session " + sessionId);
//        }
//        VoteOption voteOption = options.get(0);        
//        
//        Vote vote = new Vote();
//        vote.setUser(user);
//        vote.setVoteOption(voteOption);
//        vote.setSession(session);
//        voteRepository.save(vote);
//        
//        // 6. Update vote count
//        voteOption.setVoteCount(voteOption.getVoteCount() + 1);
//        voteOptionRepository.save(voteOption);
//    }
//    
//    public List<VoteOption> getResults(Long sessionId) {
//        return voteOptionRepository.findBySessionId(sessionId);
//    }
//    
//    @Transactional
//        public void resetAllSessions() {
//            // Clear all vote records
//            voteRepository.deleteAll();
//            
//            // Reset all vote counts to 0
//            voteOptionRepository.findAll().forEach(option -> {   
//                option.setVoteCount(0);
//                voteOptionRepository.save(option);
//            });
//        }
//
//        @Transactional
//        public void hardResetAllVotingData() {
//            // Delete all votes
//            voteRepository.deleteAll();
//            
//            // Delete all options
//            voteOptionRepository.deleteAll();
//            
//            // Delete all sessions
//            votingSessionRepository.deleteAll();
//            
//            // Reset user voting permissions (if needed)
//            userRepository.findAll().forEach(user -> {
//                if (!user.isAdmin()) {
//                    user.setCanVote(true);
//                    userRepository.save(user);
//                }
//            });
//        }
//    
//    public List<VotingSession> getActiveSessions() {
//        return votingSessionRepository.findByIsActiveTrue();
//    }
//    
//    public boolean hasUserVotedInSession(String userId, Long sessionId) {
//        return voteRepository.existsByUserUserIdAndSessionId(userId, sessionId);
//    }
//}