package com.yurii.pavlenko.ui.actions;

import com.yurii.pavlenko.controller.TaskController;
import javax.swing.AbstractAction;
import javax.swing.JList;
import java.awt.event.ActionEvent;

/**
 * Standalone action handling the removal of selected tasks from the tracking list view.
 */
public class DeleteTaskAction extends AbstractAction {

    private final TaskController controller;
    private final JList<String> taskList;
    private final Runnable refreshCallback;

    public DeleteTaskAction(TaskController controller, JList<String> taskList, Runnable refreshCallback) {
        super("Delete");
        this.controller = controller;
        this.taskList = taskList;
        this.refreshCallback = refreshCallback;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex >= 0) {
            controller.deleteTask(selectedIndex);
            refreshCallback.run();
        }
    }
}