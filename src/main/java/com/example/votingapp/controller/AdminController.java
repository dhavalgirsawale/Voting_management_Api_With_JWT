package com.example.votingapp.controller;

import com.example.votingapp.model.User;
import com.example.votingapp.service.AdminMaintenanceService;
import com.example.votingapp.service.OptionService;
import com.example.votingapp.service.SessionService;
import com.example.votingapp.service.UserImportService;
import com.example.votingapp.service.VoteService;
//import com.example.votingapp.service.VotingService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
//    @Autowired
//    private VotingService votingService;
	@Autowired
    private SessionService sessionService;

    @Autowired
    private OptionService optionService;

    @Autowired
    private VoteService voteService;

    @Autowired
    private AdminMaintenanceService adminMaintenanceService;
    
    @Autowired
    private UserImportService userImportService;
    
    @PostMapping("/create-session")
    public ResponseEntity<?> createSession(
            @RequestParam String title,
            @RequestParam int durationMinutes) {
        Long sessionId = sessionService.createSession(title, durationMinutes);
        return ResponseEntity.ok("Session " + sessionId + " created and will auto-close in " + durationMinutes + " minutes.");
    }
    
//    @PostMapping("/end-session")
//    public ResponseEntity<?> endSession(@RequestParam Long sessionId) {
//        votingService.endSession(sessionId);
//        return ResponseEntity.ok("Session ended");
//    }
    
    @PostMapping("/add-option")
    public ResponseEntity<?> addOption(
            @RequestParam Long sessionId,
            @RequestParam String option) {
    	optionService.addOption(sessionId, option);
        return ResponseEntity.ok("Option added");
    }
    
    @GetMapping("/results")
    public ResponseEntity<?> getResults(@RequestParam Long sessionId) {
        return ResponseEntity.ok(voteService.getResults(sessionId));
    }
    
    
    
    @PostMapping("/grant-access")
    public ResponseEntity<?> grantVotingAccess(
            @RequestParam String userId,
            @RequestParam boolean canVote) {
        // Implementation would update user's canVote status
        return ResponseEntity.ok("Voting access updated");
    }

    @PostMapping("/reset-votes")
    public ResponseEntity<?> resetVotes() {
    	adminMaintenanceService.resetAllSessions();
        return ResponseEntity.ok("All votes and results have been reset");
    }

    @PostMapping("/hard-reset")
    public ResponseEntity<?> hardReset() {
    	adminMaintenanceService.hardResetAllVotingData();
        return ResponseEntity.ok("All voting data has been completely reset except users");
    }
    @PostMapping("/import-users")
    public ResponseEntity<?> importUsers(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Please select a file to upload");
            }

            // Check file type
            String fileName = file.getOriginalFilename();
            if (fileName == null || 
               !(fileName.endsWith(".csv") || fileName.endsWith(".xlsx") || fileName.endsWith(".xls"))) {
                return ResponseEntity.badRequest().body("Only CSV or Excel files are allowed");
            }

            // Import users
            List<User> importedUsers = userImportService.importUsers(file);

            return ResponseEntity.ok(
                importedUsers.size() + " users imported successfully"
            );

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to import users: " + e.getMessage());
        }
    }
}