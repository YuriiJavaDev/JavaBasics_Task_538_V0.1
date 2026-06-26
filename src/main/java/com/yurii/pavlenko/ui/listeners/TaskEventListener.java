package com.yurii.pavlenko.ui.listeners;

import com.yurii.pavlenko.controller.TaskController;
import com.yurii.pavlenko.model.Task;
import com.yurii.pavlenko.ui.actions.pressingbuttons.*;
import javax.swing.*;
import java.awt.event.KeyEvent;

public class TaskEventListener {
    public static void register(TaskController controller, JList<Task> taskList,
                                JTextField input, JComboBox<?> filter, JComboBox<?> sort,
                                AddTaskAction addAction, DeleteTaskAction deleteAction,
                                EditTaskAction editAction, Runnable refresh) {

        taskList.addMouseListener(new TaskMouseHandler(controller, taskList, refresh, editAction));

        input.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "submit");
        input.getActionMap().put("submit", addAction);

        taskList.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
        taskList.getActionMap().put("delete", deleteAction);

        filter.addActionListener(e -> refresh.run());
        sort.addActionListener(e -> refresh.run());
    }
}