package com.grade.tracker.ui.gui;

import com.grade.tracker.model.User;
import com.grade.tracker.service.AuthService;
import com.grade.tracker.service.StudentService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Modern authentication window with original proportions, vector logo icon,
 * balanced padding, glowing focus fields, and realistic tactile buttons.
 */
public class LoginFrame extends JFrame {
    private final AuthService authService;
    private final StudentService studentService;

    // Login Form Fields
    private final JTextField loginUsernameField;
    private final JPasswordField loginPasswordField;
    private final JLabel loginFeedbackLabel;

    // Register Form Fields
    private final JTextField regUsernameField;
    private final JPasswordField regPasswordField;
    private final JTextField regFullNameField;
    private final JComboBox<String> regRoleCombo;
    private final JLabel regFeedbackLabel;

    public LoginFrame(AuthService authService, StudentService studentService) {
        super("Student Grade Tracker - Sign In");
        this.authService = authService;
        this.studentService = studentService;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 520);
        setMinimumSize(new Dimension(380, 480));
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.BACKGROUND);
        setLayout(new BorderLayout());

        // Outer wrapper with clean padding
        JPanel outerPanel = new JPanel(new GridBagLayout());
        outerPanel.setBackground(UITheme.BACKGROUND);
        outerPanel.setBorder(new EmptyBorder(12, 16, 12, 16));

        RealisticCard card = new RealisticCard(14, UITheme.SURFACE);
        card.setLayout(new BorderLayout(0, 10));
        card.setPreferredSize(new Dimension(360, 450));
        card.setBorder(new EmptyBorder(16, 20, 16, 20));

        // Header Panel with Vector Logo
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        titleRow.setOpaque(false);
        titleRow.add(new JLabel(new GraduationCapIcon(24, 24)));

        JLabel titleText = new JLabel("Student Grade Tracker");
        titleText.setFont(UITheme.FONT_TITLE);
        titleText.setForeground(UITheme.TEXT_PRIMARY);
        titleRow.add(titleText);

        JLabel sub = new JLabel("Secure Student Management & Analytics");
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(UITheme.TEXT_MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleRow);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(sub);
        headerPanel.add(Box.createVerticalStrut(8));
        headerPanel.add(new JSeparator(JSeparator.HORIZONTAL));
        card.add(headerPanel, BorderLayout.NORTH);

        // Tabbed Panel for Login vs Register
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UITheme.FONT_BOLD);
        tabs.setBackground(UITheme.SURFACE);

        // --- TAB 1: LOGIN ---
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setOpaque(false);
        loginPanel.setBorder(new EmptyBorder(10, 2, 6, 2));

        loginUsernameField = UITheme.createTextField("Enter username...", 16);
        loginPasswordField = UITheme.createPasswordField("Enter password...", 16);
        loginFeedbackLabel = new JLabel(" ");
        loginFeedbackLabel.setFont(UITheme.FONT_SMALL);
        loginFeedbackLabel.setForeground(UITheme.DANGER);

        loginPanel.add(createFormField("Username", loginUsernameField));
        loginPanel.add(Box.createVerticalStrut(8));
        loginPanel.add(createFormField("Password", loginPasswordField));
        loginPanel.add(Box.createVerticalStrut(4));
        loginPanel.add(loginFeedbackLabel);
        loginPanel.add(Box.createVerticalStrut(8));

        JButton loginBtn = UITheme.createPrimaryButton("Sign In");
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.addActionListener(e -> handleLogin());
        loginPanel.add(loginBtn);

        loginPanel.add(Box.createVerticalStrut(8));

        JButton demoBtn = UITheme.createSecondaryButton("Quick Demo Sign In (admin)");
        demoBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        demoBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        demoBtn.addActionListener(e -> {
            loginUsernameField.setText("admin");
            loginPasswordField.setText("admin123");
            handleLogin();
        });
        loginPanel.add(demoBtn);

        tabs.addTab("Sign In", loginPanel);

        // --- TAB 2: REGISTER ---
        JPanel regPanel = new JPanel();
        regPanel.setLayout(new BoxLayout(regPanel, BoxLayout.Y_AXIS));
        regPanel.setOpaque(false);
        regPanel.setBorder(new EmptyBorder(8, 2, 6, 2));

        regFullNameField = UITheme.createTextField("Full Name (e.g. Dr. Connor)", 16);
        regUsernameField = UITheme.createTextField("Username (min 3 chars)", 16);
        regPasswordField = UITheme.createPasswordField("Password (min 4 chars)", 16);

        regRoleCombo = new JComboBox<>(new String[]{"Teacher", "Administrator", "Instructor", "Teaching Assistant"});
        regRoleCombo.setFont(UITheme.FONT_REGULAR);
        regRoleCombo.setBackground(UITheme.SURFACE);
        regRoleCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        regFeedbackLabel = new JLabel(" ");
        regFeedbackLabel.setFont(UITheme.FONT_SMALL);
        regFeedbackLabel.setForeground(UITheme.DANGER);

        regPanel.add(createFormField("Full Name", regFullNameField));
        regPanel.add(Box.createVerticalStrut(6));
        regPanel.add(createFormField("Username", regUsernameField));
        regPanel.add(Box.createVerticalStrut(6));
        regPanel.add(createFormField("Password", regPasswordField));
        regPanel.add(Box.createVerticalStrut(6));

        JPanel roleWrapper = new JPanel(new BorderLayout(0, 2));
        roleWrapper.setOpaque(false);
        roleWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        roleWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel roleLabel = new JLabel("Account Role");
        roleLabel.setFont(UITheme.FONT_BOLD);
        roleLabel.setForeground(UITheme.TEXT_PRIMARY);
        roleWrapper.add(roleLabel, BorderLayout.NORTH);
        roleWrapper.add(regRoleCombo, BorderLayout.CENTER);
        regPanel.add(roleWrapper);

        regPanel.add(Box.createVerticalStrut(4));
        regPanel.add(regFeedbackLabel);
        regPanel.add(Box.createVerticalStrut(6));

        JButton registerBtn = UITheme.createPrimaryButton("Create Account");
        registerBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        registerBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerBtn.addActionListener(e -> handleRegister());
        regPanel.add(registerBtn);

        tabs.addTab("Register", regPanel);

        card.add(tabs, BorderLayout.CENTER);
        outerPanel.add(card);
        add(outerPanel, BorderLayout.CENTER);

        // Bind Enter key to submit in login fields
        loginPasswordField.addActionListener(e -> handleLogin());
        loginUsernameField.addActionListener(e -> handleLogin());
    }

    private JPanel createFormField(String label, JComponent field) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 3));
        wrapper.setOpaque(false);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_BOLD);
        lbl.setForeground(UITheme.TEXT_PRIMARY);

        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        wrapper.add(lbl, BorderLayout.NORTH);
        wrapper.add(field, BorderLayout.CENTER);
        return wrapper;
    }

    private void handleLogin() {
        String username = loginUsernameField.getText().trim();
        String password = new String(loginPasswordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            loginFeedbackLabel.setText("Please enter both username and password.");
            return;
        }

        User user = authService.login(username, password);
        if (user != null) {
            loginFeedbackLabel.setText(" ");
            SwingUtilities.invokeLater(() -> {
                DashboardFrame dashboard = new DashboardFrame(authService, studentService);
                dashboard.setVisible(true);
                dispose();
            });
        } else {
            loginFeedbackLabel.setText("Invalid username or password.");
        }
    }

    private void handleRegister() {
        String username = regUsernameField.getText().trim();
        String password = new String(regPasswordField.getPassword()).trim();
        String fullName = regFullNameField.getText().trim();
        String role = (String) regRoleCombo.getSelectedItem();

        if (fullName.isEmpty()) {
            regFeedbackLabel.setText("Full name cannot be empty.");
            return;
        }
        if (username.length() < 3) {
            regFeedbackLabel.setText("Username must be at least 3 characters.");
            return;
        }
        if (password.length() < 4) {
            regFeedbackLabel.setText("Password must be at least 4 characters.");
            return;
        }

        boolean ok = authService.register(username, password, fullName, role);
        if (ok) {
            JOptionPane.showMessageDialog(this,
                    "Account registered successfully! You may now sign in.",
                    "Registration Successful",
                    JOptionPane.INFORMATION_MESSAGE);
            regFeedbackLabel.setText(" ");
            regUsernameField.setText("");
            regPasswordField.setText("");
            regFullNameField.setText("");
            loginUsernameField.setText(username);
            loginPasswordField.requestFocus();
        } else {
            regFeedbackLabel.setText("Username already exists or input is invalid.");
        }
    }
}
