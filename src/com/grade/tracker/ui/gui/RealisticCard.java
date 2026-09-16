package com.grade.tracker.ui.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * A custom JPanel that paints a realistic elevated card with smooth rounded corners,
 * ambient drop-shadow, and balanced padding.
 */
public class RealisticCard extends JPanel {

    private final int arc;
    private Color cardColor;

    public RealisticCard() {
        this(12, Color.WHITE);
    }

    public RealisticCard(int arc, Color cardColor) {
        this.arc = arc;
        this.cardColor = cardColor;
        setOpaque(false);
        // Proportional internal padding
        setBorder(new EmptyBorder(12, 14, 12, 14));
    }

    public void setCardColor(Color color) {
        this.cardColor = color;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // 1. Ambient Drop Shadow Layers
        g2.setColor(new Color(15, 23, 42, 6));
        g2.fillRoundRect(2, 4, w - 4, h - 5, arc + 2, arc + 2);

        g2.setColor(new Color(15, 23, 42, 12));
        g2.fillRoundRect(1, 3, w - 2, h - 4, arc + 1, arc + 1);

        // 2. Card Surface Body
        int cardHeight = h - 3;
        g2.setColor(cardColor);
        g2.fillRoundRect(0, 0, w, cardHeight, arc, arc);

        // 3. Crisp Subtle Border
        g2.setColor(UITheme.BORDER);
        g2.drawRoundRect(0, 0, w - 1, cardHeight - 1, arc, arc);

        g2.dispose();
        super.paintComponent(g);
    }
}
