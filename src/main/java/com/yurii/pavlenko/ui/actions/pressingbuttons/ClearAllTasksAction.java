package com.yurii.pavlenko.ui.actions.pressingbuttons;

import com.yurii.pavlenko.controller.TaskController;
import com.yurii.pavlenko.ui.dialogs.DialogHelperDelete;

import javax.swing.AbstractAction;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Standalone action handling the removal of all tasks from the tracking system repository.
 */
public class ClearAllTasksAction extends AbstractAction {

    private final TaskController controller;
    private final Component parentComponent;
    private final Runnable refreshCallback;

    public ClearAllTasksAction(TaskController controller, Component parentComponent, Runnable refreshCallback) {
        super("Clear All");
        this.controller = controller;
        this.parentComponent = parentComponent;
        this.refreshCallback = refreshCallback;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (DialogHelperDelete.showDeleteConfirmation(parentComponent, "Are you sure you want to delete ALL tasks?")) {
        controller.clearAllTasks();
        refreshCallback.run();
        }
    }
}
