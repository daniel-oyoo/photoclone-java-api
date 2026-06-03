package com.daniel.photoclone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * MAIN APPLICATION CLASS
 * =====================
 * 
 * @SpringBootApplication is a meta-annotation that combines:
 * 1. @SpringBootConfiguration: Marks this as a configuration class
 * 2. @EnableAutoConfiguration: Enables Spring Boot auto-configuration
 * 3. @ComponentScan: Scans for components in current package and sub-packages
 * 
 * This is the entry point of the Spring Boot application.
 * When you run this class, Spring Boot:
 * 1. Starts an embedded web server (Tomcat by default)
 * 2. Configures Spring beans
 * 3. Sets up the application context
 */
@SpringBootApplication
public class PhotocloneApplication {

    /**
     * MAIN METHOD - Application Entry Point
     * 
     * SpringApplication.run() does the following:
     * 1. Creates an ApplicationContext (Spring container)
     * 2. Registers all @Bean definitions
     * 3. Starts the embedded web server
     * 4. Executes CommandLineRunner beans
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // SpringApplication.run() boots up the Spring application
        SpringApplication.run(PhotocloneApplication.class, args);
        
        // After startup completes
        System.out.println("=========================================");
        System.out.println(" PhotoClone Application Started!");
        System.out.println(" Server running on: http://localhost:8080");
        System.out.println(" H2 Console: http://localhost:8080/h2-console");
        System.out.println("=========================================");
    }
}