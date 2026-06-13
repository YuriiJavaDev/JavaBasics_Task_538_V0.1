package main.com.yurii.pavlenko.ui.actions;

import main.com.yurii.pavlenko.controller.TaskController;
import main.com.yurii.pavlenko.model.Task;
import main.com.yurii.pavlenko.ui.dialogs.DialogHelper;

import javax.swing.AbstractAction;
import javax.swing.JList;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Standalone action handling the removal of selected tasks from the tracking list view.
 */
public class DeleteTaskAction extends AbstractAction {

    private final TaskController controller;
    private final Component parentComponent;
    private final JList<String> taskList;
    private final Runnable refreshCallback;

    public DeleteTaskAction(TaskController controller, Component parentComponent, JList<String> taskList, Runnable refreshCallback) {
        super("Delete");
        this.controller = controller;
        this.parentComponent = parentComponent;
        this.taskList = taskList;
        this.refreshCallback = refreshCallback;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex < 0) {
            return;
        }

        Task task = controller.getTasks().get(selectedIndex);

        if (DialogHelper.showDeleteConfirmation(parentComponent, "Are you sure you want to delete the selected task?")) {
            controller.deleteTask(task.getId());
            refreshCallback.run();
        }
    }
}