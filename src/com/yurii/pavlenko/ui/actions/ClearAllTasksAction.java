package com.yurii.pavlenko.ui.actions;

import com.yurii.pavlenko.controller.TaskController;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;

/**
 * Standalone action handling the removal of all tasks from the tracking system repository.
 */
public class ClearAllTasksAction extends AbstractAction {

    private final TaskController controller;
    private final Runnable refreshCallback;

    public ClearAllTasksAction(TaskController controller, Runnable refreshCallback) {
        super("Clear All");
        this.controller = controller;
        this.refreshCallback = refreshCallback;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Execute the global cleanup routine through the application controller layer
        controller.clearAllTasks();

        // Trigger the synchronized screen refresh mechanism to update the viewport tracking
        refreshCallback.run();
    }
}