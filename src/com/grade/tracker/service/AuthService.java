package com.grade.tracker.service;

import com.grade.tracker.model.User;
import com.grade.tracker.util.SecurityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Service managing user authentication, registration, and active user session.
 */
public class AuthService {
    private final StorageService storageService;
    private final List<User> users;
    private User currentUser;

    public AuthService(StorageService storageService) {
        this.storageService = storageService;
        this.users = new ArrayList<>(storageService.loadUsers());
        this.currentUser = null;
    }

    /**
     * Attempts to log in with a username and password.
     *
     * @return User object if authentication is successful, null otherwise.
     */
    public User login(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        String cleanUsername = username.trim();
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(cleanUsername)) {
                if (SecurityUtil.verifyPassword(password, user.getSalt(), user.getPasswordHash())) {
                    this.currentUser = user;
                    return user;
                }
                return null;
            }
        }
        return null;
    }

    /**
     * Registers a new user account.
     *
     * @param username clean alphanumeric username
     * @param password plain text password (minimum 4 characters)
     * @param fullName display name of user
     * @param role     user role (e.g. Teacher, Admin, Assistant)
     * @return true if registration succeeded, false if username exists or validation fails
     */
    public boolean register(String username, String password, String fullName, String role) {
        if (username == null || password == null || fullName == null) {
            return false;
        }
        String cleanUser = username.trim();
        String cleanName = fullName.trim();
        if (cleanUser.length() < 3 || password.length() < 4 || cleanName.isEmpty()) {
            return false;
        }

        // Check if username already exists
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(cleanUser)) {
                return false;
            }
        }

        String salt = SecurityUtil.generateSalt();
        String hash = SecurityUtil.hashPassword(password, salt);
        String assignedRole = (role == null || role.trim().isEmpty()) ? "Teacher" : role.trim();

        User newUser = new User(cleanUser, hash, salt, cleanName, assignedRole);
        users.add(newUser);
        storageService.saveUsers(users);
        return true;
    }

    /**
     * Clears the current session.
     */
    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isAuthenticated() {
        return currentUser != null;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }
}
