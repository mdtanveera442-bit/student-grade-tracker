# 🎓 Student Grade Tracker (with Authentication)

A robust, feature-packed Java application designed to input, manage, and analyze student grades. Built with secure authentication, statistical metrics calculations, dynamic `ArrayList` data structures, and both a modern **Swing Desktop GUI** and an **interactive Console CLI**.

---

## 🌟 Key Features

### 1. 🔐 Authentication & Security
- **Secure Password Hashing**: Passwords stored using SHA-256 with cryptographically generated unique salt (`java.security.SecureRandom`).
- **Account Management**: Register new instructor/teacher accounts with customizable roles.
- **Pre-configured Admin Account**:
  - **Username:** `admin`
  - **Password:** `admin123`
- **Active Session Tracking**: User session indicator, role-based titles, and clean logout support.

### 2. 📊 Grade Tracking & Statistical Calculations
- **Dynamic ArrayList Storage**: Seamlessly records any number of scores per student.
- **Student-Level Calculations**:
  - Individual Average Score
  - Highest and Lowest Score per student
  - Academic Letter Grade Mapping (`A` $\ge 90$, `B`: $80-89.9$, `C`: $70-79.9$, `D`: $60-69.9$, `F`: $<60$)
  - Pass/Fail evaluation ($\ge 60.0$)
- **Class-Wide Aggregate Statistics**:
  - Overall Class Average
  - Highest Score in Class & Top Achievers
  - Lowest Score in Class & Lowest Achievers
  - Grade Distribution Counts & Percentages (A, B, C, D, F)
  - Class Passing Rate (%)

### 3. 🖥️ Dual Interfaces (GUI + CLI)
- **Modern Swing GUI**:
  - Metric KPI Stat Cards (Class Average, Highest Score, Lowest Score, Passing Rate)
  - Interactive Table with real-time search filtering by name or ID
  - Color-coded badges for Letter Grades and Pass/Fail statuses
  - Dialogs for Adding/Editing Students and Recording Scores
  - Graphical Summary Report modal with distribution bars and clipboard export
- **Interactive Console CLI**:
  - Formatted tabular output with ANSI colors
  - Full CRUD and reporting menu operations
  - Supports headless terminal environments

### 4. 💾 Persistent Storage
- User accounts and student grade records are persisted locally in `.data/users.csv` and `.data/students.csv`.
- Automatically initialized with sample student data upon first run.

---

## 🚀 Quick Start

### Prerequisites
- **Java SE Development Kit (JDK) 17+** (Tested on Java 26)

### Option A: Run the Modern Desktop GUI
Double-click `run.bat` or execute in terminal:
```bash
java -jar student-grade-tracker.jar
# Or directly with classpath:
java -cp out com.grade.tracker.Main
```

### Option B: Run the Interactive Console CLI
Double-click `run-cli.bat` or execute in terminal:
```bash
java -jar student-grade-tracker.jar --cli
# Or directly with classpath:
java -cp out com.grade.tracker.Main --cli
```

### Recompile & Build
Run `build.bat` or compile with:
```powershell
javac -d out (Get-ChildItem -Recurse -Filter *.java src, test | Select-Object -ExpandProperty FullName)
```

---

## 🧪 Automated Testing

Run the comprehensive unit test suite covering 51 assertions across hashing, authentication, student statistics, and report generation:

```bash
java -cp out com.grade.tracker.StudentTrackerTest
```

---

## 📁 Project Directory Structure

```
student-grade-tracker/
├── .data/
│   ├── students.csv                  # Persisted student grade records
│   └── users.csv                     # Persisted user credentials (salted SHA-256)
├── src/
│   └── com/grade/tracker/
│       ├── Main.java                 # Entry point (auto-detects GUI vs CLI)
│       ├── model/
│       │   ├── Student.java          # Student entity with scores ArrayList & stats
│       │   ├── User.java             # User entity for authentication
│       │   └── GradeReport.java      # Aggregated statistics container
│       ├── service/
│       │   ├── AuthService.java      # Login, registration, and session management
│       │   ├── StudentService.java   # Student CRUD & class-wide calculations
│       │   └── StorageService.java   # Local file persistence
│       ├── util/
│       │   └── SecurityUtil.java     # SHA-256 and secure salt generation
│       └── ui/
│           ├── cli/
│           │   └── ConsoleUI.java    # Interactive terminal interface
│           └── gui/
│               ├── UITheme.java      # Styling, fonts, and palette
│               ├── LoginFrame.java   # Authentication and registration window
│               ├── DashboardFrame.java # Main desktop dashboard
│               ├── StudentDialog.java # Modal for add/edit student
│               └── ReportDialog.java # Summary report modal & distribution
├── test/
│   └── com/grade/tracker/
│       └── StudentTrackerTest.java   # Automated unit test suite (51 tests)
├── build.bat                         # Compile sources & package JAR
├── run.bat                           # Launch Swing GUI
├── run-cli.bat                       # Launch Console CLI
├── student-grade-tracker.jar         # Compiled executable archive
└── README.md                         # Documentation
```
