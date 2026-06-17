package com.typetrack.model;

/**
 * Model representing a user/typist with credential security.
 */
public class User {
    private final int userId;
    private final String username;
    private final String passwordHash;
    private final String name;
    private final String role;
    private final String securityQuestion;
    private final String securityAnswerHash;

    public User(int userId, String username, String passwordHash, String name, String role, String securityQuestion, String securityAnswerHash) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.name = name;
        this.role = role != null ? role : "USER";
        this.securityQuestion = securityQuestion;
        this.securityAnswerHash = securityAnswerHash;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public String getSecurityAnswerHash() {
        return securityAnswerHash;
    }
}
