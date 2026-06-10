package com.yurii.pavlenko.ui.components;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

/**
 * Isolated panel layout encapsulating summary metrics displays and secondary list control triggers.
 */
public class TaskFooterPanel extends JPanel {

    private JButton deleteCompletedButton;
    private JButton clearAllButton;
    private JLabel statusLabel;

    public TaskFooterPanel() {
        setLayout(new BorderLayout(5, 5));
        initComponents();
    }

    private void initComponents() {
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        deleteCompletedButton = new JButton("Delete Completed");
        clearAllButton = new JButton("Clear All");

        buttonsPanel.add(deleteCompletedButton);
        buttonsPanel.add(clearAllButton);

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statusLabel = new JLabel("Total: 0  Completed: 0  Left: 0  Progress: 0%");
        statsPanel.add(statusLabel);

        JPanel contentContainer = new JPanel(new GridLayout(2, 1, 5, 5));
        contentContainer.add(buttonsPanel);
        contentContainer.add(statsPanel);

        add(contentContainer, BorderLayout.CENTER);
    }

    /**
     * Public accessor exposing the internal delete completed button to link external action listeners.
     */
    public JButton getDeleteCompletedButton() {
        return deleteCompletedButton;
    }

    /**
     * Public accessor exposing the internal clear button to link external action listeners.
     */
    public JButton getClearAllButton() {
        return clearAllButton;
    }

    /**
     * Re-calculates and re-renders text metric segments inside the main statistics logger label.
     */
    public void updateStatistics(int total, int completed, int left, int progress) {
        String formattedStatus = String.format("Total: %d  Completed: %d  Left: %d  Progress: %d%%",
                total, completed, left, progress);
        statusLabel.setText(formattedStatus);
    }
}