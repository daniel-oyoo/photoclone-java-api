package com.daniel.photoclone.service;

import com.daniel.photoclone.model.User;
import com.daniel.photoclone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * REGISTER NEW USER
     */
    @Transactional
    public User registerUser(String username, String password, String email, String fullName) {
        System.out.println("Registering user: " + username);
        
        // Check if username exists
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
        
        // Check if email exists
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered: " + email);
        }
        
        // Create new user
        User user = new User();
        user.setUsername(username);
        
        // ENCRYPT PASSWORD before storing (VERY IMPORTANT!)
        String encryptedPassword = passwordEncoder.encode(password);
        user.setPassword(encryptedPassword);
        
        user.setEmail(email);
        user.setFullName(fullName);
        
        // Set default role
        user.setRoles(Set.of("USER"));
        
        // Save to database
        User savedUser = userRepository.save(user);
        System.out.println("User registered successfully: " + savedUser.getUsername());
        
        return savedUser;
    }

    /**
     * FIND USER BY USERNAME
     */
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
    }

    /**
     * GET ALL USERS
     */
    @Transactional(readOnly = true)
    public java.util.List<User> getAllUsers() {
        return userRepository.findAll();
    }
}