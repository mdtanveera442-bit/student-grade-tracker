package com.grade.tracker.service;

import com.grade.tracker.model.Student;
import com.grade.tracker.model.User;
import com.grade.tracker.util.SecurityUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles persistent storage of students and users using structured local CSV files.
 */
public class StorageService {
    private final File dataDir;
    private final File usersFile;
    private final File studentsFile;

    public StorageService() {
        this.dataDir = new File(".data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        this.usersFile = new File(dataDir, "users.csv");
        this.studentsFile = new File(dataDir, "students.csv");
    }

    public StorageService(String customDirPath) {
        this.dataDir = new File(customDirPath);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        this.usersFile = new File(dataDir, "users.csv");
        this.studentsFile = new File(dataDir, "students.csv");
    }

    /**
     * Loads all users. If user file does not exist, seeds default admin account.
     */
    public List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        if (!usersFile.exists()) {
            // Seed default admin
            String salt = SecurityUtil.generateSalt();
            String hash = SecurityUtil.hashPassword("admin123", salt);
            User defaultAdmin = new User("admin", hash, salt, "Administrator", "Teacher");
            users.add(defaultAdmin);
            saveUsers(users);
            return users;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(usersFile, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split(";", -1);
                if (parts.length >= 5) {
                    users.add(new User(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim(), parts[4].trim()));
                }
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not read users file: " + e.getMessage());
        }

        if (users.isEmpty()) {
            String salt = SecurityUtil.generateSalt();
            String hash = SecurityUtil.hashPassword("admin123", salt);
            users.add(new User("admin", hash, salt, "Administrator", "Teacher"));
            saveUsers(users);
        }

        return users;
    }

    /**
     * Saves user accounts to disk.
     */
    public synchronized boolean saveUsers(List<User> users) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(usersFile, StandardCharsets.UTF_8))) {
            writer.write("# username;passwordHash;salt;fullName;role\n");
            for (User u : users) {
                writer.write(String.format("%s;%s;%s;%s;%s\n",
                        escape(u.getUsername()),
                        escape(u.getPasswordHash()),
                        escape(u.getSalt()),
                        escape(u.getFullName()),
                        escape(u.getRole())));
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
            return false;
        }
    }

    /**
     * Loads all students from disk. If file is absent, creates initial sample records.
     */
    public List<Student> loadStudents() {
        List<Student> students = new ArrayList<>();
        if (!studentsFile.exists()) {
            // Seed sample students
            students.add(new Student("STU-101", "Emma Watson", List.of(92.0, 88.5, 95.0, 91.0)));
            students.add(new Student("STU-102", "Liam Johnson", List.of(78.0, 82.5, 75.0, 80.0)));
            students.add(new Student("STU-103", "Sophia Miller", List.of(96.0, 94.0, 98.5, 92.0)));
            students.add(new Student("STU-104", "Noah Davis", List.of(62.0, 58.0, 65.5, 70.0)));
            students.add(new Student("STU-105", "Ava Martinez", List.of(54.0, 48.5, 59.0, 52.0)));
            saveStudents(students);
            return students;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(studentsFile, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split(";", -1);
                if (parts.length >= 2) {
                    String id = unescape(parts[0].trim());
                    String name = unescape(parts[1].trim());
                    Student s = new Student(id, name);
                    if (parts.length >= 3 && !parts[2].trim().isEmpty()) {
                        String[] scoreTokens = parts[2].trim().split(",");
                        for (String token : scoreTokens) {
                            try {
                                if (!token.trim().isEmpty()) {
                                    s.addScore(Double.parseDouble(token.trim()));
                                }
                            } catch (NumberFormatException ignored) {
                            }
                        }
                    }
                    students.add(s);
                }
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not read students file: " + e.getMessage());
        }

        return students;
    }

    /**
     * Saves students to disk.
     */
    public synchronized boolean saveStudents(List<Student> students) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(studentsFile, StandardCharsets.UTF_8))) {
            writer.write("# id;name;scores(comma-separated)\n");
            for (Student s : students) {
                StringBuilder scoresBuilder = new StringBuilder();
                List<Double> scoreList = s.getScores();
                for (int i = 0; i < scoreList.size(); i++) {
                    scoresBuilder.append(scoreList.get(i));
                    if (i < scoreList.size() - 1) {
                        scoresBuilder.append(",");
                    }
                }
                writer.write(String.format("%s;%s;%s\n",
                        escape(s.getId()),
                        escape(s.getName()),
                        scoresBuilder.toString()));
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error saving students: " + e.getMessage());
            return false;
        }
    }

    private String escape(String s) {
        return s == null ? "" : s.replace(";", ",");
    }

    private String unescape(String s) {
        return s == null ? "" : s;
    }
}
