package main.com.yurii.pavlenko.ui.actions.pressingbuttons;

import main.com.yurii.pavlenko.controller.TaskController;
import main.com.yurii.pavlenko.model.Task;
import main.com.yurii.pavlenko.ui.dialogs.TaskDialog;
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

        // Fetch the domain model instance from the data source
        Task task = controller.getTasks().get(selectedIndex);

        // Display modal prompt pre-filled with the current title
        String newText = TaskDialog.showEditDialog(parentComponent, task.getTitle(), "Edit task");

        // Validate text and apply structural update parameters if safe
        if (newText != null && !newText.isBlank()) {
            task.setTitle(newText.trim());
            task.setUpdatedAt(LocalDateTime.now()); // Capture the exact modification timestamp

            controller.editTask(task.getId(), task);
            refreshCallback.run();
        }
    }
}