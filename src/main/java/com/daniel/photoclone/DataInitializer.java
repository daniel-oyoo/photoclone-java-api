package com.daniel.photoclone;

import com.daniel.photoclone.model.User;
import com.daniel.photoclone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

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
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("=== Starting Data Initialization ===");
        
        // Only create test data if no users exist
        if (userRepository.count() == 0) {
            createTestUsers();
        } else {
            log.info("Users already exist, skipping test data creation");
        }
        
        log.info("=== Data Initialization Complete ===");
        printApplicationInfo();
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
        log.info("Created admin user: admin/admin123");
        
        // Create REGULAR user
        User user = new User();
        user.setUsername("user");
        user.setPassword(passwordEncoder.encode("user123")); // Encrypt!
        user.setEmail("user@photoclone.com");
        user.setFullName("Regular User");
        user.setRoles(Set.of("USER"));
        userRepository.save(user);
        log.info("Created regular user: user/user123");
        
        // Create TEST user
        User test = new User();
        test.setUsername("test");
        test.setPassword(passwordEncoder.encode("test123")); // Encrypt!
        test.setEmail("test@photoclone.com");
        test.setFullName("Test User");
        test.setRoles(Set.of("USER"));
        userRepository.save(test);
        log.info("Created test user: test/test123");
        
        log.info("Total users created: {}", userRepository.count());
    }

    /**
     * PRINT APPLICATION INFO
     */
    private void printApplicationInfo() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println(" PHOTOCLONE APPLICATION READY!");
        System.out.println("=".repeat(50));
        System.out.println("\n TEST CREDENTIALS:");
        System.out.println("    admin / admin123 (ADMIN role)");
        System.out.println("    user / user123 (USER role)");
        System.out.println("    test / test123 (USER role)");
        System.out.println("\n ACCESS POINTS:");
        System.out.println("    Application: http://localhost:8080");
        System.out.println("    H2 Console: http://localhost:8080/h2-console");
        System.out.println("    JDBC URL: jdbc:h2:mem:photodb");
        System.out.println("    Username: sa (no password)");
        System.out.println("\n API ENDPOINTS:");
        System.out.println("    POST   /api/users/register");
        System.out.println("    POST   /api/photos/upload");
        System.out.println("    GET    /api/photos");
        System.out.println("    GET    /api/photos/{id}");
        System.out.println("    PUT    /api/photos/{id}");
        System.out.println("    DELETE /api/photos/{id}");
        System.out.println("=".repeat(50) + "\n");
    }
}