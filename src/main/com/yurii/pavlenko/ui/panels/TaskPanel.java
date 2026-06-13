package main.com.yurii.pavlenko.ui.panels;

import main.com.yurii.pavlenko.controller.TaskController;
import main.com.yurii.pavlenko.model.Task;
import main.com.yurii.pavlenko.ui.actions.*;
import main.com.yurii.pavlenko.ui.components.TaskFooterPanel;
import main.com.yurii.pavlenko.ui.renderers.TaskCellRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Structural view panel layout encapsulating core components and mounting command action maps.
 */
public class TaskPanel extends JPanel {

    private JTextField input;
    private JButton addButton;
    private JButton deleteButton;
    private DefaultListModel<String> listModel;
    private JList<String> taskList;
    private TaskFooterPanel footerPanel;

    private AddTaskAction addTaskAction;
    private DeleteTaskAction deleteTaskAction;
    private EditTaskAction editTaskAction;
    private ClearAllTasksAction clearAllTasksAction;
    private DeleteCompletedTasksAction deleteCompletedTasksAction;

    public TaskPanel(TaskController controller) {
        initializeComponents(controller);
        initializeActions(controller);
        initializeButtons();
        initializeLayout();
        initializeListeners(controller);
        refreshTasks(controller);
    }

    private void initializeComponents(TaskController controller) {
        input = new JTextField(50);
        listModel = new DefaultListModel<>();
        taskList = new JList<>(listModel);
        taskList.setCellRenderer(new TaskCellRenderer(controller));
        footerPanel = new TaskFooterPanel();
    }

    private void initializeActions(TaskController controller) {
        addTaskAction = new AddTaskAction(controller, input, () -> refreshTasks(controller));
        deleteTaskAction = new DeleteTaskAction(controller, this, taskList, () -> refreshTasks(controller));
        editTaskAction = new EditTaskAction(controller, taskList, listModel, this, () -> refreshTasks(controller));
        clearAllTasksAction = new ClearAllTasksAction(controller, this, () -> refreshTasks(controller));
        deleteCompletedTasksAction = new DeleteCompletedTasksAction(controller, this, () -> refreshTasks(controller));
    }

    private void initializeButtons() {
        addButton = new JButton(addTaskAction);
        deleteButton = new JButton(deleteTaskAction);
        footerPanel.getClearAllButton().setAction(clearAllTasksAction);
        footerPanel.getDeleteCompletedButton().setAction(deleteCompletedTasksAction);
    }

    private void initializeLayout() {
        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(input);
        topPanel.add(addButton);
        topPanel.add(deleteButton);
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(taskList), BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private void initializeListeners(TaskController controller) {
        taskList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = taskList.locationToIndex(e.getPoint());
                if (index >= 0) {
                    Rectangle cellBounds = taskList.getCellBounds(index, index);
                    if (e.getX() - cellBounds.x < 30) {
                        Task task = controller.getTasks().get(index);
                        boolean newStatus = !task.isCompleted();
                        // task.setCompleted(!task.isCompleted());
                        task.setCompleted(newStatus);

                        // Dynamically manage completion timestamp based on the new status
                        if (newStatus) {
                            task.setCompletedAt(LocalDateTime.now());
                        } else {
                            task.setCompletedAt(null); // Clear timestamp if task is uncompleted
                        }

                        controller.editTask(index, task);
                        refreshTasks(controller);
                        taskList.repaint();
                    } else if (e.getClickCount() == 2) {
                        editTaskAction.actionPerformed(null);
                    }
                }
            }
        });

        // ====================================
        // SETTING THE ENTER KEY (KEY BINDINGS)
        // ====================================
        input.getInputMap(JComponent.WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke("ENTER"), "submitTask");
        input.getActionMap().put("submitTask", addTaskAction);

        taskList.getInputMap(JComponent.WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke("ENTER"), "editTask");
        taskList.getActionMap().put("editTask", editTaskAction);
    }

    public void refreshTasks(TaskController controller) {
        listModel.clear();
        List<Task> tasks = controller.getTasks();
        tasks.forEach(task -> listModel.addElement(task.getTitle()));
        int total = tasks.size();
        int completed = (int) tasks.stream().filter(Task::isCompleted).count();
        footerPanel.updateStatistics(total, completed, total - completed, total > 0 ? (completed * 100) / total : 0);
    }
}