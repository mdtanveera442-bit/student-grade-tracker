package com.grade.tracker.ui.gui;

import com.grade.tracker.model.Student;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Modal dialog for creating a new student or editing an existing student with balanced padding.
 */
public class StudentDialog extends JDialog {
    private final JTextField idField;
    private final JTextField nameField;
    private final JTextField scoresField;
    private final JLabel errorLabel;
    private boolean saved = false;
    private Student resultStudent = null;
    private final boolean isEditMode;

    public StudentDialog(Frame owner, Student existingStudent) {
        super(owner, existingStudent == null ? "Add New Student" : "Edit Student", true);
        this.isEditMode = (existingStudent != null);

        setSize(440, 360);
        setLocationRelativeTo(owner);
        setResizable(false);
        getContentPane().setBackground(UITheme.SURFACE);
        setLayout(new BorderLayout());

        // Header Panel with Vector Logo
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.SURFACE);
        headerPanel.setBorder(new EmptyBorder(16, 20, 10, 20));

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titleRow.setOpaque(false);
        titleRow.add(new JLabel(new GraduationCapIcon(18, 18)));

        JLabel titleLabel = new JLabel(isEditMode ? "Edit Student Details" : "Enroll New Student");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);
        titleRow.add(titleLabel);

        JLabel subtitleLabel = new JLabel(isEditMode ? "Update student name and numeric grades" : "Enter unique student ID, full name, and initial scores");
        subtitleLabel.setFont(UITheme.FONT_REGULAR);
        subtitleLabel.setForeground(UITheme.TEXT_MUTED);

        headerPanel.add(titleRow, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UITheme.SURFACE);
        formPanel.setBorder(new EmptyBorder(6, 20, 8, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 3, 4, 3);

        // ID
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel idLabel = new JLabel("Student ID:");
        idLabel.setFont(UITheme.FONT_BOLD);
        idLabel.setForeground(UITheme.TEXT_PRIMARY);
        formPanel.add(idLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        idField = UITheme.createTextField("e.g. STU-107", 14);
        if (isEditMode) {
            idField.setText(existingStudent.getId());
            idField.setEditable(false);
        }
        formPanel.add(idField, gbc);

        // Name
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setFont(UITheme.FONT_BOLD);
        nameLabel.setForeground(UITheme.TEXT_PRIMARY);
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        nameField = UITheme.createTextField("e.g. Alexander Pierce", 14);
        if (isEditMode) {
            nameField.setText(existingStudent.getName());
        }
        formPanel.add(nameField, gbc);

        // Scores
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        JLabel scoresLabel = new JLabel("Scores (0-100):");
        scoresLabel.setFont(UITheme.FONT_BOLD);
        scoresLabel.setForeground(UITheme.TEXT_PRIMARY);
        formPanel.add(scoresLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        scoresField = UITheme.createTextField("e.g. 85.5, 92, 78", 14);
        if (isEditMode) {
            scoresField.setText(existingStudent.getFormattedScores().replace("No scores", ""));
        }
        formPanel.add(scoresField, gbc);

        // Helper label
        gbc.gridx = 1; gbc.gridy = 3;
        JLabel helperLabel = new JLabel("Separate multiple scores with commas or spaces");
        helperLabel.setFont(UITheme.FONT_SMALL);
        helperLabel.setForeground(UITheme.TEXT_MUTED);
        formPanel.add(helperLabel, gbc);

        // Error message
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        errorLabel = new JLabel(" ");
        errorLabel.setFont(UITheme.FONT_SMALL);
        errorLabel.setForeground(UITheme.DANGER);
        formPanel.add(errorLabel, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        buttonPanel.setBackground(UITheme.SURFACE_ALT);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER));

        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());

        JButton saveBtn = UITheme.createPrimaryButton(isEditMode ? "Save Changes" : "Add Student");
        saveBtn.addActionListener(e -> handleSave(existingStudent));

        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void handleSave(Student existingStudent) {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String scoresText = scoresField.getText().trim();

        if (id.isEmpty()) {
            errorLabel.setText("Student ID is required.");
            return;
        }
        if (name.isEmpty()) {
            errorLabel.setText("Student Name is required.");
            return;
        }

        List<Double> parsedScores = new ArrayList<>();
        if (!scoresText.isEmpty()) {
            String[] tokens = scoresText.split("[,\\s]+");
            for (String t : tokens) {
                try {
                    double val = Double.parseDouble(t);
                    if (val < 0.0 || val > 100.0) {
                        errorLabel.setText("Scores must be between 0.0 and 100.0 (found: " + t + ")");
                        return;
                    }
                    parsedScores.add(val);
                } catch (NumberFormatException ex) {
                    errorLabel.setText("Invalid score format: '" + t + "'");
                    return;
                }
            }
        }

        resultStudent = new Student(id, name, parsedScores);
        saved = true;
        dispose();
    }

    public boolean isSaved() {
        return saved;
    }

    public Student getResultStudent() {
        return resultStudent;
    }
}
