package com.example.votingapp;

import java.util.List;
import java.util.TimeZone;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.votingapp.model.User;
import com.example.votingapp.repository.UserRepository;

import jakarta.annotation.PostConstruct;
@SpringBootApplication
@EnableScheduling
public class VotingappApplication {
	@PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        System.setProperty("user.timezone", "UTC");
        
        System.out.println("Timezone forced to UTC: " );
        System.out.println("JVM timezone: " + TimeZone.getDefault().getID());
    }

	public static void main(String[] args) {
		SpringApplication.run(VotingappApplication.class, args);
	}
	
	@Bean
    public CommandLineRunner migratePasswords(
            UserRepository userRepository, 
            PasswordEncoder passwordEncoder) {
        return args -> {
            List<User> users = userRepository.findAll();
            for (User user : users) {
                if (!user.getPassword().startsWith("$2a$")) {
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                    userRepository.save(user);
                }
            }
        };
    }
	@Bean
    public CommandLineRunner createDefaultAdmin(UserRepository userRepository, PasswordEncoder encoder) {
        return args -> {
            String defaultAdminId = "siesadmin";  
            String defaultPassword = "admin123";          

            if (!userRepository.existsById(defaultAdminId)) {
                User admin = new User();
                admin.setUserId(defaultAdminId);
                admin.setPassword(encoder.encode(defaultPassword));
                admin.setAdmin(true);
                admin.setCanVote(false); // admin cannot vote
                userRepository.save(admin);

                System.out.println("✅ Default admin created: " + defaultAdminId + " / " + defaultPassword);
            } else {
                System.out.println("ℹ️ Admin already exists: " + defaultAdminId);
            }
        };
    }
}
