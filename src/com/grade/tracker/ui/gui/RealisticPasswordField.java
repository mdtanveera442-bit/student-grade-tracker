package com.grade.tracker.ui.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * A modern password field with rounded corners, focus glow ring, and balanced padding.
 */
public class RealisticPasswordField extends JPasswordField implements FocusListener {

    private final int arc;
    private String placeholder;
    private boolean isFocused = false;

    public RealisticPasswordField(int columns) {
        this("", columns, 8);
    }

    public RealisticPasswordField(String placeholder, int columns) {
        this(placeholder, columns, 8);
    }

    public RealisticPasswordField(String placeholder, int columns, int arc) {
        super(columns);
        this.placeholder = placeholder;
        this.arc = arc;

        setFont(UITheme.FONT_REGULAR);
        setForeground(UITheme.TEXT_PRIMARY);
        setOpaque(false);
        setBorder(new EmptyBorder(6, 10, 6, 10));
        addFocusListener(this);
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        int w = getWidth();
        int h = getHeight();

        // 1. Focus Glow Ring
        if (isFocused && isEnabled()) {
            g2.setColor(new Color(59, 130, 246, 60));
            g2.fillRoundRect(0, 0, w, h, arc + 3, arc + 3);
        }

        // 2. Inner Field Background
        int inset = isFocused ? 2 : 1;
        int fieldW = w - (inset * 2);
        int fieldH = h - (inset * 2);

        g2.setColor(isEnabled() ? (isEditable() ? Color.WHITE : UITheme.SURFACE_ALT) : UITheme.SURFACE_ALT);
        g2.fillRoundRect(inset, inset, fieldW, fieldH, arc, arc);

        // 3. Border
        if (isFocused && isEnabled()) {
            g2.setColor(UITheme.PRIMARY);
            g2.setStroke(new BasicStroke(1.5f));
        } else {
            g2.setColor(new Color(203, 213, 225));
            g2.setStroke(new BasicStroke(1.0f));
        }
        g2.drawRoundRect(inset, inset, fieldW - 1, fieldH - 1, arc, arc);

        g2.dispose();
        super.paintComponent(g);

        // 4. Placeholder text if empty
        if ((getPassword() == null || getPassword().length == 0) && placeholder != null && !isFocused) {
            Graphics2D gText = (Graphics2D) g.create();
            gText.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            gText.setColor(UITheme.TEXT_MUTED);
            gText.setFont(getFont());
            FontMetrics fm = gText.getFontMetrics();
            Insets insets = getInsets();
            int x = insets.left;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            gText.drawString(placeholder, x, y);
            gText.dispose();
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
        isFocused = true;
        repaint();
    }

    @Override
    public void focusLost(FocusEvent e) {
        isFocused = false;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        return new Dimension(d.width, Math.max(d.height, 32));
    }
}
