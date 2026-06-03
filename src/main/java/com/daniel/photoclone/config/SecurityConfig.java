package com.daniel.photoclone.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SECURITY CONFIGURATION
 * ======================
 * Configures Spring Security for the application
 * 
 * @Configuration: Marks as configuration class
 * @EnableWebSecurity: Enables Spring Security web security support
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * SECURITY FILTER CHAIN BEAN
     * --------------------------
     * Main security configuration method
     * 
     * @param http HttpSecurity to configure
     * @return SecurityFilterChain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("Configuring Security Filter Chain...");
        
        http
            // ============================================
            // 1. AUTHORIZATION CONFIGURATION
            // ============================================
            .authorizeHttpRequests(auth -> auth
                // PUBLIC ENDPOINTS (no authentication required)
                .requestMatchers("/api/users/register").permitAll()
                .requestMatchers("/h2-console/**").permitAll()  // H2 console
                .requestMatchers("/error").permitAll()          // Error pages
                
                // PROTECTED ENDPOINTS (require authentication)
                .requestMatchers("/api/**").permitAll()
                //authenticated()     // All API endpoints
                
                // Any other request
                .anyRequest().permitAll()
            )
            
            // ============================================
            // 2. HTTP BASIC AUTHENTICATION
            // ============================================
            .httpBasic(Customizer.withDefaults())
            // Basic Auth sends username:password in Authorization header
            // Base64 encoded: "Basic base64(username:password)"
            
            // ============================================
            // 3. CSRF CONFIGURATION
            // ============================================
            .csrf(AbstractHttpConfigurer::disable)
            // Disable CSRF for API testing (enable for production with frontend)
            // CSRF (Cross-Site Request Forgery) protection is for browser apps
            
            // ============================================
            // 4. FRAME OPTIONS (for H2 Console)
            // ============================================
            .headers(headers -> headers.frameOptions().disable())
            // Allows H2 console to be displayed in iframe
            
            // ============================================
            // 5. CORS CONFIGURATION
            // ============================================
            .cors(Customizer.withDefaults());
            // Configure CORS as needed
        
        System.out.println("Security configuration complete!");
        return http.build();
    }

    /**
     * PASSWORD ENCODER BEAN
     * ---------------------
     * BCrypt is a strong hashing algorithm for passwords
     * NEVER store plain text passwords!
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        System.out.println("Creating BCryptPasswordEncoder...");
        return new BCryptPasswordEncoder();
        // BCrypt features:
        // - Automatically generates salt
        // - Configurable strength (default: 10)
        // - One-way hash (cannot be reversed)
    }
}