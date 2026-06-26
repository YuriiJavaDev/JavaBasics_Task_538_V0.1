package com.yurii.pavlenko.ui.components;

import javax.swing.*;
import java.awt.*;

public class TaskFooterPanel extends JPanel {
    private final JButton deleteCompletedButton = new JButton("Delete Completed");
    private final JButton clearAllButton = new JButton("Clear All");
    private final JLabel statusLabel = new JLabel("Total: 0  Completed: 0  Left: 0  Progress: 0%");

    public TaskFooterPanel() {
        setLayout(new BorderLayout(5, 5));

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonsPanel.add(deleteCompletedButton);
        buttonsPanel.add(clearAllButton);

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statsPanel.add(statusLabel);

        JPanel contentContainer = new JPanel(new GridLayout(2, 1, 5, 5));
        contentContainer.add(buttonsPanel);
        contentContainer.add(statsPanel);

        add(contentContainer, BorderLayout.CENTER);
    }

    public JButton getDeleteCompletedButton() { return deleteCompletedButton; }
    public JButton getClearAllButton() { return clearAllButton; }

    public void updateStatistics(int total, int completed, int left, int progress) {
        statusLabel.setText(String.format("Total: %d  Completed: %d  Left: %d  Progress: %d%%",
                total, completed, left, progress));
    }
}