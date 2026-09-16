package com.grade.tracker.ui.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Custom table cell renderer providing pill badges for Letter Grades and Status.
 */
public class StatusBadgeRenderer extends DefaultTableCellRenderer {

    public enum Mode {
        LETTER_GRADE, STATUS, AVERAGE
    }

    private final Mode mode;

    public StatusBadgeRenderer(Mode mode) {
        this.mode = mode;
        setOpaque(false);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(UITheme.FONT_BOLD);

        String text = value != null ? value.toString() : "";
        label.setText(text);

        return new PillBadgePanel(text, mode, isSelected);
    }

    private static class PillBadgePanel extends JPanel {
        private final String text;
        private final Mode mode;
        private final boolean isRowSelected;

        public PillBadgePanel(String text, Mode mode, boolean isRowSelected) {
            this.text = text;
            this.mode = mode;
            this.isRowSelected = isRowSelected;
            setOpaque(false);
            setLayout(new GridBagLayout());

            JLabel lbl = new JLabel(getDisplayText());
            lbl.setFont(UITheme.FONT_BOLD);
            lbl.setForeground(getTextColor());
            add(lbl);
        }

        private String getDisplayText() {
            if (mode == Mode.STATUS) {
                if ("PASS".equalsIgnoreCase(text)) return "✔ PASS";
                if ("FAIL".equalsIgnoreCase(text)) return "✘ FAIL";
                return text;
            }
            return text;
        }

        private Color getTextColor() {
            if (mode == Mode.STATUS) {
                if ("PASS".equalsIgnoreCase(text)) return new Color(21, 128, 61);   // Green 700
                if ("FAIL".equalsIgnoreCase(text)) return new Color(185, 28, 28);  // Red 700
                return UITheme.TEXT_MUTED;
            } else if (mode == Mode.LETTER_GRADE) {
                return switch (text.trim().toUpperCase()) {
                    case "A" -> new Color(21, 128, 61);   // Emerald 700
                    case "B" -> new Color(29, 78, 216);   // Blue 700
                    case "C" -> new Color(3, 105, 161);   // Sky 700
                    case "D" -> new Color(180, 83, 9);    // Amber 700
                    case "F" -> new Color(185, 28, 28);   // Red 700
                    default -> UITheme.TEXT_MUTED;
                };
            } else { // AVERAGE
                return UITheme.TEXT_PRIMARY;
            }
        }

        private Color getBadgeBgColor() {
            if (mode == Mode.STATUS) {
                if ("PASS".equalsIgnoreCase(text)) return new Color(220, 252, 231);  // Green 100
                if ("FAIL".equalsIgnoreCase(text)) return new Color(254, 226, 226);  // Red 100
                return new Color(241, 245, 249);
            } else if (mode == Mode.LETTER_GRADE) {
                return switch (text.trim().toUpperCase()) {
                    case "A" -> new Color(220, 252, 231);  // Emerald 100
                    case "B" -> new Color(219, 234, 254);  // Blue 100
                    case "C" -> new Color(224, 242, 254);  // Sky 100
                    case "D" -> new Color(254, 243, 199);  // Amber 100
                    case "F" -> new Color(254, 226, 226);  // Red 100
                    default -> new Color(241, 245, 249);
                };
            } else {
                return new Color(241, 245, 249);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            if (isRowSelected) {
                g2.setColor(new Color(239, 246, 255)); // Selection row background
                g2.fillRect(0, 0, w, h);
            }

            if (mode != Mode.AVERAGE) {
                // Draw pill capsule
                int badgeW = Math.min(w - 14, 68);
                int badgeH = 20;
                int x = (w - badgeW) / 2;
                int y = (h - badgeH) / 2;

                g2.setColor(getBadgeBgColor());
                g2.fillRoundRect(x, y, badgeW, badgeH, badgeH, badgeH);

                // Subtle border
                g2.setColor(new Color(getTextColor().getRed(), getTextColor().getGreen(), getTextColor().getBlue(), 50));
                g2.drawRoundRect(x, y, badgeW - 1, badgeH - 1, badgeH, badgeH);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
