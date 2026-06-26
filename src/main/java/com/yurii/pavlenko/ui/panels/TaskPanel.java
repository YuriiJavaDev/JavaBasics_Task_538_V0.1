package com.yurii.pavlenko.ui.panels;

import com.yurii.pavlenko.controller.TaskController;
import com.yurii.pavlenko.model.Task;
import com.yurii.pavlenko.ui.actions.filtration.TaskFilterService;
import com.yurii.pavlenko.ui.actions.pressingbuttons.*;
import com.yurii.pavlenko.ui.actions.sorting.TaskComparatorFactory;
import com.yurii.pavlenko.ui.components.TaskFooterPanel;
import com.yurii.pavlenko.ui.listeners.TaskMouseHandler;
import com.yurii.pavlenko.ui.renderers.SortComboBoxRenderer;
import com.yurii.pavlenko.ui.renderers.TaskCellRenderer;
import com.yurii.pavlenko.utils.FilterStatus;
import com.yurii.pavlenko.utils.SortOrderOption;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

public class TaskPanel extends JPanel {
    private final JTextField input = new JTextField(50);
    private final JComboBox<FilterStatus> filterComboBox = new JComboBox<>(FilterStatus.values());
    private final JComboBox<SortOrderOption> sortComboBox = new JComboBox<>(SortOrderOption.values());
    private final DefaultListModel<Task> listModel = new DefaultListModel<>();
    private final JList<Task> taskList = new JList<>(listModel);
    private final TaskFooterPanel footerPanel = new TaskFooterPanel();

    public TaskPanel(TaskController controller) {
        setLayout(new BorderLayout());

        sortComboBox.setRenderer(new SortComboBoxRenderer(() -> (FilterStatus) filterComboBox.getSelectedItem()));
        taskList.setCellRenderer(new TaskCellRenderer());

        // Actions
        AddTaskAction addTaskAction = new AddTaskAction(controller, input, this, () -> refreshTasks(controller));
        DeleteTaskAction deleteTaskAction = new DeleteTaskAction(controller, this, taskList, () -> refreshTasks(controller));

        footerPanel.getDeleteCompletedButton().setAction(new DeleteCompletedTasksAction(controller, this, () -> refreshTasks(controller)));
        footerPanel.getClearAllButton().setAction(new ClearAllTasksAction(controller, this, () -> refreshTasks(controller)));

        // Top UI
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(input);
        topPanel.add(new JButton(addTaskAction));
        topPanel.add(filterComboBox);
        topPanel.add(sortComboBox);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(taskList), BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);

        // --- KEY BINDINGS ---
        // 1. Enter key for AddTaskAction
        input.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "submitTask");
        input.getActionMap().put("submitTask", addTaskAction);

        // 2. Delete key for DeleteTaskAction
        taskList.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "deleteAction");
        taskList.getActionMap().put("deleteAction", deleteTaskAction);

        // Listeners
        taskList.addMouseListener(new TaskMouseHandler(controller, taskList, () -> refreshTasks(controller),
                new EditTaskAction(controller, taskList, listModel, this, () -> refreshTasks(controller))));

        filterComboBox.addActionListener(e -> refreshTasks(controller));
        sortComboBox.addActionListener(e -> refreshTasks(controller));

        refreshTasks(controller);
    }

    public void refreshTasks(TaskController controller) {
        List<Task> allTasks = controller.getTasks();

        listModel.clear();
        List<Task> processed = TaskFilterService.filter(allTasks, (FilterStatus) filterComboBox.getSelectedItem());
        processed.sort(TaskComparatorFactory.getComparator((SortOrderOption) sortComboBox.getSelectedItem()));
        processed.forEach(listModel::addElement);

        int total = allTasks.size();
        int completed = (int) allTasks.stream().filter(Task::isCompleted).count();
        footerPanel.updateStatistics(total, completed, total - completed, (total > 0) ? (completed * 100) / total : 0);

        revalidate();
        repaint();
    }
}