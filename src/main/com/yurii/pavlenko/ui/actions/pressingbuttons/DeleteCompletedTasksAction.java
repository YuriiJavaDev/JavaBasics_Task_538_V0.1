package main.com.yurii.pavlenko.ui.actions.pressingbuttons;

import main.com.yurii.pavlenko.controller.TaskController;
import main.com.yurii.pavlenko.ui.dialogs.DialogHelper;

import javax.swing.AbstractAction;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Interface action tracking component routing selective drop requests back to the master controller.
 */
public class DeleteCompletedTasksAction extends AbstractAction {

    private final TaskController controller;
    private final Component parentComponent;
    private final Runnable refreshCallback;

    public DeleteCompletedTasksAction(TaskController controller, Component parentComponent, Runnable refreshCallback) {
        super("Delete Completed");
        this.controller = controller;
        this.parentComponent = parentComponent;
        this.refreshCallback = refreshCallback;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (DialogHelper.showDeleteConfirmation(parentComponent, "Are you sure? Delete all completed tasks?")) {
        controller.deleteCompletedTasks();
        refreshCallback.run();
        }
    }
}