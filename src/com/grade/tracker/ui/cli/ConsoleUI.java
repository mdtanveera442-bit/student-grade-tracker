package com.grade.tracker.ui.cli;

import com.grade.tracker.model.GradeReport;
import com.grade.tracker.model.Student;
import com.grade.tracker.model.User;
import com.grade.tracker.service.AuthService;
import com.grade.tracker.service.StudentService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Interactive Console-based User Interface for Student Grade Tracker with Authentication.
 */
public class ConsoleUI {
    private final AuthService authService;
    private final StudentService studentService;
    private final Scanner scanner;

    // ANSI Color constants for enhanced terminal visuals
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String CYAN = "\u001B[36m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";

    public ConsoleUI(AuthService authService, StudentService studentService) {
        this.authService = authService;
        this.studentService = studentService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        printBanner();
        boolean running = true;

        while (running) {
            if (!authService.isAuthenticated()) {
                running = handleAuthMenu();
            } else {
                running = handleMainMenu();
            }
        }

        System.out.println(GREEN + "\nThank you for using Student Grade Tracker. Goodbye!" + RESET);
    }

    private void printBanner() {
        System.out.println(CYAN + BOLD);
        System.out.println("========================================================================");
        System.out.println("           STUDENT GRADE TRACKER - AUTHENTICATED SYSTEM                  ");
        System.out.println("========================================================================");
        System.out.println(RESET);
    }

