package com.yurii.pavlenko.ui.actions;

import com.yurii.pavlenko.controller.TaskController;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;

/**
 * Interface action tracking component routing selective drop requests back to the master controller.
 */
public class DeleteCompletedTasksAction extends AbstractAction {

    private final TaskController controller;
    private final Runnable refreshCallback;

    public DeleteCompletedTasksAction(TaskController controller, Runnable refreshCallback) {
        super("Delete Completed"); // Set the text string directly onto the bounded button layout
        this.controller = controller;
        this.refreshCallback = refreshCallback;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Fire back-end delete arrays and notify window viewports immediately
        controller.deleteCompletedTasks();
        refreshCallback.run();
    }
}