package com.yurii.pavlenko.ui.renderers;

import com.yurii.pavlenko.model.Task;
import com.yurii.pavlenko.utils.DateFormatterUtil;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TaskCellRenderer extends JPanel implements ListCellRenderer<Task> {

    private final JCheckBox checkBox;
    private final JLabel textLabel;

    public TaskCellRenderer() {
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
        if (task != null) {
            checkBox.setSelected(task.isCompleted());
            String dateInfo = DateFormatterUtil.getFormattedDatesInfo(task);

            if (task.isCompleted()) {
                textLabel.setText("<html><s>" + task.getTitle() + "</s>" + dateInfo + "</html>");
            } else {
                textLabel.setText("<html>" + task.getTitle() + dateInfo + "</html>");
            }

            switch (task.getImportance()) {
                case "Urgent" -> textLabel.setForeground(Color.RED);
                case "Important" -> textLabel.setForeground(new Color(184, 134, 11));
                default -> textLabel.setForeground(Color.BLACK);
            }
        } else {
            textLabel.setText("");
            checkBox.setSelected(false);
            textLabel.setForeground(Color.BLACK);
        }

        textLabel.setFont(list.getFont());

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