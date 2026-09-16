package com.grade.tracker;

import com.grade.tracker.model.GradeReport;
import com.grade.tracker.model.Student;
import com.grade.tracker.model.User;
import com.grade.tracker.service.AuthService;
import com.grade.tracker.service.StudentService;
import com.grade.tracker.service.StorageService;
import com.grade.tracker.util.SecurityUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit and integration tests for Student Grade Tracker.
 */
public class StudentTrackerTest {

    private static int testsRun = 0;
    private static int testsPassed = 0;

    public static void main(String[] args) {
        System.out.println(">>> Starting Student Grade Tracker Test Suite <<<");

        testSecurityAndHashing();
        testStudentCalculations();
        testStudentBoundaryScores();
        testAuthService();
        testStudentServiceCRUDAndStats();
        testSummaryReport();

        System.out.println("\n------------------------------------------------");
        System.out.printf("Test Suite Completed: %d/%d tests passed.%n", testsPassed, testsRun);
        System.out.println("------------------------------------------------");

        if (testsPassed != testsRun) {
            System.err.println("SOME TESTS FAILED!");
            System.exit(1);
        } else {
            System.out.println("ALL TESTS PASSED SUCCESSFULLY! \u2714");
        }
    }

    private static void assertTrue(String testName, boolean condition) {
        testsRun++;
        if (condition) {
            testsPassed++;
            System.out.println("  [PASS] " + testName);
        } else {
            System.err.println("  [FAIL] " + testName);
        }
    }

    private static void assertEquals(String testName, Object expected, Object actual) {
        testsRun++;
        boolean match = (expected == null && actual == null) || (expected != null && expected.equals(actual));
        if (match) {
            testsPassed++;
            System.out.println("  [PASS] " + testName);
        } else {
            System.err.printf("  [FAIL] %s - Expected: %s, Actual: %s%n", testName, expected, actual);
        }
    }

    private static void assertEqualsDouble(String testName, double expected, double actual) {
        testsRun++;
        boolean match = Math.abs(expected - actual) < 0.01;
        if (match) {
            testsPassed++;
            System.out.println("  [PASS] " + testName);
        } else {
            System.err.printf("  [FAIL] %s - Expected: %.2f, Actual: %.2f%n", testName, expected, actual);
        }
    }

    private static void testSecurityAndHashing() {
        System.out.println("\n[1] Testing Security & Password Hashing:");
        String salt = SecurityUtil.generateSalt();
        assertTrue("Salt is non-empty", salt != null && !salt.isEmpty());

        String rawPassword = "securePassword123";
        String hash1 = SecurityUtil.hashPassword(rawPassword, salt);
        String hash2 = SecurityUtil.hashPassword(rawPassword, salt);
        assertEquals("Hashing is deterministic for same salt", hash1, hash2);

        assertTrue("Password verification succeeds for correct password",
                SecurityUtil.verifyPassword(rawPassword, salt, hash1));
        assertTrue("Password verification fails for wrong password",
                !SecurityUtil.verifyPassword("wrongPassword", salt, hash1));
    }

    private static void testStudentCalculations() {
        System.out.println("\n[2] Testing Student Grade Calculations:");
        Student s = new Student("S1", "Alice");
        s.addScore(85.0);
        s.addScore(95.0);
        s.addScore(90.0);

        assertEquals("Score count is 3", 3, s.getScoreCount());
        assertEqualsDouble("Average score is 90.0", 90.0, s.getAverageScore());
        assertEqualsDouble("Highest score is 95.0", 95.0, s.getHighestScore());
        assertEqualsDouble("Lowest score is 85.0", 85.0, s.getLowestScore());
        assertEquals("Letter grade is A", "A", s.getLetterGrade());
        assertTrue("Student is passing", s.isPassing());

        // Test array retrieval
        double[] scoreArr = s.getScoresAsArray();
        assertEquals("Array length matches ArrayList size", 3, scoreArr.length);
        assertEqualsDouble("Array element 0 matches", 85.0, scoreArr[0]);
    }

    private static void testStudentBoundaryScores() {
        System.out.println("\n[3] Testing Student Boundary and Letter Grades:");
        // Test 100 & 90 -> A
        Student sA = new Student("SA", "Test A", List.of(90.0));
        assertEquals("Grade 90 is A", "A", sA.getLetterGrade());

        // Test 89.9 & 80 -> B
        Student sB = new Student("SB", "Test B", List.of(80.0));
        assertEquals("Grade 80 is B", "B", sB.getLetterGrade());

        // Test 70 -> C
        Student sC = new Student("SC", "Test C", List.of(75.5));
        assertEquals("Grade 75.5 is C", "C", sC.getLetterGrade());

        // Test 60 -> D
        Student sD = new Student("SD", "Test D", List.of(60.0));
        assertEquals("Grade 60.0 is D", "D", sD.getLetterGrade());
        assertTrue("Grade 60.0 is passing", sD.isPassing());

        // Test 59.9 -> F
        Student sF = new Student("SF", "Test F", List.of(59.9));
        assertEquals("Grade 59.9 is F", "F", sF.getLetterGrade());
        assertTrue("Grade 59.9 is not passing", !sF.isPassing());

        // Test Invalid scores rejected
        Student sInvalid = new Student("SI", "Invalid");
        assertTrue("Negative score rejected", !sInvalid.addScore(-5.0));
        assertTrue("Score > 100 rejected", !sInvalid.addScore(105.0));
        assertEquals("No scores accepted", 0, sInvalid.getScoreCount());
    }

