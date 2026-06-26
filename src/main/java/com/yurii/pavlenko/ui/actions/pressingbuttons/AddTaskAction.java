package com.yurii.pavlenko.ui.actions.pressingbuttons;

import com.yurii.pavlenko.controller.TaskController;
import com.yurii.pavlenko.ui.dialogs.TaskDialog;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AddTaskAction extends AbstractAction {
    private final TaskController controller;
    private final JTextField inputField;
    private final Component parentComponent;
    private final Runnable refreshCallback;

    public AddTaskAction(TaskController controller, JTextField inputField, Component parentComponent, Runnable refreshCallback) {
        super("Add");
        this.controller = controller;
        this.inputField = inputField;
        this.parentComponent = parentComponent;
        this.refreshCallback = refreshCallback;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String text = inputField.getText().trim();
        if (!text.isBlank()) {
            TaskDialog.TaskResult result = TaskDialog.showEditDialog(parentComponent, text, "Normal", "Add New Task");

            if (result != null) {
                controller.addTask(result.title(), result.importance());
                inputField.setText("");
                refreshCallback.run();
            }
        }
    }
}