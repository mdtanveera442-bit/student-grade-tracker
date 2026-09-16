package com.grade.tracker.ui.gui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Styling constants and helper factory methods for a realistic, modern, tactile Swing UI
 * with original, balanced proportional typography and padding.
 */
public class UITheme {

    // Palette
    public static final Color PRIMARY = new Color(37, 99, 235);       // #2563EB Vibrant Blue
    public static final Color PRIMARY_HOVER = new Color(29, 78, 216); // #1D4ED8
    public static final Color BACKGROUND = new Color(241, 245, 249);   // #F1F5F9 Soft Modern Slate
    public static final Color SURFACE = new Color(255, 255, 255);      // Pure White
    public static final Color SURFACE_ALT = new Color(248, 250, 252);  // #F8FAFC
    public static final Color TEXT_PRIMARY = new Color(15, 23, 42);    // #0F172A Slate 900
    public static final Color TEXT_MUTED = new Color(100, 116, 139);   // #64748B Slate 500
    public static final Color BORDER = new Color(226, 232, 240);       // #E2E8F0 Slate 200

    public static final Color SUCCESS = new Color(22, 163, 74);        // #16A34A Green
    public static final Color SUCCESS_BG = new Color(220, 252, 231);   // Light Green
    public static final Color DANGER = new Color(220, 38, 38);         // #DC2626 Red
    public static final Color DANGER_BG = new Color(254, 226, 226);    // Light Red
    public static final Color WARNING = new Color(217, 119, 6);        // Amber
    public static final Color PURPLE = new Color(124, 58, 237);        // Violet

    // Balanced Proportional Typography
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_STAT_VAL = new Font("Segoe UI", Font.BOLD, 22);

    /**
     * Creates a realistic tactile Primary button with gradient, 3D bottom shadow,
     * and physical click depression.
     */
    public static JButton createPrimaryButton(String text) {
        return new RealisticButton(text, RealisticButton.Type.PRIMARY);
    }

    /**
     * Creates a realistic tactile Secondary button with clean frosted look and 3D depth.
     */
    public static JButton createSecondaryButton(String text) {
        return new RealisticButton(text, RealisticButton.Type.SECONDARY);
    }

    /**
     * Creates a realistic tactile Success button with emerald green gradient.
     */
    public static JButton createSuccessButton(String text) {
        return new RealisticButton(text, RealisticButton.Type.SUCCESS);
    }

    /**
     * Creates a realistic tactile Danger button with ruby red gradient.
     */
    public static JButton createDangerButton(String text) {
        return new RealisticButton(text, RealisticButton.Type.DANGER);
    }

    /**
     * Creates a realistic tactile Accent button with purple gradient.
     */
    public static JButton createAccentButton(String text) {
        return new RealisticButton(text, RealisticButton.Type.ACCENT);
    }

    /**
     * Creates a modern rounded text field with focus glow ring.
     */
    public static JTextField createTextField(int columns) {
        return new RealisticTextField(columns);
    }

    /**
     * Creates a modern rounded text field with placeholder.
     */
    public static JTextField createTextField(String placeholder, int columns) {
        return new RealisticTextField(placeholder, columns);
    }

    /**
     * Creates a modern rounded password field with focus glow ring.
     */
    public static JPasswordField createPasswordField(int columns) {
        return new RealisticPasswordField(columns);
    }

    /**
     * Creates a modern rounded password field with placeholder.
     */
    public static JPasswordField createPasswordField(String placeholder, int columns) {
        return new RealisticPasswordField(placeholder, columns);
    }

    public static Border createCardBorder() {
        return new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(12, 14, 12, 14)
        );
    }
}