    private static void testAuthService() {
        System.out.println("\n[4] Testing AuthService:");
        String tempDir = ".test_data_" + System.currentTimeMillis();
        StorageService storage = new StorageService(tempDir);
        AuthService auth = new AuthService(storage);

        // Default admin should authenticate
        User admin = auth.login("admin", "admin123");
        assertTrue("Default admin login successful", admin != null);
        assertEquals("Admin username is 'admin'", "admin", auth.getCurrentUser().getUsername());

        // Failed login
        User failed = auth.login("admin", "incorrect");
        assertTrue("Incorrect password returns null", failed == null);

        // Registration
        boolean registered = auth.register("prof_smith", "pass456", "Professor Smith", "Teacher");
        assertTrue("Registration succeeds for new user", registered);

        User prof = auth.login("prof_smith", "pass456");
        assertTrue("New user login succeeds", prof != null);
        assertEquals("Role is Teacher", "Teacher", prof.getRole());

        // Duplicate registration should fail
        boolean duplicate = auth.register("prof_smith", "differentPass", "Imposter", "Teacher");
        assertTrue("Duplicate registration fails", !duplicate);

        // Logout
        auth.logout();
        assertTrue("User is logged out", !auth.isAuthenticated());

        // Cleanup
        deleteRecursive(new File(tempDir));
    }

    private static void testStudentServiceCRUDAndStats() {
        System.out.println("\n[5] Testing StudentService CRUD & Calculations:");
        String tempDir = ".test_data_student_" + System.currentTimeMillis();
        StorageService storage = new StorageService(tempDir);
        StudentService service = new StudentService(storage);

        // Clear pre-seeded sample data for clean deterministic tests
        List<Student> initial = service.getAllStudents();
        for (Student s : initial) {
            service.deleteStudent(s.getId());
        }
        assertEquals("Student count is 0 after clearing", 0, service.getStudentCount());

        // Add students
        Student s1 = new Student("T1", "John Doe", List.of(90.0, 80.0)); // avg 85.0
        Student s2 = new Student("T2", "Jane Roe", List.of(100.0, 95.0)); // avg 97.5
        Student s3 = new Student("T3", "Jack Low", List.of(50.0, 60.0)); // avg 55.0

        service.addStudent(s1);
        service.addStudent(s2);
        service.addStudent(s3);

        assertEquals("Student count is 3", 3, service.getStudentCount());

        // Total scores: 90 + 80 + 100 + 95 + 50 + 60 = 475 / 6 = 79.17
        assertEqualsDouble("Class average is 79.17", 79.17, service.calculateClassAverage());
        assertEqualsDouble("Class highest is 100.0", 100.0, service.getHighestScore());
        assertEqualsDouble("Class lowest is 50.0", 50.0, service.getLowestScore());

        List<Student> top = service.getTopStudents();
        assertEquals("One top student", 1, top.size());
        assertEquals("Top student is Jane Roe", "Jane Roe", top.get(0).getName());

        List<Student> bottom = service.getLowestStudents();
        assertEquals("One lowest student", 1, bottom.size());
        assertEquals("Lowest student is Jack Low", "Jack Low", bottom.get(0).getName());

        // Update student
        service.updateStudent("T1", "Johnathan Doe", List.of(92.0, 88.0));
        Student updated = service.findStudentById("T1");
        assertEquals("Updated name matches", "Johnathan Doe", updated.getName());
        assertEqualsDouble("Updated average is 90.0", 90.0, updated.getAverageScore());

        // Search
        ArrayList<Student> found = service.searchStudents("jane");
        assertEquals("Search finds Jane", 1, found.size());

        // Delete
        service.deleteStudent("T3");
        assertEquals("Student count is 2 after deletion", 2, service.getStudentCount());

        deleteRecursive(new File(tempDir));
    }

    private static void testSummaryReport() {
        System.out.println("\n[6] Testing Summary Report Generation:");
        String tempDir = ".test_data_report_" + System.currentTimeMillis();
        StorageService storage = new StorageService(tempDir);
        StudentService service = new StudentService(storage);

        // Clear
        for (Student s : service.getAllStudents()) {
            service.deleteStudent(s.getId());
        }

        service.addStudent(new Student("S1", "A-Student", List.of(95.0)));
        service.addStudent(new Student("S2", "B-Student", List.of(85.0)));
        service.addStudent(new Student("S3", "C-Student", List.of(75.0)));
        service.addStudent(new Student("S4", "D-Student", List.of(65.0)));
        service.addStudent(new Student("S5", "F-Student", List.of(55.0)));

        GradeReport report = service.generateSummaryReport();
        assertEquals("Total students is 5", 5, report.getTotalStudents());
        assertEquals("Passing students is 4", 4, report.getPassingCount());
        assertEquals("Failing students is 1", 1, report.getFailingCount());
        assertEqualsDouble("Passing rate is 80.0%", 80.0, report.getPassingRate());
        assertEquals("A count is 1", 1, (int) report.getGradeDistribution().get("A"));
        assertEquals("F count is 1", 1, (int) report.getGradeDistribution().get("F"));

        String summaryText = report.toFormattedSummary();
        assertTrue("Summary contains title", summaryText.contains("CLASS GRADE SUMMARY REPORT"));
        assertTrue("Summary contains passing rate", summaryText.contains("80.0%"));

        deleteRecursive(new File(tempDir));
    }

    private static void deleteRecursive(File f) {
        if (f.isDirectory()) {
            File[] children = f.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursive(child);
                }
            }
        }
        f.delete();
    }
}
