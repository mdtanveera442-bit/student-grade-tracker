package com.grade.tracker.ui.gui;

import com.grade.tracker.model.GradeReport;
import com.grade.tracker.model.Student;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.Map;

/**
 * Dialog displaying a comprehensive graphical and tabular grade summary report with balanced padding.
 */
public class ReportDialog extends JDialog {

    public ReportDialog(Frame owner, GradeReport report) {
        super(owner, "Class Summary Report", true);
        setSize(650, 680);
        setLocationRelativeTo(owner);
        getContentPane().setBackground(UITheme.BACKGROUND);
        setLayout(new BorderLayout());

        // Header with Vector Logo
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.SURFACE);
        header.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER),
                new EmptyBorder(14, 20, 14, 20)
        ));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titlePanel.setOpaque(false);
        titlePanel.add(new JLabel(new GraduationCapIcon(20, 20)));

        JLabel title = new JLabel("Class Summary Report");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);
        titlePanel.add(title);

        JLabel subtitle = new JLabel("Aggregate performance metrics, distributions, and performers");
        subtitle.setFont(UITheme.FONT_REGULAR);
        subtitle.setForeground(UITheme.TEXT_MUTED);

        header.add(titlePanel, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(UITheme.BACKGROUND);
        contentPanel.setBorder(new EmptyBorder(14, 18, 14, 18));

        // 1. Metric Cards Grid
        JPanel metricsGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        metricsGrid.setOpaque(false);
        metricsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        metricsGrid.add(createMiniCard("Class Average", String.format("%.2f", report.getClassAverage()), "Overall student mean", UITheme.PRIMARY));
        metricsGrid.add(createMiniCard("Highest Score", String.format("%.2f", report.getHighestScore()), getStudentListNames(report.getTopStudents()), UITheme.SUCCESS));
        metricsGrid.add(createMiniCard("Lowest Score", String.format("%.2f", report.getLowestScore()), getStudentListNames(report.getLowestStudents()), UITheme.DANGER));
        metricsGrid.add(createMiniCard("Passing Rate", String.format("%.1f%%", report.getPassingRate()),
                String.format("%d pass / %d fail", report.getPassingCount(), report.getFailingCount()), UITheme.PURPLE));

        contentPanel.add(metricsGrid);
        contentPanel.add(Box.createVerticalStrut(12));

        // 2. Grade Distribution Panel
        RealisticCard distPanel = new RealisticCard(12, UITheme.SURFACE);
        distPanel.setLayout(new BorderLayout());
        distPanel.setBorder(new EmptyBorder(12, 14, 12, 14));

        JLabel distTitle = new JLabel("Grade Distribution");
        distTitle.setFont(UITheme.FONT_HEADER);
        distTitle.setForeground(UITheme.TEXT_PRIMARY);
        distPanel.add(distTitle, BorderLayout.NORTH);

        JPanel barsPanel = new JPanel(new GridLayout(5, 1, 4, 8));
        barsPanel.setOpaque(false);
        barsPanel.setBorder(new EmptyBorder(10, 0, 4, 0));

        Map<String, Integer> dist = report.getGradeDistribution();
        String[] grades = {"A", "B", "C", "D", "F"};
        Color[] gradeColors = {UITheme.SUCCESS, UITheme.PRIMARY, new Color(14, 165, 233), UITheme.WARNING, UITheme.DANGER};

        for (int i = 0; i < grades.length; i++) {
            String g = grades[i];
            int count = dist.getOrDefault(g, 0);
            int total = report.getTotalStudents();
            double pct = total > 0 ? (count * 100.0 / total) : 0.0;

            JPanel row = new JPanel(new BorderLayout(8, 0));
            row.setOpaque(false);

            JLabel lbl = new JLabel(String.format("Grade %s (%d):", g, count));
            lbl.setFont(UITheme.FONT_BOLD);
            lbl.setForeground(UITheme.TEXT_PRIMARY);
            lbl.setPreferredSize(new Dimension(85, 20));

            JProgressBar bar = new JProgressBar(0, Math.max(1, total));
            bar.setValue(count);
            bar.setForeground(gradeColors[i]);
            bar.setBackground(new Color(241, 245, 249));
            bar.setBorder(new LineBorder(UITheme.BORDER, 1, true));
            bar.setPreferredSize(new Dimension(260, 18));

            JLabel pctLbl = new JLabel(String.format("%5.1f%%", pct));
            pctLbl.setFont(UITheme.FONT_BOLD);
            pctLbl.setForeground(gradeColors[i]);
            pctLbl.setPreferredSize(new Dimension(50, 20));

            row.add(lbl, BorderLayout.WEST);
            row.add(bar, BorderLayout.CENTER);
            row.add(pctLbl, BorderLayout.EAST);
            barsPanel.add(row);
        }
        distPanel.add(barsPanel, BorderLayout.CENTER);
        contentPanel.add(distPanel);
        contentPanel.add(Box.createVerticalStrut(12));

        // 3. Raw Text Report Preview
        RealisticCard textPanel = new RealisticCard(12, UITheme.SURFACE);
        textPanel.setLayout(new BorderLayout());
        textPanel.setBorder(new EmptyBorder(12, 14, 12, 14));

        JLabel textTitle = new JLabel("Summary Text");
        textTitle.setFont(UITheme.FONT_HEADER);
        textTitle.setForeground(UITheme.TEXT_PRIMARY);
        textPanel.add(textTitle, BorderLayout.NORTH);

        JTextArea textArea = new JTextArea(report.toFormattedSummary());
        textArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        textArea.setEditable(false);
        textArea.setBackground(new Color(248, 250, 252));
        textArea.setBorder(new EmptyBorder(6, 8, 6, 8));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(580, 140));
        scrollPane.setBorder(new LineBorder(UITheme.BORDER, 1, true));
        textPanel.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(textPanel);

        JScrollPane mainScroll = new JScrollPane(contentPanel);
        mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(12);
        add(mainScroll, BorderLayout.CENTER);

        // Footer buttons
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        footer.setBackground(UITheme.SURFACE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER));

        JButton copyBtn = UITheme.createSecondaryButton("Copy Text");
        copyBtn.addActionListener(e -> {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                    new StringSelection(report.toFormattedSummary()), null
            );
            JOptionPane.showMessageDialog(this, "Report copied to clipboard!", "Copied", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton closeBtn = UITheme.createPrimaryButton("Close");
        closeBtn.addActionListener(e -> dispose());

        footer.add(copyBtn);
        footer.add(closeBtn);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel createMiniCard(String title, String value, String subtitle, Color valColor) {
        RealisticCard card = new RealisticCard(10, UITheme.SURFACE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(10, 12, 10, 12));

        JLabel tLbl = new JLabel(title);
        tLbl.setFont(UITheme.FONT_SMALL);
        tLbl.setForeground(UITheme.TEXT_MUTED);

        JLabel vLbl = new JLabel(value);
        vLbl.setFont(UITheme.FONT_STAT_VAL);
        vLbl.setForeground(valColor);

        JLabel sLbl = new JLabel(subtitle);
        sLbl.setFont(UITheme.FONT_SMALL);
        sLbl.setForeground(UITheme.TEXT_MUTED);

        card.add(tLbl);
        card.add(Box.createVerticalStrut(2));
        card.add(vLbl);
        card.add(Box.createVerticalStrut(2));
        card.add(sLbl);
        return card;
    }

    private String getStudentListNames(java.util.List<Student> students) {
        if (students == null || students.isEmpty()) return "None";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < students.size(); i++) {
            sb.append(students.get(i).getName());
            if (i < students.size() - 1) sb.append(", ");
        }
        return sb.toString();
    }
}
