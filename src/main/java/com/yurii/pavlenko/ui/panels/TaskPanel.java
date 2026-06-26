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
import java.util.List;

public class TaskPanel extends JPanel {
    private JTextField input;
    private JComboBox<FilterStatus> filterComboBox;
    private JComboBox<SortOrderOption> sortComboBox;
    private DefaultListModel<Task> listModel;
    private JList<Task> taskList;
    private TaskFooterPanel footerPanel;
    private AddTaskAction addTaskAction;
    private EditTaskAction editTaskAction;

    public TaskPanel(TaskController controller) {
        initComponents(controller);
        initActions(controller);
        initLayout();
        initListeners(controller);
        refreshTasks(controller);
    }

    private void initComponents(TaskController controller) {
        input = new JTextField(50);
        filterComboBox = new JComboBox<>(FilterStatus.values());
        sortComboBox = new JComboBox<>(SortOrderOption.values());
        sortComboBox.setRenderer(new SortComboBoxRenderer(() -> (FilterStatus) filterComboBox.getSelectedItem()));
        listModel = new DefaultListModel<>();
        taskList = new JList<>(listModel);
        taskList.setCellRenderer(new TaskCellRenderer());
        footerPanel = new TaskFooterPanel();
    }

    private void initActions(TaskController controller) {
        addTaskAction = new AddTaskAction(controller, input, this, () -> refreshTasks(controller));
        editTaskAction = new EditTaskAction(controller, taskList, listModel, this, () -> refreshTasks(controller));
    }

    private void initLayout() {
        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(input);
        topPanel.add(new JButton(addTaskAction));
        topPanel.add(filterComboBox);
        topPanel.add(sortComboBox);
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(taskList), BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private void initListeners(TaskController controller) {
        taskList.addMouseListener(new TaskMouseHandler(controller, taskList, () -> refreshTasks(controller), editTaskAction));
        filterComboBox.addActionListener(e -> updateAndRefresh(controller));
        sortComboBox.addActionListener(e -> updateAndRefresh(controller));
    }

    private void updateAndRefresh(TaskController controller) {
        validateSortOptions();
        refreshTasks(controller);
    }

    private void validateSortOptions() {
        FilterStatus filter = (FilterStatus) filterComboBox.getSelectedItem();
        SortOrderOption sort = (SortOrderOption) sortComboBox.getSelectedItem();
        if ((filter != FilterStatus.ALL && sort == SortOrderOption.BY_STATUS) ||
                (filter == FilterStatus.ACTIVE && sort == SortOrderOption.BY_COMPLETED)) {
            sortComboBox.setSelectedItem(SortOrderOption.A_Z);
        }
    }

    public void refreshTasks(TaskController controller) {
        List<Task> allTasks = controller.getTasks();
        updateFooter(allTasks);
        listModel.clear();
        List<Task> processed = TaskFilterService.filter(allTasks, (FilterStatus) filterComboBox.getSelectedItem());
        processed.sort(TaskComparatorFactory.getComparator((SortOrderOption) sortComboBox.getSelectedItem()));
        processed.forEach(listModel::addElement);
    }

    private void updateFooter(List<Task> tasks) {
        int total = tasks.size();
        int completed = (int) tasks.stream().filter(Task::isCompleted).count();
        int percent = (total > 0) ? (completed * 100) / total : 0;
        footerPanel.updateStatistics(total, completed, total - completed, percent);
    }
}