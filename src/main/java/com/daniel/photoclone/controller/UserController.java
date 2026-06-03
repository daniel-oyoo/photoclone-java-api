package com.daniel.photoclone.controller;

import com.daniel.photoclone.model.User;
import com.daniel.photoclone.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * POST /api/users/register
     * ------------------------
     * Register a new user (no authentication required)
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> request) {
        System.out.println("=== User Registration ===");
        System.out.println("Request: " + request);
        
        try {
            // Extract parameters from request body
            String username = request.get("username");
            String password = request.get("password");
            String email = request.get("email");
            String fullName = request.get("fullName");
            
            // Validate required fields
            if (username == null || password == null || email == null) {
                throw new IllegalArgumentException("Username, password, and email are required");
            }
            
            // Register user
            User user = userService.registerUser(username, password, email, fullName);
            
            // Don't return password in response!
            user.setPassword("[PROTECTED]");
            
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(user);
                    
        } catch (IllegalArgumentException e) {
            // Return error response
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(error);
        }
    }

    /**
     * GET /api/users
     * --------------
     * Get all users (requires authentication)
     */
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        System.out.println("Fetching all users");
        
        try {
            var users = userService.getAllUsers();
            
            // Hide passwords in response
            users.forEach(user -> user.setPassword("[PROTECTED]"));
            
            return ResponseEntity.ok(users);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch users: " + e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(error);
        }
    }
}