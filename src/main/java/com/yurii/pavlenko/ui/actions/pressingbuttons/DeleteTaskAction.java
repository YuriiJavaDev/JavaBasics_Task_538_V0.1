package com.yurii.pavlenko.ui.actions.pressingbuttons;

import com.yurii.pavlenko.controller.TaskController;
import com.yurii.pavlenko.model.Task;
import com.yurii.pavlenko.ui.dialogs.DialogHelperDelete;
import javax.swing.AbstractAction;
import javax.swing.JList;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.List;

public class DeleteTaskAction extends AbstractAction {
    private final TaskController controller;
    private final Component parent;
    private final JList<Task> taskList;
    private final Runnable callback;

    public DeleteTaskAction(TaskController controller, Component parent, JList<Task> taskList, Runnable callback) {
        super("Delete");
        this.controller = controller;
        this.parent = parent;
        this.taskList = taskList;
        this.callback = callback;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        List<Task> selectedTasks = taskList.getSelectedValuesList();
        if (selectedTasks.isEmpty()) return;

        String message = (selectedTasks.size() == 1)
                ? "Are you sure you want to delete the selected task?"
                : "Are you sure you want to delete " + selectedTasks.size() + " selected tasks?";

        if (DialogHelperDelete.showDeleteConfirmation(parent, message)) {
            for (Task task : selectedTasks) {
                controller.deleteTask(task.getId());
            }
            callback.run();
        }
    }
}