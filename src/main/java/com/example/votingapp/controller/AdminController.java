package com.example.votingapp.controller;

import com.example.votingapp.dto.RegisterUserRequest;
import com.example.votingapp.model.User;
import com.example.votingapp.model.VotingSession;
import com.example.votingapp.service.AdminMaintenanceService;
import com.example.votingapp.service.AuthService;
import com.example.votingapp.service.OptionService;
import com.example.votingapp.service.SessionService;
import com.example.votingapp.service.UserImportService;
import com.example.votingapp.service.VoteService;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.dao.DataAccessException;
import jakarta.persistence.EntityNotFoundException;
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
	@Autowired
    private SessionService sessionService;

    @Autowired
    private OptionService optionService;

    @Autowired
    private VoteService voteService;

    @Autowired
    private AdminMaintenanceService adminMaintenanceService;
    
    @Autowired
    private UserImportService u1;
    
    @Autowired
    private AuthService authService;
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

//    @PostMapping("/create-session")
//    public ResponseEntity<?> createSession(
//            @RequestParam String title,
//            @RequestParam int durationMinutes) {
//        VotingSession sessionId = sessionService.createSession(title, durationMinutes);
//        return ResponseEntity.ok("Session " + sessionId + " created and will auto-close in " + durationMinutes + " minutes.");
//    }
//    
//    @PostMapping("/add-option")
//    public ResponseEntity<?> addOption(
//            @RequestParam Long sessionId,
//            @RequestParam String option) {
//    	optionService.addOption(sessionId, option);
//        return ResponseEntity.ok("Option added");
//    }
    
    @GetMapping("/results")
    public ResponseEntity<?> getResults(@RequestParam Long sessionId) {
        return ResponseEntity.ok(voteService.getResults(sessionId));
    }
    
    
    
    @PostMapping("/grant-access")
    public ResponseEntity<?> grantVotingAccess(
            @RequestParam String userId,
            @RequestParam boolean canVote) {
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
            String fileName = file.getOriginalFilename();
            if (fileName == null || 
               !(fileName.endsWith(".csv") || fileName.endsWith(".xlsx") || fileName.endsWith(".xls"))) {
                return ResponseEntity.badRequest().body("Only CSV or Excel files are allowed");
            }

            // Import users
            List<User> importedUsers = u1.importUsers(file);

            return ResponseEntity.ok(
                importedUsers.size() + " users imported successfully"
            );

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to import users: " + e.getMessage());
        }
    }
    @PostMapping("/register-user")
    public ResponseEntity<?> registerUser(@RequestBody @Valid RegisterUserRequest request) {
        try {
            User newUser = authService.registerUser1(request);
            return ResponseEntity.ok("User registered: " + newUser.getUserId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

        @PostMapping("/create-session")
        public ResponseEntity<?> createSession(
                @RequestParam String title,
                @RequestParam int durationMinutes) {
            
            try {
                logger.info("Creating session with title: {}, duration: {} minutes", title, durationMinutes);
                
                // Validate input parameters
                if (title == null || title.trim().isEmpty()) {
                    return ResponseEntity.badRequest().body("Title cannot be empty");
                }
                
                if (durationMinutes <= 0) {
                    return ResponseEntity.badRequest().body("Duration must be positive");
                }
                
                VotingSession session = sessionService.createSession(title, durationMinutes);
                
                logger.info("Session created successfully with ID: {}", session.getId());
                return ResponseEntity.ok("Session " + session.getId() + " created and will auto-close in " + durationMinutes + " minutes.");
                
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid request parameters: {}", e.getMessage());
                return ResponseEntity.badRequest().body("Error: " + e.getMessage());
                
            } catch (DataAccessException e) {
                logger.error("Database error creating session: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Database error: " + e.getMessage());
                        
            } catch (Exception e) {
                logger.error("Unexpected error creating session: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Unexpected error: " + e.getMessage());
            }
        }

        @PostMapping("/add-option")
        public ResponseEntity<?> addOption(
                @RequestParam Long sessionId,
                @RequestParam String option) {
            
            try {
                logger.info("Adding option: {} to session ID: {}", option, sessionId);
                
                // Validate input parameters
                if (option == null || option.trim().isEmpty()) {
                    return ResponseEntity.badRequest().body("Option cannot be empty");
                }
                
                if (sessionId == null || sessionId <= 0) {
                    return ResponseEntity.badRequest().body("Invalid session ID");
                }
                
                optionService.addOption(sessionId, option);
                
                logger.info("Option added successfully to session ID: {}", sessionId);
                return ResponseEntity.ok("Option added");
                
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid request parameters: {}", e.getMessage());
                return ResponseEntity.badRequest().body("Error: " + e.getMessage());
                
            } catch (EntityNotFoundException e) {
                logger.warn("Session not found: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Session not found: " + e.getMessage());
                        
            } catch (DataAccessException e) {
                logger.error("Database error adding option: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Database error: " + e.getMessage());
                        
            } catch (Exception e) {
                logger.error("Unexpected error adding option: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Unexpected error: " + e.getMessage());
            }
        }
        @ExceptionHandler(MissingServletRequestParameterException.class)
        public ResponseEntity<?> handleMissingParams(MissingServletRequestParameterException ex) {
            String paramName = ex.getParameterName();
            logger.warn("Missing required parameter: {}", paramName);
            return ResponseEntity.badRequest()
                    .body("Missing required parameter: " + paramName);
        }

}