package com.yurii.pavlenko.ui.renderers;

import com.yurii.pavlenko.model.Task;
import com.yurii.pavlenko.utils.DateFormatterUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Custom full-row list cell renderer ensuring unified background highlights and dynamic HTML task text strikethrough styling.
 */
public class TaskCellRenderer extends JPanel implements ListCellRenderer<Task> { // Updated to Task

    private final JCheckBox checkBox;
    private final JLabel textLabel;

    public TaskCellRenderer() { // Controller dependency removed
        setLayout(new BorderLayout(8, 0));
        setBorder(new EmptyBorder(4, 6, 4, 6));

        checkBox = new JCheckBox();
        textLabel = new JLabel();

        checkBox.setOpaque(false);
        textLabel.setOpaque(false);

        add(checkBox, BorderLayout.WEST);
        add(textLabel, BorderLayout.CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Task> list, Task task, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        // Fix: Use the direct task object supplied by the JList model instead of index-based controller fetching
        if (task != null) {
            checkBox.setSelected(task.isCompleted());

            // Generate metadata date suffix using centralized utility class
            String dateInfo = DateFormatterUtil.getFormattedDatesInfo(task);

            // Construct final display content with specific conditional HTML typography rules
            if (task.isCompleted()) {
                // Apply strikethrough styling to core task title text only, preserving plain text for meta dates
                textLabel.setText("<html><s>" + task.getTitle() + "</s>" + dateInfo + "</html>");
            } else {
                textLabel.setText("<html>" + task.getTitle() + dateInfo + "</html>");
            }
        } else {
            textLabel.setText("");
            checkBox.setSelected(false);
        }

        textLabel.setFont(list.getFont());
        textLabel.setForeground(Color.BLACK);

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