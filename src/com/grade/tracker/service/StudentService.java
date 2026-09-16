package com.grade.tracker.service;

import com.grade.tracker.model.GradeReport;
import com.grade.tracker.model.Student;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Service managing student records using ArrayList and providing statistical analysis.
 */
public class StudentService {
    private final StorageService storageService;
    private final ArrayList<Student> students;

    public StudentService(StorageService storageService) {
        this.storageService = storageService;
        this.students = new ArrayList<>(storageService.loadStudents());
    }

    public synchronized ArrayList<Student> getAllStudents() {
        return new ArrayList<>(students);
    }

    public synchronized int getStudentCount() {
        return students.size();
    }

    /**
     * Adds a new student record to the tracker.
     */
    public synchronized boolean addStudent(Student student) {
        if (student == null || student.getId().isEmpty() || student.getName().isEmpty()) {
            return false;
        }
        if (findStudentById(student.getId()) != null) {
            return false; // Duplicate ID
        }
        students.add(student);
        storageService.saveStudents(students);
        return true;
    }

    /**
     * Updates an existing student's name and scores.
     */
    public synchronized boolean updateStudent(String id, String newName, List<Double> newScores) {
        Student student = findStudentById(id);
        if (student == null) {
            return false;
        }
        if (newName != null && !newName.trim().isEmpty()) {
            student.setName(newName.trim());
        }
        if (newScores != null) {
            student.setScores(newScores);
        }
        storageService.saveStudents(students);
        return true;
    }

    /**
     * Deletes a student by ID.
     */
    public synchronized boolean deleteStudent(String id) {
        Student target = findStudentById(id);
        if (target != null) {
            students.remove(target);
            storageService.saveStudents(students);
            return true;
        }
        return false;
    }

    /**
     * Finds a student by their unique ID.
     */
    public synchronized Student findStudentById(String id) {
        if (id == null) return null;
        for (Student s : students) {
            if (s.getId().equalsIgnoreCase(id.trim())) {
                return s;
            }
        }
        return null;
    }

    /**
     * Searches for students matching an ID or name query.
     */
    public synchronized ArrayList<Student> searchStudents(String query) {
        ArrayList<Student> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            return getAllStudents();
        }
        String q = query.trim().toLowerCase();
        for (Student s : students) {
            if (s.getId().toLowerCase().contains(q) || s.getName().toLowerCase().contains(q)) {
                results.add(s);
            }
        }
        return results;
    }

    /**
     * Adds a score to a student identified by ID.
     */
    public synchronized boolean addScoreToStudent(String id, double score) {
        Student s = findStudentById(id);
        if (s != null && s.addScore(score)) {
            storageService.saveStudents(students);
            return true;
        }
        return false;
    }

    /**
     * Calculates the overall class average across all recorded scores.
     */
    public synchronized double calculateClassAverage() {
        double totalSum = 0.0;
        int totalCount = 0;

        for (Student s : students) {
            for (Double score : s.getScores()) {
                totalSum += score;
                totalCount++;
            }
        }

        if (totalCount == 0) {
            return 0.0;
        }
        double avg = totalSum / totalCount;
        return Math.round(avg * 100.0) / 100.0;
    }

    /**
     * Calculates the highest score recorded across all students.
     */
    public synchronized double getHighestScore() {
        double highest = 0.0;
        boolean foundAny = false;

        for (Student s : students) {
            for (Double score : s.getScores()) {
                if (!foundAny || score > highest) {
                    highest = score;
                    foundAny = true;
                }
            }
        }
        return highest;
    }

    /**
     * Finds all students who achieved the highest score.
     */
    public synchronized List<Student> getTopStudents() {
        List<Student> top = new ArrayList<>();
        double highest = getHighestScore();
        if (highest == 0.0 && calculateClassAverage() == 0.0) {
            return top;
        }

        for (Student s : students) {
            if (s.getScores().contains(highest)) {
                top.add(s);
            }
        }
        return top;
    }

    /**
     * Calculates the lowest score recorded across all students.
     */
    public synchronized double getLowestScore() {
        double lowest = 100.0;
        boolean foundAny = false;

        for (Student s : students) {
            for (Double score : s.getScores()) {
                if (!foundAny || score < lowest) {
                    lowest = score;
                    foundAny = true;
                }
            }
        }
        return foundAny ? lowest : 0.0;
    }

    /**
     * Finds all students who recorded the lowest score.
     */
    public synchronized List<Student> getLowestStudents() {
        List<Student> lowestList = new ArrayList<>();
        double lowest = getLowestScore();

        for (Student s : students) {
            if (s.getScores().contains(lowest)) {
                lowestList.add(s);
            }
        }
        return lowestList;
    }

    /**
     * Generates a comprehensive GradeReport instance containing all aggregate statistics.
     */
    public synchronized GradeReport generateSummaryReport() {
        int totalStudents = students.size();
        int totalScores = 0;
        int passingCount = 0;
        int failingCount = 0;

        Map<String, Integer> distribution = new LinkedHashMap<>();
        distribution.put("A", 0);
        distribution.put("B", 0);
        distribution.put("C", 0);
        distribution.put("D", 0);
        distribution.put("F", 0);

        for (Student s : students) {
            totalScores += s.getScoreCount();
            if (s.getScoreCount() > 0) {
                if (s.isPassing()) {
                    passingCount++;
                } else {
                    failingCount++;
                }
                String letter = s.getLetterGrade();
                if (distribution.containsKey(letter)) {
                    distribution.put(letter, distribution.get(letter) + 1);
                }
            }
        }

        double classAvg = calculateClassAverage();
        double highest = getHighestScore();
        List<Student> top = getTopStudents();
        double lowest = getLowestScore();
        List<Student> bottom = getLowestStudents();

        int gradedStudents = passingCount + failingCount;
        double passRate = gradedStudents > 0 ? ((passingCount * 100.0) / gradedStudents) : 0.0;
        passRate = Math.round(passRate * 10.0) / 10.0;

        return new GradeReport(
                totalStudents,
                totalScores,
                classAvg,
                highest,
                top,
                lowest,
                bottom,
                distribution,
                passingCount,
                failingCount,
                passRate
        );
    }
}
