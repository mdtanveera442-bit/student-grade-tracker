package com.grade.tracker;

import com.grade.tracker.service.AuthService;
import com.grade.tracker.service.StudentService;
import com.grade.tracker.service.StorageService;
import com.grade.tracker.ui.cli.ConsoleUI;
import com.grade.tracker.ui.gui.LoginFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Main application entry point for Student Grade Tracker.
 * Supports both modern Swing GUI and interactive Console CLI.
 */
public class Main {

    public static void main(String[] args) {
        boolean forceCli = false;
        for (String arg : args) {
            if ("--cli".equalsIgnoreCase(arg) || "--console".equalsIgnoreCase(arg) || "-c".equalsIgnoreCase(arg)) {
                forceCli = true;
                break;
            }
        }

        // Initialize core backend services
        StorageService storageService = new StorageService();
        AuthService authService = new AuthService(storageService);
        StudentService studentService = new StudentService(storageService);

        // Determine if GUI can be launched
        boolean isHeadless = GraphicsEnvironment.isHeadless();

        if (forceCli || isHeadless) {
            if (isHeadless && !forceCli) {
                System.out.println("[INFO] Headless environment detected. Launching in Console CLI mode...");
            }
            ConsoleUI cli = new ConsoleUI(authService, studentService);
            cli.start();
        } else {
            // Launch GUI
            try {
                // Set system look and feel for native titlebars and fonts
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame(authService, studentService);
                loginFrame.setVisible(true);
            });
        }
    }
}
