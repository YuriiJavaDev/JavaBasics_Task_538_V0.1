package com.yurii.pavlenko.ui.panels;

import com.yurii.pavlenko.controller.TaskController;
import com.yurii.pavlenko.model.Task;
import com.yurii.pavlenko.ui.actions.*;
import com.yurii.pavlenko.ui.components.TaskFooterPanel;
import com.yurii.pavlenko.ui.renderers.TaskCellRenderer;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

    /**
     * Instantiates components and sets up clean behavioral command structures.
     */
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

        // Inject custom cell renderer to present tasks with checkboxes, forwarding controller context
        taskList.setCellRenderer(new TaskCellRenderer(controller));

        // Initialize the tracking metrics footer container
        footerPanel = new TaskFooterPanel();
    }

    private void initializeActions(TaskController controller) {
        addTaskAction = new AddTaskAction(controller, input, () -> refreshTasks(controller));
        deleteTaskAction = new DeleteTaskAction(controller, taskList, () -> refreshTasks(controller));
        editTaskAction = new EditTaskAction(controller, taskList, listModel, this, () -> refreshTasks(controller));
        clearAllTasksAction = new ClearAllTasksAction(controller, () -> refreshTasks(controller));
        deleteCompletedTasksAction = new DeleteCompletedTasksAction(controller, () -> refreshTasks(controller));
    }

    private void initializeButtons() {
        addButton = new JButton(addTaskAction);
        deleteButton = new JButton(deleteTaskAction);

        // Bind our clear action logic directly to the pre-existing button inside the footer panel
        footerPanel.getClearAllButton().setAction(clearAllTasksAction);
        footerPanel.getDeleteCompletedButton().setAction(deleteCompletedTasksAction);
    }

    private void initializeLayout() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Top toolbar contains only the original text input, add, and single row delete buttons
        topPanel.add(input);
        topPanel.add(addButton);
        topPanel.add(deleteButton);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(taskList), BorderLayout.CENTER);

        // Append the configured statistical metrics panel containing its native controls to the southern region
        add(footerPanel, BorderLayout.SOUTH);
    }

    private void initializeListeners(TaskController controller) {
        taskList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = taskList.locationToIndex(e.getPoint());
                if (index >= 0) {
                    // Check if the click happened strictly inside the checkbox area (first 30 horizontal pixels)
                    if (e.getX() < 30) {
                        Task task = controller.getTasks().get(index);
                        // Toggle completion state maps
                        task.setCompleted(!task.isCompleted());

                        // Immediately fire a synchronized view model refresh call
                        refreshTasks(controller);
                    } else if (e.getClickCount() == 2) {
                        editTaskAction.actionPerformed(null);
                    }
                }
            }
        });
    }

    /**
     * Publicly accessible visibility method to push structural data models into the view list layout.
     */
    public void refreshTasks(TaskController controller) {
        listModel.clear();

        List<Task> tasks = controller.getTasks();
        tasks.forEach(task -> listModel.addElement(task.getTitle()));

        // Factual active runtime metrics calculations
        int total = tasks.size();
        int completed = (int) tasks.stream().filter(Task::isCompleted).count();
        int left = total - completed;
        int progress = total > 0 ? (completed * 100) / total : 0;

        // Feed actual runtime stats parameters down into the footer label
        footerPanel.updateStatistics(total, completed, left, progress);
    }
}
