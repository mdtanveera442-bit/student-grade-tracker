package com.grade.tracker.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a student with identification, name, and a list of numeric scores.
 * Demonstrates ArrayList usage for storing and calculating dynamic student scores.
 */
public class Student {
    private String id;
    private String name;
    private ArrayList<Double> scores;

    public Student(String id, String name) {
        this.id = id != null ? id.trim() : "";
        this.name = name != null ? name.trim() : "";
        this.scores = new ArrayList<>();
    }

    public Student(String id, String name, List<Double> initialScores) {
        this(id, name);
        if (initialScores != null) {
            for (Double score : initialScores) {
                addScore(score);
            }
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id != null ? id.trim() : "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name != null ? name.trim() : "";
    }

    /**
     * Returns a copy of scores ArrayList to protect internal state.
     */
    public ArrayList<Double> getScores() {
        return new ArrayList<>(scores);
    }

    /**
     * Returns scores as a primitive double array.
     */
    public double[] getScoresAsArray() {
        double[] arr = new double[scores.size()];
        for (int i = 0; i < scores.size(); i++) {
            arr[i] = scores.get(i);
        }
        return arr;
    }

    /**
     * Sets the scores list.
     */
    public void setScores(List<Double> newScores) {
        this.scores.clear();
        if (newScores != null) {
            for (Double score : newScores) {
                addScore(score);
            }
        }
    }

    /**
     * Adds a single valid score (0.0 to 100.0).
     */
    public boolean addScore(double score) {
        if (score < 0.0 || score > 100.0) {
            return false;
        }
        scores.add(Math.round(score * 100.0) / 100.0);
        return true;
    }

    /**
     * Removes a score at a specific index.
     */
    public boolean removeScore(int index) {
        if (index >= 0 && index < scores.size()) {
            scores.remove(index);
            return true;
        }
        return false;
    }

    public int getScoreCount() {
        return scores.size();
    }

    /**
     * Calculates the average score for this student.
     * Returns 0.0 if no scores exist.
     */
    public double getAverageScore() {
        if (scores.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        for (Double score : scores) {
            sum += score;
        }
        double avg = sum / scores.size();
        return Math.round(avg * 100.0) / 100.0;
    }

    /**
     * Finds the highest score among this student's grades.
     * Returns 0.0 if no scores exist.
     */
    public double getHighestScore() {
        if (scores.isEmpty()) {
            return 0.0;
        }
        return Collections.max(scores);
    }

    /**
     * Finds the lowest score among this student's grades.
     * Returns 0.0 if no scores exist.
     */
    public double getLowestScore() {
        if (scores.isEmpty()) {
            return 0.0;
        }
        return Collections.min(scores);
    }

    /**
     * Maps the student's average score to an academic letter grade.
     */
    public String getLetterGrade() {
        if (scores.isEmpty()) {
            return "N/A";
        }
        double avg = getAverageScore();
        if (avg >= 90.0) return "A";
        if (avg >= 80.0) return "B";
        if (avg >= 70.0) return "C";
        if (avg >= 60.0) return "D";
        return "F";
    }

    /**
     * Returns true if the student is passing (average >= 60.0).
     */
    public boolean isPassing() {
        if (scores.isEmpty()) {
            return false;
        }
        return getAverageScore() >= 60.0;
    }

    /**
     * Returns a formatted comma-separated representation of scores.
     */
    public String getFormattedScores() {
        if (scores.isEmpty()) {
            return "No scores";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < scores.size(); i++) {
            sb.append(String.format("%.1f", scores.get(i)));
            if (i < scores.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("%s - %s | Scores: [%s] | Avg: %.2f | Grade: %s",
                id, name, getFormattedScores(), getAverageScore(), getLetterGrade());
    }
}
