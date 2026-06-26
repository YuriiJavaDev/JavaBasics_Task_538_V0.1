package com.yurii.pavlenko.ui.listeners;

import com.yurii.pavlenko.controller.TaskController;
import com.yurii.pavlenko.model.Task;
import com.yurii.pavlenko.ui.actions.pressingbuttons.EditTaskAction;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;

public class TaskMouseHandler extends MouseAdapter {
    private final TaskController controller;
    private final JList<Task> taskList;
    private final Runnable refreshCallback;
    private final EditTaskAction editTaskAction;

    public TaskMouseHandler(TaskController controller, JList<Task> taskList,
                            Runnable refreshCallback, EditTaskAction editTaskAction) {
        this.controller = controller;
        this.taskList = taskList;
        this.refreshCallback = refreshCallback;
        this.editTaskAction = editTaskAction;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int index = taskList.locationToIndex(e.getPoint());
        if (index < 0) return;

        if (e.getX() - taskList.getCellBounds(index, index).x < 30) {
            Task task = taskList.getModel().getElementAt(index);
            task.setCompleted(!task.isCompleted());
            task.setCompletedAt(task.isCompleted() ? LocalDateTime.now() : null);
            controller.editTask(task.getId(), task);
            refreshCallback.run();
        } else if (e.getClickCount() == 2) {
            editTaskAction.actionPerformed(null);
        }
    }
}