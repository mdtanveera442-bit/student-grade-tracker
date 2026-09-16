package com.grade.tracker.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Encapsulates aggregated statistical calculations and summary report data for a class.
 */
public class GradeReport {
    private int totalStudents;
    private int totalScoresRecorded;
    private double classAverage;
    private double highestScore;
    private List<Student> topStudents;
    private double lowestScore;
    private List<Student> lowestStudents;
    private Map<String, Integer> gradeDistribution;
    private int passingCount;
    private int failingCount;
    private double passingRate;

    public GradeReport(int totalStudents,
                       int totalScoresRecorded,
                       double classAverage,
                       double highestScore,
                       List<Student> topStudents,
                       double lowestScore,
                       List<Student> lowestStudents,
                       Map<String, Integer> gradeDistribution,
                       int passingCount,
                       int failingCount,
                       double passingRate) {
        this.totalStudents = totalStudents;
        this.totalScoresRecorded = totalScoresRecorded;
        this.classAverage = classAverage;
        this.highestScore = highestScore;
        this.topStudents = topStudents != null ? topStudents : new ArrayList<>();
        this.lowestScore = lowestScore;
        this.lowestStudents = lowestStudents != null ? lowestStudents : new ArrayList<>();
        this.gradeDistribution = gradeDistribution;
        this.passingCount = passingCount;
        this.failingCount = failingCount;
        this.passingRate = passingRate;
    }

    public int getTotalStudents() {
        return totalStudents;
    }

    public int getTotalScoresRecorded() {
        return totalScoresRecorded;
    }

    public double getClassAverage() {
        return classAverage;
    }

    public double getHighestScore() {
        return highestScore;
    }

    public List<Student> getTopStudents() {
        return topStudents;
    }

    public double getLowestScore() {
        return lowestScore;
    }

    public List<Student> getLowestStudents() {
        return lowestStudents;
    }

    public Map<String, Integer> getGradeDistribution() {
        return gradeDistribution;
    }

    public int getPassingCount() {
        return passingCount;
    }

    public int getFailingCount() {
        return failingCount;
    }

    public double getPassingRate() {
        return passingRate;
    }

    /**
     * Generates a formatted ASCII summary report.
     */
    public String toFormattedSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("=========================================================\n");
        sb.append("                 CLASS GRADE SUMMARY REPORT               \n");
        sb.append("=========================================================\n");
        sb.append(String.format("Total Students Registered : %d\n", totalStudents));
        sb.append(String.format("Total Scores Recorded     : %d\n", totalScoresRecorded));
        sb.append(String.format("Overall Class Average     : %.2f\n", classAverage));
        sb.append("---------------------------------------------------------\n");

        sb.append(String.format("Highest Score             : %.2f\n", highestScore));
        sb.append("Top Performer(s)          : ");
        if (topStudents.isEmpty()) {
            sb.append("None\n");
        } else {
            for (int i = 0; i < topStudents.size(); i++) {
                Student s = topStudents.get(i);
                sb.append(s.getName()).append(" (ID: ").append(s.getId()).append(")");
                if (i < topStudents.size() - 1) sb.append(", ");
            }
            sb.append("\n");
        }

        sb.append(String.format("Lowest Score              : %.2f\n", lowestScore));
        sb.append("Lowest Performer(s)       : ");
        if (lowestStudents.isEmpty()) {
            sb.append("None\n");
        } else {
            for (int i = 0; i < lowestStudents.size(); i++) {
                Student s = lowestStudents.get(i);
                sb.append(s.getName()).append(" (ID: ").append(s.getId()).append(")");
                if (i < lowestStudents.size() - 1) sb.append(", ");
            }
            sb.append("\n");
        }

        sb.append("---------------------------------------------------------\n");
        sb.append("Passing Students (>= 60)  : ").append(passingCount)
                .append(String.format(" (%.1f%%)\n", passingRate));
        sb.append("Failing Students (< 60)   : ").append(failingCount)
                .append(String.format(" (%.1f%%)\n", (100.0 - passingRate)));

        sb.append("---------------------------------------------------------\n");
        sb.append("Grade Distribution:\n");
        String[] grades = {"A", "B", "C", "D", "F"};
        for (String g : grades) {
            int count = gradeDistribution.getOrDefault(g, 0);
            int barLen = totalStudents > 0 ? (count * 20 / totalStudents) : 0;
            String bar = "█".repeat(barLen);
            double pct = totalStudents > 0 ? (count * 100.0 / totalStudents) : 0.0;
            sb.append(String.format("  Grade %s: %2d students (%5.1f%%) %s\n", g, count, pct, bar));
        }
        sb.append("=========================================================\n");
        return sb.toString();
    }
}
