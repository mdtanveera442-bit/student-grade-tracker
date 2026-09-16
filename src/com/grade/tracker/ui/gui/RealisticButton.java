package com.grade.tracker.ui.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * A custom JButton with realistic tactile click feedback, clean proportional padding,
 * subtle 3D shadow, specular rim highlight, and 1px physical press depression.
 */
public class RealisticButton extends JButton {

    public enum Type {
        PRIMARY, SECONDARY, SUCCESS, DANGER, ACCENT
    }

    private final Type type;
    private final int arc;

    public RealisticButton(String text, Type type) {
        this(text, type, 10);
    }

    public RealisticButton(String text, Type type, int arc) {
        super(text);
        this.type = type;
        this.arc = arc;

        setFont(UITheme.FONT_BOLD);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Clean, balanced inner padding
        setBorder(new EmptyBorder(6, 12, 6, 12));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        int w = getWidth();
        int h = getHeight();

        boolean isPressed = getModel().isPressed();
        boolean isHover = getModel().isRollover();
        boolean isEnabled = isEnabled();

        // 3D tactile displacement: when clicked, button physically depresses 1 pixel
        int yOffset = isPressed ? 1 : 0;
        int btnHeight = h - 2;

        Color topColor;
        Color bottomColor;
        Color borderColor;
        Color shadowColor;
        Color textColor;
        Color topHighlight;

        switch (type) {
            case SUCCESS -> {
                topColor = isHover ? new Color(34, 197, 94) : new Color(22, 163, 74);
                bottomColor = isHover ? new Color(22, 163, 74) : new Color(21, 128, 61);
                borderColor = new Color(20, 83, 45, 170);
                shadowColor = new Color(20, 83, 45, 80);
                textColor = Color.WHITE;
                topHighlight = new Color(255, 255, 255, 60);
            }
            case DANGER -> {
                topColor = isHover ? new Color(248, 113, 113) : new Color(220, 38, 38);
                bottomColor = isHover ? new Color(220, 38, 38) : new Color(185, 28, 28);
                borderColor = new Color(127, 29, 29, 170);
                shadowColor = new Color(127, 29, 29, 80);
                textColor = Color.WHITE;
                topHighlight = new Color(255, 255, 255, 60);
            }
            case SECONDARY -> {
                topColor = isHover ? new Color(255, 255, 255) : new Color(248, 250, 252);
                bottomColor = isHover ? new Color(241, 245, 249) : new Color(226, 232, 240);
                borderColor = new Color(203, 213, 225);
                shadowColor = new Color(148, 163, 184, 70);
                textColor = isHover ? UITheme.PRIMARY : UITheme.TEXT_PRIMARY;
                topHighlight = new Color(255, 255, 255, 200);
            }
            case ACCENT -> {
                topColor = isHover ? new Color(168, 85, 247) : new Color(147, 51, 234);
                bottomColor = isHover ? new Color(147, 51, 234) : new Color(126, 34, 206);
                borderColor = new Color(88, 28, 135, 170);
                shadowColor = new Color(88, 28, 135, 80);
                textColor = Color.WHITE;
                topHighlight = new Color(255, 255, 255, 60);
            }
            default -> { // PRIMARY
                topColor = isHover ? new Color(59, 130, 246) : new Color(37, 99, 235);
                bottomColor = isHover ? new Color(37, 99, 235) : new Color(29, 78, 216);
                borderColor = new Color(30, 64, 175, 170);
                shadowColor = new Color(30, 58, 138, 90);
                textColor = Color.WHITE;
                topHighlight = new Color(255, 255, 255, 70);
            }
        }

        if (!isEnabled) {
            topColor = new Color(241, 245, 249);
            bottomColor = new Color(226, 232, 240);
            borderColor = new Color(226, 232, 240);
            shadowColor = new Color(0, 0, 0, 0);
            textColor = new Color(148, 163, 184);
            topHighlight = new Color(255, 255, 255, 80);
        }

        if (isPressed) {
            topColor = bottomColor;
        }

        // 1. Draw 3D Base Shadow
        if (!isPressed && isEnabled) {
            g2.setColor(shadowColor);
            g2.fillRoundRect(0, 2, w, btnHeight, arc, arc);
        }

        // 2. Draw Gradient Body
        GradientPaint gradient = new GradientPaint(
                0, yOffset, topColor,
                0, yOffset + btnHeight, bottomColor
        );
        g2.setPaint(gradient);
        g2.fillRoundRect(0, yOffset, w, btnHeight, arc, arc);

        // 3. Specular Top Rim Highlight
        if (isEnabled && !isPressed) {
            g2.setColor(topHighlight);
            g2.drawRoundRect(1, yOffset + 1, w - 3, btnHeight - 2, arc, arc);
        }

        // 4. Crisp Border
        g2.setColor(borderColor);
        g2.drawRoundRect(0, yOffset, w - 1, btnHeight - 1, arc, arc);

        // 5. Draw Text & Icon with smooth centering and 1px press shift
        FontMetrics fm = g2.getFontMetrics(getFont());
        String text = getText();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();

        int x = (w - textWidth) / 2;
        int y = yOffset + (btnHeight + textHeight) / 2 - 1;

        if (textColor.equals(Color.WHITE) && isEnabled && !isPressed) {
            g2.setColor(new Color(0, 0, 0, 40));
            g2.drawString(text, x, y + 1);
        }

        g2.setColor(textColor);
        g2.setFont(getFont());
        g2.drawString(text, x, y);

        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        // Natural proportional sizing (height 32px, proportional width)
        return new Dimension(Math.max(d.width + 6, 76), Math.max(d.height, 32));
    }
}
