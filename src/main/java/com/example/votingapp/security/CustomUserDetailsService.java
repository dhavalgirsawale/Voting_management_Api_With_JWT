package com.example.votingapp.security;

import com.example.votingapp.model.User;
import com.example.votingapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User u = userRepository.findByUserId(userId);
        
        if (u == null) {
            throw new UsernameNotFoundException("User not found: " + userId);
        }

        String role = u.isAdmin() ? "ROLE_ADMIN" : "ROLE_USER";
        return org.springframework.security.core.userdetails.User
                .withUsername(u.getUserId())
                .password(u.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority(role)))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}