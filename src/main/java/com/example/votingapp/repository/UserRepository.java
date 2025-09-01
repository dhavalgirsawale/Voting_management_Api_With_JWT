package com.example.votingapp.repository;

import com.example.votingapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
	User  findByUserId(String userId);
//    User findByUserIdAndPassword(String userId, String password);
//    User findByUserIdAndPasswordAndIsAdmin(String userId, String password, boolean isAdmin);
}