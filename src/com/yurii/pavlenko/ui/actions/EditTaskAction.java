package com.yurii.pavlenko.ui.actions;

import com.yurii.pavlenko.controller.TaskController;
import com.yurii.pavlenko.ui.dialogs.TaskDialog;
import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import java.awt.Component;
import java.awt.event.ActionEvent;

/**
 * Standalone action handling modal task description adjustments and validation sync.
 */
public class EditTaskAction extends AbstractAction {

    private final TaskController controller;
    private final JList<String> taskList;
    private final DefaultListModel<String> listModel;
    private final Component parentComponent;
    private final Runnable refreshCallback;

    public EditTaskAction(TaskController controller, JList<String> taskList,
                          DefaultListModel<String> listModel, Component parentComponent, Runnable refreshCallback) {
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

        String currentText = listModel.getElementAt(selectedIndex);
        String newText = TaskDialog.showEditDialog(parentComponent, currentText, "Edit task");

        if (newText != null && !newText.isBlank()) {
            controller.editTask(selectedIndex, newText.trim());
            refreshCallback.run();
        }
    }
}