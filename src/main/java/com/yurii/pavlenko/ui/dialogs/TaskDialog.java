package com.yurii.pavlenko.ui.dialogs;

import javax.swing.*;
import java.awt.*;

public class TaskDialog {

    public record TaskResult(String title, String importance) {}

    public static TaskResult showEditDialog(Component parent, String currentText, String currentImportance, String title) {
        JTextField textField = new JTextField(currentText, 60);
        String[] options = {"Urgent", "Important", "Normal"};
        JComboBox<String> importanceBox = new JComboBox<>(options);
        importanceBox.setSelectedItem(currentImportance != null ? currentImportance : "Normal");

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Task description:"));
        panel.add(textField);
        panel.add(new JLabel("Importance level:"));
        panel.add(importanceBox);

        int result = JOptionPane.showConfirmDialog(parent, panel, title, JOptionPane.OK_CANCEL_OPTION);
        return (result == JOptionPane.OK_OPTION) ?
                new TaskResult(textField.getText().trim(), (String) importanceBox.getSelectedItem()) : null;
    }
}