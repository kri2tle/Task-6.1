package com.example.personalizedlearning.db;

/**
 * Model class representing a user profile in the database
 */
public class UserProfile {
    private int id;
    private String username;
    private String email;
    private String passwordHash;
    private String interests;
    
    public UserProfile() {
    }
    
    public UserProfile(int id, String username, String email, String passwordHash, String interests) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.interests = interests;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public String getInterests() {
        return interests;
    }
    
    public void setInterests(String interests) {
        this.interests = interests;
    }
} 