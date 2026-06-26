package com.yurii.pavlenko.ui.renderers;

import com.yurii.pavlenko.model.Task;
import com.yurii.pavlenko.utils.DateFormatterUtil;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TaskCellRenderer extends JPanel implements ListCellRenderer<Task> {

    private final JCheckBox checkBox = new JCheckBox();
    private final JLabel textLabel = new JLabel();

    public TaskCellRenderer() {
        setLayout(new BorderLayout(8, 0));
        setBorder(new EmptyBorder(4, 6, 4, 6));
        checkBox.setOpaque(false);
        add(checkBox, BorderLayout.WEST);
        add(textLabel, BorderLayout.CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Task> list, Task task, int index,
                                                  boolean isSelected, boolean cellHasFocus) {

        setOpaque(isSelected);
        setBackground(isSelected ? list.getSelectionBackground() : null);
        checkBox.setEnabled(list.isEnabled());
        textLabel.setFont(list.getFont());

        if (task == null) {
            textLabel.setText("");
            checkBox.setSelected(false);
            return this;
        }

        checkBox.setSelected(task.isCompleted());
        String dateInfo = DateFormatterUtil.getFormattedDatesInfo(task);
        String title = task.isCompleted() ? "<s>" + task.getTitle() + "</s>" : task.getTitle();
        textLabel.setText("<html>" + title + dateInfo + "</html>");
        textLabel.setForeground(getImportanceColor(task.getImportance()));
        return this;
    }

    private Color getImportanceColor(String importance) {
        return switch (importance) {
            case "Urgent" -> Color.RED;
            case "Important" -> new Color(184, 134, 11);
            default -> Color.BLACK;
        };
    }
}