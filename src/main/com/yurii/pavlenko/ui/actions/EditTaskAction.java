package main.com.yurii.pavlenko.ui.actions;

import main.com.yurii.pavlenko.controller.TaskController;
import main.com.yurii.pavlenko.model.Task;
import main.com.yurii.pavlenko.ui.dialogs.TaskDialog;
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

        // 1. Получаем актуальный объект задачи из контроллера
        Task task = controller.getTasks().get(selectedIndex);

        // 2. Показываем диалог с текущим текстом
        String newText = TaskDialog.showEditDialog(parentComponent, task.getTitle(), "Edit task");

        // 3. Если текст изменился, обновляем объект и отправляем в контроллер
        if (newText != null && !newText.isBlank()) {
            task.setTitle(newText.trim()); // Обновляем поле title в объекте
            controller.editTask(selectedIndex, task); // Передаем объект целиком
            refreshCallback.run();
        }
    }
}