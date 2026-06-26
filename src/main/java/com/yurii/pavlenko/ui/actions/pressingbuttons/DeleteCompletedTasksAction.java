package com.yurii.pavlenko.ui.actions.pressingbuttons;

import com.yurii.pavlenko.controller.TaskController;
import com.yurii.pavlenko.ui.dialogs.DialogHelperDelete;
import javax.swing.AbstractAction;
import java.awt.Component;
import java.awt.event.ActionEvent;

public class DeleteCompletedTasksAction extends AbstractAction {
    private final TaskController controller;
    private final Component parent;
    private final Runnable callback;

    public DeleteCompletedTasksAction(TaskController controller, Component parent, Runnable callback) {
        super("Delete Completed");
        this.controller = controller;
        this.parent = parent;
        this.callback = callback;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (DialogHelperDelete.showDeleteConfirmation(parent, "Are you sure? Delete all completed tasks?")) {
            controller.deleteCompletedTasks();
            callback.run();
        }
    }
}