    private boolean handleAuthMenu() {
        System.out.println(BOLD + "\n[ AUTHENTICATION MENU ]" + RESET);
        System.out.println("1. Login");
        System.out.println("2. Register New Account");
        System.out.println("3. Quick Login as Demo Admin (admin / admin123)");
        System.out.println("0. Exit");
        System.out.print(CYAN + "Choose an option: " + RESET);

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                login();
                break;
            case "2":
                register();
                break;
            case "3":
                quickAdminLogin();
                break;
            case "0":
                return false;
            default:
                System.out.println(RED + "Invalid selection. Please try again." + RESET);
        }
        return true;
    }

    private void quickAdminLogin() {
        User user = authService.login("admin", "admin123");
        if (user != null) {
            System.out.println(GREEN + "\u2714 Logged in successfully as Demo Administrator!" + RESET);
        } else {
            System.out.println(RED + "Failed to login as admin." + RESET);
        }
    }

    private void login() {
        System.out.println(BOLD + "\n--- Account Login ---" + RESET);
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        User user = authService.login(username, password);
        if (user != null) {
            System.out.println(GREEN + "\u2714 Welcome back, " + user.getFullName() + " (" + user.getRole() + ")!" + RESET);
        } else {
            System.out.println(RED + "\u2718 Invalid username or password." + RESET);
        }
    }

    private void register() {
        System.out.println(BOLD + "\n--- Register New Account ---" + RESET);
        System.out.print("Choose username (min 3 chars): ");
        String username = scanner.nextLine().trim();
        System.out.print("Choose password (min 4 chars): ");
        String password = scanner.nextLine().trim();
        System.out.print("Enter your full name: ");
        String fullName = scanner.nextLine().trim();
        System.out.print("Role (e.g. Teacher, Instructor, Admin) [default: Teacher]: ");
        String role = scanner.nextLine().trim();
        if (role.isEmpty()) role = "Teacher";

        boolean success = authService.register(username, password, fullName, role);
        if (success) {
            System.out.println(GREEN + "\u2714 Account registered successfully! You can now log in." + RESET);
        } else {
            System.out.println(RED + "\u2718 Registration failed. Username may already exist or inputs were invalid." + RESET);
        }
    }

    private boolean handleMainMenu() {
        User user = authService.getCurrentUser();
        System.out.println(BOLD + "\n------------------------------------------------------------------------");
        System.out.printf(" MAIN DASHBOARD | Logged in as: %s%s (%s)%s%n",
                GREEN, user.getFullName(), user.getRole(), RESET);
        System.out.println("------------------------------------------------------------------------" + RESET);
        System.out.println("1. View All Students & Grades");
        System.out.println("2. Add New Student");
        System.out.println("3. Add/Append Score to Student");
        System.out.println("4. Edit Student Details / Scores");
        System.out.println("5. Remove Student");
        System.out.println("6. Search Students");
        System.out.println("7. Display Quick Class Statistics");
        System.out.println("8. Display Full Summary Report");
        System.out.println("9. Logout");
        System.out.println("0. Exit Application");
        System.out.print(CYAN + "Choose an option: " + RESET);

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                viewAllStudents();
                break;
            case "2":
                addNewStudent();
                break;
            case "3":
                addScoreToStudent();
                break;
            case "4":
                editStudent();
                break;
            case "5":
                removeStudent();
                break;
            case "6":
                searchStudents();
                break;
            case "7":
                displayQuickStats();
                break;
            case "8":
                displayFullReport();
                break;
            case "9":
                authService.logout();
                System.out.println(YELLOW + "Logged out successfully." + RESET);
                break;
            case "0":
                return false;
            default:
                System.out.println(RED + "Invalid option. Please try again." + RESET);
        }
        return true;
    }

    private void viewAllStudents() {
        ArrayList<Student> students = studentService.getAllStudents();
        printStudentTable(students);
    }

    private void printStudentTable(List<Student> students) {
        if (students.isEmpty()) {
            System.out.println(YELLOW + "\nNo students currently recorded in the system." + RESET);
            return;
        }

        System.out.println(BOLD + "\n=====================================================================================================");
        System.out.printf("%-10s %-22s %-32s %-8s %-7s %-8s%n",
                "ID", "NAME", "SCORES", "AVG", "GRADE", "STATUS");
        System.out.println("-----------------------------------------------------------------------------------------------------" + RESET);

        for (Student s : students) {
            String statusColor = s.isPassing() ? GREEN : RED;
            String gradeColor = switch (s.getLetterGrade()) {
                case "A" -> GREEN;
                case "B" -> CYAN;
                case "C" -> BLUE;
                case "D" -> YELLOW;
                default -> RED;
            };

            String statusText = s.getScoreCount() == 0 ? "NO DATA" : (s.isPassing() ? "PASS" : "FAIL");

            System.out.printf("%-10s %-22s %-32s %-8.2f %s%-7s%s %s%-8s%s%n",
                    s.getId(),
                    truncate(s.getName(), 20),
                    truncate(s.getFormattedScores(), 30),
                    s.getAverageScore(),
                    gradeColor, s.getLetterGrade(), RESET,
                    statusColor, statusText, RESET);
        }
        System.out.println("=====================================================================================================");
        System.out.println("Total Students: " + students.size());
    }

    private void addNewStudent() {
        System.out.println(BOLD + "\n--- Add New Student ---" + RESET);
        System.out.print("Enter Student ID (e.g., STU-106): ");
        String id = scanner.nextLine().trim();
        if (id.isEmpty()) {
            System.out.println(RED + "Student ID cannot be empty." + RESET);
            return;
        }
        if (studentService.findStudentById(id) != null) {
            System.out.println(RED + "A student with ID '" + id + "' already exists." + RESET);
            return;
        }

        System.out.print("Enter Student Name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println(RED + "Student name cannot be empty." + RESET);
            return;
        }

        System.out.print("Enter initial scores separated by commas or spaces (or press Enter to skip): ");
        String scoresLine = scanner.nextLine().trim();
        Student student = new Student(id, name);
        if (!scoresLine.isEmpty()) {
            parseAndAddScores(student, scoresLine);
        }

        if (studentService.addStudent(student)) {
            System.out.println(GREEN + "\u2714 Student added successfully!" + RESET);
        } else {
            System.out.println(RED + "\u2718 Failed to add student." + RESET);
        }
    }

    private void addScoreToStudent() {
        System.out.println(BOLD + "\n--- Add Score to Student ---" + RESET);
        System.out.print("Enter Student ID: ");
        String id = scanner.nextLine().trim();
        Student student = studentService.findStudentById(id);
        if (student == null) {
            System.out.println(RED + "Student with ID '" + id + "' not found." + RESET);
            return;
        }

        System.out.printf("Student: %s (Current scores: [%s])%n", student.getName(), student.getFormattedScores());
        System.out.print("Enter new score (0.0 - 100.0): ");
        try {
            double score = Double.parseDouble(scanner.nextLine().trim());
            if (studentService.addScoreToStudent(id, score)) {
                System.out.println(GREEN + "\u2714 Score added! New Average: " +
                        studentService.findStudentById(id).getAverageScore() + RESET);
            } else {
                System.out.println(RED + "\u2718 Score must be between 0.0 and 100.0." + RESET);
            }
        } catch (NumberFormatException e) {
            System.out.println(RED + "Invalid number format." + RESET);
        }
    }

    private void editStudent() {
        System.out.println(BOLD + "\n--- Edit Student Details ---" + RESET);
        System.out.print("Enter Student ID to edit: ");
        String id = scanner.nextLine().trim();
        Student student = studentService.findStudentById(id);
        if (student == null) {
            System.out.println(RED + "Student not found." + RESET);
            return;
        }

        System.out.printf("Current Name: %s (Press Enter to keep unchanged): ", student.getName());
        String newName = scanner.nextLine().trim();
        if (newName.isEmpty()) {
            newName = student.getName();
        }

        System.out.printf("Current Scores: [%s]%n", student.getFormattedScores());
        System.out.print("Enter replacement scores (comma-separated, or Enter to keep unchanged): ");
        String scoresLine = scanner.nextLine().trim();

        List<Double> newScores = null;
        if (!scoresLine.isEmpty()) {
            newScores = new ArrayList<>();
            String[] tokens = scoresLine.split("[,\\s]+");
            for (String t : tokens) {
                try {
                    double val = Double.parseDouble(t);
                    if (val >= 0.0 && val <= 100.0) {
                        newScores.add(val);
                    } else {
                        System.out.println(YELLOW + "Skipping out-of-range score: " + val + RESET);
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }

        if (studentService.updateStudent(id, newName, newScores)) {
            System.out.println(GREEN + "\u2714 Student updated successfully!" + RESET);
        } else {
            System.out.println(RED + "\u2718 Failed to update student." + RESET);
        }
    }

    private void removeStudent() {
        System.out.println(BOLD + "\n--- Remove Student ---" + RESET);
        System.out.print("Enter Student ID to delete: ");
        String id = scanner.nextLine().trim();
        Student student = studentService.findStudentById(id);
        if (student == null) {
            System.out.println(RED + "Student not found." + RESET);
            return;
        }

        System.out.print(RED + "Are you sure you want to delete '" + student.getName() + "' (y/N)? " + RESET);
        String confirm = scanner.nextLine().trim();
        if (confirm.equalsIgnoreCase("y") || confirm.equalsIgnoreCase("yes")) {
            if (studentService.deleteStudent(id)) {
                System.out.println(GREEN + "\u2714 Student deleted." + RESET);
            } else {
                System.out.println(RED + "\u2718 Failed to delete student." + RESET);
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private void searchStudents() {
        System.out.println(BOLD + "\n--- Search Students ---" + RESET);
        System.out.print("Enter search keyword (ID or name): ");
        String query = scanner.nextLine().trim();
        ArrayList<Student> results = studentService.searchStudents(query);
        System.out.printf("Found %d matching student(s):%n", results.size());
        printStudentTable(results);
    }

    private void displayQuickStats() {
        System.out.println(BOLD + CYAN + "\n==========================================");
        System.out.println("       CLASS QUICK STATISTICS             ");
        System.out.println("==========================================" + RESET);
        System.out.printf("Class Average Score : %.2f%n", studentService.calculateClassAverage());
        System.out.printf("Highest Score       : %.2f%n", studentService.getHighestScore());
        List<Student> top = studentService.getTopStudents();
        if (!top.isEmpty()) {
            System.out.print("Top Achiever(s)     : ");
            for (Student s : top) System.out.print(s.getName() + " (" + s.getId() + ") ");
            System.out.println();
        }
        System.out.printf("Lowest Score        : %.2f%n", studentService.getLowestScore());
        List<Student> low = studentService.getLowestStudents();
        if (!low.isEmpty()) {
            System.out.print("Lowest Achiever(s)  : ");
            for (Student s : low) System.out.print(s.getName() + " (" + s.getId() + ") ");
            System.out.println();
        }
        System.out.println(CYAN + "==========================================" + RESET);
    }

    private void displayFullReport() {
        GradeReport report = studentService.generateSummaryReport();
        System.out.println("\n" + report.toFormattedSummary());
    }

    private void parseAndAddScores(Student student, String input) {
        String[] tokens = input.split("[,\\s]+");
        for (String t : tokens) {
            try {
                double score = Double.parseDouble(t);
                if (!student.addScore(score)) {
                    System.out.println(YELLOW + "Ignoring invalid score: " + t + " (must be 0-100)" + RESET);
                }
            } catch (NumberFormatException ignored) {
            }
        }
    }

    private String truncate(String s, int maxLen) {
        if (s == null) return "";
        if (s.length() <= maxLen) return s;
        return s.substring(0, maxLen - 3) + "...";
    }
}
