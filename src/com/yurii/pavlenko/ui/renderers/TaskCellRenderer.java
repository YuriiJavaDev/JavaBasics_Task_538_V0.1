package com.yurii.pavlenko.ui.renderers;

import com.yurii.pavlenko.controller.TaskController;
import com.yurii.pavlenko.model.Task;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Component;

/**
 * Custom full-row list cell renderer ensuring unified background highlights and dynamic HTML task text strikethrough styling.
 */
public class TaskCellRenderer extends JPanel implements ListCellRenderer<String> {

    private final JCheckBox checkBox;
    private final JLabel textLabel;
    private final TaskController controller; // Link to safely inspect tasks via execution indices

    public TaskCellRenderer(TaskController controller) {
        this.controller = controller;

        setLayout(new BorderLayout(8, 0));
        setBorder(new EmptyBorder(4, 6, 4, 6));

        checkBox = new JCheckBox();
        textLabel = new JLabel();

        // Make sub-components background transparent to let the main panel highlight draw through cleanly
        checkBox.setOpaque(false);
        textLabel.setOpaque(false);

        add(checkBox, BorderLayout.WEST);
        add(textLabel, BorderLayout.CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends String> list, String value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        // Inspect factual task state markers inside the repository via index tracking boundaries
        if (index >= 0 && index < controller.getTasks().size()) {
            Task task = controller.getTasks().get(index);
            checkBox.setSelected(task.isCompleted());

            if (task.isCompleted()) {
                // Apply dynamic HTML wrapper to structure the strikethrough text decoration format
                textLabel.setText("<html><s>" + value + "</s></html>");
            } else {
                textLabel.setText(value == null ? "" : value);
            }
        } else {
            textLabel.setText(value == null ? "" : value);
            checkBox.setSelected(false);
        }

        textLabel.setFont(list.getFont());
        textLabel.setForeground(java.awt.Color.BLACK);

        if (isSelected) {
            setOpaque(true);
            setBackground(list.getSelectionBackground());
        } else {
            setOpaque(false);
        }

        checkBox.setEnabled(list.isEnabled());

        return this;
    }
}