package com.grade.tracker.ui.gui;

import com.grade.tracker.model.GradeReport;
import com.grade.tracker.model.Student;
import com.grade.tracker.model.User;
import com.grade.tracker.service.AuthService;
import com.grade.tracker.service.StudentService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Main dashboard application frame with original proportions, crisp vector logo icon,
 * balanced padding, realistic tactile buttons, and clean student analytics.
 */
public class DashboardFrame extends JFrame {
    private final AuthService authService;
    private final StudentService studentService;

    // Stat card labels
    private JLabel avgScoreValLabel;
    private JLabel avgScoreSubLabel;
    private JLabel highestValLabel;
    private JLabel highestSubLabel;
    private JLabel lowestValLabel;
    private JLabel lowestSubLabel;
    private JLabel passRateValLabel;
    private JLabel passRateSubLabel;

    // Table & Model
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JLabel statusLabel;

    private JButton editBtn;
    private JButton deleteBtn;
    private JButton addScoreBtn;

    public DashboardFrame(AuthService authService, StudentService studentService) {
        super("Student Grade Tracker - Dashboard");
        this.authService = authService;
        this.studentService = studentService;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1040, 700);
        setMinimumSize(new Dimension(880, 580));
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.BACKGROUND);
        setLayout(new BorderLayout());

        // 1. Top Navigation Bar (compact, original-sized logo)
        add(createTopNavBar(), BorderLayout.NORTH);

        // Center Container (Stat Cards + Toolbar + Table)
        JPanel centerPanel = new JPanel(new BorderLayout(0, 12));
        centerPanel.setBackground(UITheme.BACKGROUND);
        centerPanel.setBorder(new EmptyBorder(12, 18, 12, 18));

        // 2. Stat Cards (Realistic Elevated Cards with balanced padding)
        centerPanel.add(createStatCardsPanel(), BorderLayout.NORTH);

        // 3. Main Data Container (Realistic Card wrapping Toolbar + Table)
        RealisticCard tableContainer = new RealisticCard(12, UITheme.SURFACE);
        tableContainer.setLayout(new BorderLayout(0, 8));
        tableContainer.setBorder(new EmptyBorder(10, 12, 10, 12));

        tableContainer.add(createToolbarPanel(), BorderLayout.NORTH);
        tableContainer.add(createTableScrollPane(), BorderLayout.CENTER);

        centerPanel.add(tableContainer, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // 4. Status Bar
        add(createStatusBar(), BorderLayout.SOUTH);

        // Initial load
        refreshData();
    }

    private JPanel createTopNavBar() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(UITheme.SURFACE);
        nav.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER),
                new EmptyBorder(8, 18, 8, 18)
        ));

        // Brand with Original Sized Vector Logo
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        brandPanel.setOpaque(false);

        JLabel logoIcon = new JLabel(new GraduationCapIcon(20, 20));
        JLabel titleLabel = new JLabel("Student Grade Tracker");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);

        brandPanel.add(logoIcon);
        brandPanel.add(titleLabel);
        nav.add(brandPanel, BorderLayout.WEST);

        // User info & Logout
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userPanel.setOpaque(false);

        User curUser = authService.getCurrentUser();
        String userTitle = curUser != null ? curUser.getFullName() + " (" + curUser.getRole() + ")" : "User";
        JLabel userBadge = new JLabel(userTitle);
        userBadge.setFont(UITheme.FONT_REGULAR);
        userBadge.setForeground(UITheme.TEXT_MUTED);

        JButton logoutBtn = UITheme.createSecondaryButton("Sign Out");
        logoutBtn.addActionListener(e -> handleLogout());

        userPanel.add(userBadge);
        userPanel.add(logoutBtn);
        nav.add(userPanel, BorderLayout.EAST);

        return nav;
    }

    private JPanel createStatCardsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 12, 0));
        panel.setOpaque(false);

        // Class Average Card
        JPanel card1 = createStatCard("Class Average", "0.00", "Class letter grade", UITheme.PRIMARY);
        avgScoreValLabel = (JLabel) card1.getClientProperty("val");
        avgScoreSubLabel = (JLabel) card1.getClientProperty("sub");

        // Highest Score Card
        JPanel card2 = createStatCard("Highest Score", "0.00", "Top achiever", UITheme.SUCCESS);
        highestValLabel = (JLabel) card2.getClientProperty("val");
        highestSubLabel = (JLabel) card2.getClientProperty("sub");

        // Lowest Score Card
        JPanel card3 = createStatCard("Lowest Score", "0.00", "Lowest achiever", UITheme.DANGER);
        lowestValLabel = (JLabel) card3.getClientProperty("val");
        lowestSubLabel = (JLabel) card3.getClientProperty("sub");

        // Passing Rate Card
        JPanel card4 = createStatCard("Passing Rate", "0.0%", "0 pass / 0 fail", UITheme.PURPLE);
        passRateValLabel = (JLabel) card4.getClientProperty("val");
        passRateSubLabel = (JLabel) card4.getClientProperty("sub");

        panel.add(card1);
        panel.add(card2);
        panel.add(card3);
        panel.add(card4);

        return panel;
    }

    private JPanel createStatCard(String title, String val, String sub, Color valColor) {
        RealisticCard card = new RealisticCard(10, UITheme.SURFACE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(10, 12, 10, 12));

        JLabel tLbl = new JLabel(title);
        tLbl.setFont(UITheme.FONT_SMALL);
        tLbl.setForeground(UITheme.TEXT_MUTED);

        JLabel vLbl = new JLabel(val);
        vLbl.setFont(UITheme.FONT_STAT_VAL);
        vLbl.setForeground(valColor);

        JLabel sLbl = new JLabel(sub);
        sLbl.setFont(UITheme.FONT_SMALL);
        sLbl.setForeground(UITheme.TEXT_MUTED);

        card.add(tLbl);
        card.add(Box.createVerticalStrut(2));
        card.add(vLbl);
        card.add(Box.createVerticalStrut(2));
        card.add(sLbl);

        card.putClientProperty("val", vLbl);
        card.putClientProperty("sub", sLbl);

        return card;
    }

    private JPanel createToolbarPanel() {
        JPanel toolbar = new JPanel(new BorderLayout(8, 0));
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(2, 2, 4, 2));

        // Search Field
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        searchPanel.setOpaque(false);
        searchField = UITheme.createTextField("Search student by name or ID...", 18);
        searchField.setToolTipText("Filter students by name or ID");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterTable(); }
            public void removeUpdate(DocumentEvent e) { filterTable(); }
            public void changedUpdate(DocumentEvent e) { filterTable(); }
        });
        searchPanel.add(searchField);
        toolbar.add(searchPanel, BorderLayout.WEST);

        // Action Buttons with Balanced Padding and Realistic Click Feel
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        actions.setOpaque(false);

        JButton addBtn = UITheme.createPrimaryButton("+ Add Student");
        addBtn.addActionListener(e -> handleAddStudent());

        addScoreBtn = UITheme.createAccentButton("+ Add Score");
        addScoreBtn.setEnabled(false);
        addScoreBtn.addActionListener(e -> handleAddScore());

        editBtn = UITheme.createSecondaryButton("Edit");
        editBtn.setEnabled(false);
        editBtn.addActionListener(e -> handleEditStudent());

        deleteBtn = UITheme.createDangerButton("Delete");
        deleteBtn.setEnabled(false);
        deleteBtn.addActionListener(e -> handleDeleteStudent());

        JButton reportBtn = UITheme.createSuccessButton("Summary Report");
        reportBtn.addActionListener(e -> handleViewReport());

        JButton refreshBtn = UITheme.createSecondaryButton("Refresh");
        refreshBtn.addActionListener(e -> refreshData());

        actions.add(addBtn);
        actions.add(addScoreBtn);
        actions.add(editBtn);
        actions.add(deleteBtn);
        actions.add(reportBtn);
        actions.add(refreshBtn);

        toolbar.add(actions, BorderLayout.EAST);
        return toolbar;
    }

    private JScrollPane createTableScrollPane() {
        String[] columns = {"Student ID", "Full Name", "Grades / Scores", "Average", "Letter Grade", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        studentTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                } else {
                    c.setBackground(new Color(239, 246, 255));
                }
                return c;
            }
        };

        studentTable.setFont(UITheme.FONT_REGULAR);
        studentTable.setRowHeight(32); // Balanced standard row height
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.setGridColor(new Color(241, 245, 249));
        studentTable.setShowVerticalLines(false);
        studentTable.setShowHorizontalLines(true);
        studentTable.setSelectionBackground(new Color(239, 246, 255));

        JTableHeader header = studentTable.getTableHeader();
        header.setFont(UITheme.FONT_BOLD);
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(UITheme.TEXT_PRIMARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 32));

        // Column widths
        studentTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        studentTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        studentTable.getColumnModel().getColumn(2).setPreferredWidth(260);
        studentTable.getColumnModel().getColumn(3).setPreferredWidth(85);
        studentTable.getColumnModel().getColumn(4).setPreferredWidth(95);
        studentTable.getColumnModel().getColumn(5).setPreferredWidth(100);

        // ID Column Left Padding & Bold Font
        studentTable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(UITheme.FONT_BOLD);
                setForeground(UITheme.PRIMARY);
                setBorder(new EmptyBorder(0, 8, 0, 0));
                return this;
            }
        });

        // Name Column Left Padding
        studentTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(UITheme.FONT_REGULAR);
                setForeground(UITheme.TEXT_PRIMARY);
                setBorder(new EmptyBorder(0, 8, 0, 0));
                return this;
            }
        });

        // Scores Column Left Padding
        studentTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(new Font("Segoe UI", Font.PLAIN, 12));
                setForeground(UITheme.TEXT_MUTED);
                setBorder(new EmptyBorder(0, 8, 0, 0));
                return this;
            }
        });

        // Average Column - Centered & Bold
        studentTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(UITheme.FONT_BOLD);
                setHorizontalAlignment(SwingConstants.CENTER);
                setForeground(UITheme.TEXT_PRIMARY);
                return this;
            }
        });

        // Letter Grade Column - Pill Badge
        studentTable.getColumnModel().getColumn(4).setCellRenderer(
                new StatusBadgeRenderer(StatusBadgeRenderer.Mode.LETTER_GRADE)
        );

        // Status Column - Pill Badge (PASS / FAIL)
        studentTable.getColumnModel().getColumn(5).setCellRenderer(
                new StatusBadgeRenderer(StatusBadgeRenderer.Mode.STATUS)
        );

        // Selection listener
        studentTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = studentTable.getSelectedRow() != -1;
            editBtn.setEnabled(hasSelection);
            deleteBtn.setEnabled(hasSelection);
            addScoreBtn.setEnabled(hasSelection);
        });

        // Double-click row to edit
        studentTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && studentTable.getSelectedRow() != -1) {
                    handleEditStudent();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER));
        scrollPane.getViewport().setBackground(UITheme.SURFACE);
        return scrollPane;
    }

    private JPanel createStatusBar() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(UITheme.SURFACE);
        statusPanel.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER),
                new EmptyBorder(6, 18, 6, 18)
        ));

        statusLabel = new JLabel("Ready.");
        statusLabel.setFont(UITheme.FONT_SMALL);
        statusLabel.setForeground(UITheme.TEXT_MUTED);

        JLabel storageNotice = new JLabel("Data auto-saved to .data/");
        storageNotice.setFont(UITheme.FONT_SMALL);
        storageNotice.setForeground(UITheme.TEXT_MUTED);

        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(storageNotice, BorderLayout.EAST);
        return statusPanel;
    }

    private void filterTable() {
        String query = searchField.getText().trim();
        ArrayList<Student> list = studentService.searchStudents(query);
        populateTable(list);
    }

    private void refreshData() {
        String query = searchField != null ? searchField.getText().trim() : "";
        ArrayList<Student> list = studentService.searchStudents(query);
        populateTable(list);
        updateStatCards();
    }

    private void populateTable(List<Student> students) {
        tableModel.setRowCount(0);
        for (Student s : students) {
            String status = s.getScoreCount() == 0 ? "NO DATA" : (s.isPassing() ? "PASS" : "FAIL");
            tableModel.addRow(new Object[]{
                    s.getId(),
                    s.getName(),
                    s.getFormattedScores(),
                    s.getScoreCount() > 0 ? String.format("%.2f", s.getAverageScore()) : "0.00",
                    s.getLetterGrade(),
                    status
            });
        }
        if (statusLabel != null) {
            statusLabel.setText(String.format("Showing %d student record(s).", students.size()));
        }
    }

    private void updateStatCards() {
        GradeReport report = studentService.generateSummaryReport();

        avgScoreValLabel.setText(String.format("%.2f", report.getClassAverage()));
        avgScoreSubLabel.setText(report.getTotalScoresRecorded() + " total scores");

        highestValLabel.setText(String.format("%.2f", report.getHighestScore()));
        List<Student> top = report.getTopStudents();
        if (!top.isEmpty()) {
            highestSubLabel.setText("Top: " + top.get(0).getName() + (top.size() > 1 ? " +" + (top.size() - 1) : ""));
        } else {
            highestSubLabel.setText("No data");
        }

        lowestValLabel.setText(String.format("%.2f", report.getLowestScore()));
        List<Student> bottom = report.getLowestStudents();
        if (!bottom.isEmpty()) {
            lowestSubLabel.setText("Lowest: " + bottom.get(0).getName() + (bottom.size() > 1 ? " +" + (bottom.size() - 1) : ""));
        } else {
            lowestSubLabel.setText("No data");
        }

        passRateValLabel.setText(String.format("%.1f%%", report.getPassingRate()));
        passRateSubLabel.setText(report.getPassingCount() + " pass / " + report.getFailingCount() + " fail");
    }

    private Student getSelectedStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) return null;
        String id = (String) tableModel.getValueAt(selectedRow, 0);
        return studentService.findStudentById(id);
    }

    private void handleAddStudent() {
        StudentDialog dialog = new StudentDialog(this, null);
        dialog.setVisible(true);

        if (dialog.isSaved() && dialog.getResultStudent() != null) {
            Student newStudent = dialog.getResultStudent();
            if (studentService.addStudent(newStudent)) {
                refreshData();
                JOptionPane.showMessageDialog(this,
                        "Student '" + newStudent.getName() + "' enrolled successfully!",
                        "Student Enrolled", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to add student. ID '" + newStudent.getId() + "' may already exist.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleEditStudent() {
        Student s = getSelectedStudent();
        if (s == null) return;

        StudentDialog dialog = new StudentDialog(this, s);
        dialog.setVisible(true);

        if (dialog.isSaved() && dialog.getResultStudent() != null) {
            Student res = dialog.getResultStudent();
            studentService.updateStudent(s.getId(), res.getName(), res.getScores());
            refreshData();
        }
    }

    private void handleAddScore() {
        Student s = getSelectedStudent();
        if (s == null) return;

        String input = JOptionPane.showInputDialog(this,
                "Enter new score (0.0 - 100.0) for " + s.getName() + ":",
                "Add Grade Score",
                JOptionPane.PLAIN_MESSAGE);

        if (input != null && !input.trim().isEmpty()) {
            try {
                double score = Double.parseDouble(input.trim());
                if (studentService.addScoreToStudent(s.getId(), score)) {
                    refreshData();
                    JOptionPane.showMessageDialog(this,
                            "Score added successfully! Updated Average: " +
                                    String.format("%.2f", studentService.findStudentById(s.getId()).getAverageScore()),
                            "Score Recorded", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Score must be between 0.0 and 100.0.",
                            "Invalid Score", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a valid numeric value.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleDeleteStudent() {
        Student s = getSelectedStudent();
        if (s == null) return;

        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove student '" + s.getName() + "' (ID: " + s.getId() + ")?\nThis action cannot be undone.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            if (studentService.deleteStudent(s.getId())) {
                refreshData();
            }
        }
    }

    private void handleViewReport() {
        GradeReport report = studentService.generateSummaryReport();
        ReportDialog dialog = new ReportDialog(this, report);
        dialog.setVisible(true);
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to sign out?",
                "Confirm Sign Out",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            authService.logout();
            SwingUtilities.invokeLater(() -> {
                LoginFrame login = new LoginFrame(authService, studentService);
                login.setVisible(true);
                dispose();
            });
        }
    }
}
