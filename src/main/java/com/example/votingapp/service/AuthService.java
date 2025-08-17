package com.example.votingapp.service;

import com.example.votingapp.model.User;
import com.example.votingapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
//    public User authenticateUser(String userId, String password) {
//        return userRepository.findByUserIdAndPassword(userId, password);
//    }
//    
//    public User authenticateAdmin(String userId, String password) {
//        return userRepository.findByUserIdAndPasswordAndIsAdmin(userId, password, true);
//    }
//    
//    public void registerUser(User user) {
//        userRepository.save(user);
//    }
    
    @Autowired
    private PasswordEncoder passwordEncoder;  // Injected encoder
    
//    public User authenticateUser(String userId, String password) {
//        User user = userRepository.findById(userId).orElse(null);
//        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
//            return user;
//        }
//        return null;
//    }
    
    public User authenticateUser(String userId, String password) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
        	System.out.println("Raw password from request: '" + password + "'");
        	System.out.println("Length: " + password.length());
        	System.out.println("DB hash: '" + user.getPassword() + "'");
        	System.out.println("Length: " + user.getPassword().length());
        	System.out.println("Match? " + passwordEncoder.matches(password, user.getPassword()));
            
            return passwordEncoder.matches(password, user.getPassword()) ? user : null;
        }
        return null;
    }

    
    public User authenticateAdmin(String userId, String password) {
        User user = authenticateUser(userId, password);
        return (user != null && user.isAdmin()) ? user : null;
    }
    
    public void registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));  // Encode password
        userRepository.save(user);
    }
    
    
}