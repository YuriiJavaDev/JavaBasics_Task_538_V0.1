package com.yurii.pavlenko.ui.dialogs;

import javax.swing.*;
import java.awt.*;

/**
 * Visual modal helper wrapping standardized user entry forms.
 */
public class TaskDialog {

    public static String showEditDialog(Component parentComponent, String currentText, String title) {
        // 1. Создаем поле ввода и задаем ему ширину (400 пикселей)
        JTextField textField = new JTextField(currentText);
        textField.setPreferredSize(new Dimension(600, 30));

        // 2. Создаем панель, чтобы красиво разместить компоненты
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Edit task description:"), BorderLayout.NORTH);
        panel.add(textField, BorderLayout.CENTER);

        // 3. Используем ConfirmDialog с нашей панелью
        int result = JOptionPane.showConfirmDialog(
                parentComponent,
                panel,
                title,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        // 4. Возвращаем текст, если нажали OK
        if (result == JOptionPane.OK_OPTION) {
            return textField.getText();
        }
        return null;
    }
}