package com.grade.tracker.model;

/**
 * Represents a system user (e.g. Teacher, Admin) for authentication.
 */
public class User {
    private String username;
    private String passwordHash;
    private String salt;
    private String fullName;
    private String role;

    public User(String username, String passwordHash, String salt, String fullName, String role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.fullName = fullName;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return fullName + " (" + username + " - " + role + ")";
    }
}
