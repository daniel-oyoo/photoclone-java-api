package com.daniel.photoclone;

import com.daniel.photoclone.model.User;
import com.daniel.photoclone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.Set;

/**
 * DATA INITIALIZER
 * ================
 * Runs on application startup to create test data
 * 
 * @Component: Spring bean
 * CommandLineRunner: Runs after application context is loaded
 * 
 * Useful for:
 * 1. Creating test users
 * 2. Loading initial data
 * 3. Database migrations
 */
@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "photoclone.seed-data", havingValue = "true")
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("=== Starting optional data initialization ===");
        
        // Only create test data if no users exist
        if (userRepository.count() == 0) {
            createTestUsers();
        } else {
            log.info("Users already exist, skipping test data creation");
        }
        
        log.info("=== Data Initialization Complete ===");
    }

    /**
     * CREATE TEST USERS
     */
    private void createTestUsers() {
        log.info("Creating test users...");
        
        // Create ADMIN user
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123")); // Encrypt!
        admin.setEmail("admin@photoclone.com");
        admin.setFullName("Administrator");
        admin.setRoles(Set.of("USER", "ADMIN"));
        userRepository.save(admin);
        log.info("Created admin user");
        
        // Create REGULAR user
        User user = new User();
        user.setUsername("user");
        user.setPassword(passwordEncoder.encode("user123")); // Encrypt!
        user.setEmail("user@photoclone.com");
        user.setFullName("Regular User");
        user.setRoles(Set.of("USER"));
        userRepository.save(user);
        log.info("Created regular user");
        
        // Create TEST user
        User test = new User();
        test.setUsername("test");
        test.setPassword(passwordEncoder.encode("test123")); // Encrypt!
        test.setEmail("test@photoclone.com");
        test.setFullName("Test User");
        test.setRoles(Set.of("USER"));
        userRepository.save(test);
        log.info("Created test user");
        
        log.info("Total users created: {}", userRepository.count());
    }

}