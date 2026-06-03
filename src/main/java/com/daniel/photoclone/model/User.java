package com.daniel.photoclone.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.HashSet;
import java.util.Set;

/**
 * USER ENTITY
 * ===========
 * Represents application users for authentication
 */
@Entity
@Table(name = "users")  // Note: 'user' is reserved in some databases
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * USERNAME
     * --------
     * Must be unique and not null
     */
    @Column(unique = true, nullable = false)
    private String username;

    /**
     * PASSWORD
     * --------
     * Will be encrypted before storing
     */
    @Column(nullable = false)
    private String password;

    /**
     * EMAIL
     * -----
     */
    @Column(nullable = false)
    private String email;

    /**
     * FULL NAME
     * ---------
     */
    @Column(name = "full_name")
    private String fullName;

    /**
     * ROLES (e.g., "USER", "ADMIN")
     * -----------------------------
     * @ElementCollection: Creates separate table for roles
     * FetchType.EAGER: Load roles immediately with user
     * 
     * This creates a table: user_roles(user_id, role)
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "user_roles",  // Table name
        joinColumns = @JoinColumn(name = "user_id")  // Foreign key
    )
    @Column(name = "role")  // Column name in user_roles table
    private Set<String> roles = new HashSet<>();

    /**
     * HELPER METHOD: Add role to user
     */
    public void addRole(String role) {
        this.roles.add(role);
    }
    
    /**
     * HELPER METHOD: Check if user has role
     */
    public boolean hasRole(String role) {
        return this.roles.contains(role);
    }
}