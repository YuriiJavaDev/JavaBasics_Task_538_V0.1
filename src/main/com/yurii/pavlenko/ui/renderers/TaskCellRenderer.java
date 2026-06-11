package main.com.yurii.pavlenko.ui.renderers;

import main.com.yurii.pavlenko.controller.TaskController;
import main.com.yurii.pavlenko.model.Task;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Custom full-row list cell renderer ensuring unified background highlights and dynamic HTML task text strikethrough styling.
 */
public class TaskCellRenderer extends JPanel implements ListCellRenderer<String> {

    private final JCheckBox checkBox;
    private final JLabel textLabel;
    private final TaskController controller;

    public TaskCellRenderer(TaskController controller) {
        this.controller = controller;
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
    public Component getListCellRendererComponent(JList<? extends String> list, String value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        if (index >= 0 && index < controller.getTasks().size()) {
            Task task = controller.getTasks().get(index);
            checkBox.setSelected(task.isCompleted());

            if (task.isCompleted()) {
                textLabel.setText("<html><s>" + value + "</s></html>");
            } else {
                textLabel.setText(value == null ? "" : value);
            }
        } else {
            textLabel.setText(value == null ? "" : value);
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