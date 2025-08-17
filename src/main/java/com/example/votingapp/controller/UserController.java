package com.example.votingapp.controller;

import com.example.votingapp.service.OptionService;
import com.example.votingapp.service.SessionService;
import com.example.votingapp.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user")
public class UserController {
    
//    @Autowired
//    private VotingService votingService;
	@Autowired
    private SessionService sessionService;

    @Autowired
    private OptionService optionService;

    @Autowired
    private VoteService voteService;
    
    @GetMapping("/sessions")
    public ResponseEntity<?> getActiveSessions() {
        return ResponseEntity.ok(sessionService.getActiveSessions());
    }
    
    @GetMapping("/options")
    public ResponseEntity<?> getOptions(@RequestParam Long sessionId) {
        return ResponseEntity.ok(optionService.getOptions(sessionId));
    }
    
    @PostMapping("/vote")
    public ResponseEntity<?> castVote(
            @RequestParam String userId,
            @RequestParam Long sessionId,
            @RequestParam String option) {
        try {
        	voteService.castVote(userId, sessionId, option);
            return ResponseEntity.ok("Vote cast successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}