package com.yurii.pavlenko.ui.actions.pressingbuttons;

import com.yurii.pavlenko.controller.TaskController;
import com.yurii.pavlenko.model.Task;
import com.yurii.pavlenko.ui.dialogs.TaskDialog;
import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;

/**
 * Standalone action handling modal task description adjustments and validation sync.
 */
public class EditTaskAction extends AbstractAction {

    private final TaskController controller;
    private final JList<Task> taskList;
    private final DefaultListModel<Task> listModel;
    private final Component parentComponent;
    private final Runnable refreshCallback;

    public EditTaskAction(TaskController controller, JList<Task> taskList,
                          DefaultListModel<Task> listModel, Component parentComponent, Runnable refreshCallback) {
        super("Edit");
        this.controller = controller;
        this.taskList = taskList;
        this.listModel = listModel;
        this.parentComponent = parentComponent;
        this.refreshCallback = refreshCallback;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex < 0) {
            return;
        }

        Task task = listModel.getElementAt(selectedIndex);

        String newText = TaskDialog.showEditDialog(parentComponent, task.getTitle(), "Edit task");

        if (newText != null && !newText.isBlank()) {
            task.setTitle(newText.trim());
            task.setUpdatedAt(LocalDateTime.now());

            controller.editTask(task.getId(), task);
            refreshCallback.run();
        }
    }
}