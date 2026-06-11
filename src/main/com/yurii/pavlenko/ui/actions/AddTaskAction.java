package main.com.yurii.pavlenko.ui.actions;

import main.com.yurii.pavlenko.controller.TaskController;
import javax.swing.AbstractAction;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;

/**
 * Standalone action handling the addition of new tasks into the application system.
 */
public class AddTaskAction extends AbstractAction {

    private final TaskController controller;
    private final JTextField inputField;
    private final Runnable refreshCallback;

    public AddTaskAction(TaskController controller, JTextField inputField, Runnable refreshCallback) {
        super("Add");
        this.controller = controller;
        this.inputField = inputField;
        this.refreshCallback = refreshCallback;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String text = inputField.getText();
        if (text != null && !text.isBlank()) {
            controller.onAddButtonClicked(text.trim());
            inputField.setText("");
            refreshCallback.run();
        }
    }
}
