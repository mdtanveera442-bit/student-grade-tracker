package com.grade.tracker.ui.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;

/**
 * A vector-rendered academic graduation cap icon that scales cleanly at original sizes
 * without relying on system emoji font fallbacks.
 */
public class GraduationCapIcon implements Icon {
    private final int width;
    private final int height;
    private final Color primaryColor;
    private final Color accentColor;

    public GraduationCapIcon() {
        this(22, 22, UITheme.PRIMARY, new Color(245, 158, 11)); // Blue with Amber tassel
    }

    public GraduationCapIcon(int size) {
        this(size, size, UITheme.PRIMARY, new Color(245, 158, 11));
    }

    public GraduationCapIcon(int width, int height) {
        this(width, height, UITheme.PRIMARY, new Color(245, 158, 11));
    }

    public GraduationCapIcon(int width, int height, Color primaryColor, Color accentColor) {
        this.width = width;
        this.height = height;
        this.primaryColor = primaryColor;
        this.accentColor = accentColor;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.translate(x, y);

        double sx = width / 24.0;
        double sy = height / 24.0;
        g2.scale(sx, sy);

        // 1. Skullcap (underneath cap)
        g2.setColor(primaryColor);
        GeneralPath skull = new GeneralPath();
        skull.moveTo(6, 11);
        skull.curveTo(6, 18, 18, 18, 18, 11);
        skull.closePath();
        g2.fill(skull);

        // 2. Diamond Mortarboard (Top of cap)
        GeneralPath cap = new GeneralPath();
        cap.moveTo(12, 4);   // Top
        cap.lineTo(22, 9);   // Right
        cap.lineTo(12, 14);  // Bottom
        cap.lineTo(2, 9);    // Left
        cap.closePath();

        // Shaded cap surface
        GradientPaint gp = new GradientPaint(2, 4, primaryColor.brighter(), 22, 14, primaryColor);
        g2.setPaint(gp);
        g2.fill(cap);

        g2.setColor(primaryColor.darker());
        g2.setStroke(new BasicStroke(1.0f));
        g2.draw(cap);

        // 3. Center Button / Stud
        g2.setColor(accentColor);
        g2.fillOval(11, 8, 2, 2);

        // 4. Tassel String & Tassel
        GeneralPath tassel = new GeneralPath();
        tassel.moveTo(12, 9);
        tassel.curveTo(8, 9, 5, 12, 4, 16);
        g2.setColor(accentColor);
        g2.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(tassel);

        // Tassel drop
        g2.fillRoundRect(3, 16, 2, 4, 1, 1);

        g2.dispose();
    }

    @Override
    public int getIconWidth() {
        return width;
    }

    @Override
    public int getIconHeight() {
        return height;
    }
}
