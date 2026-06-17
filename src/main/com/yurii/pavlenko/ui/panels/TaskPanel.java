package main.com.yurii.pavlenko.ui.panels;

import main.com.yurii.pavlenko.controller.TaskController;
import main.com.yurii.pavlenko.model.Task;
import main.com.yurii.pavlenko.ui.actions.pressingbuttons.*;
import main.com.yurii.pavlenko.ui.actions.filtration.TaskFilterService;
import main.com.yurii.pavlenko.ui.actions.sorting.TaskComparatorFactory;
import main.com.yurii.pavlenko.ui.components.TaskFooterPanel;
import main.com.yurii.pavlenko.ui.renderers.SortComboBoxRenderer;
import main.com.yurii.pavlenko.ui.renderers.TaskCellRenderer;
import main.com.yurii.pavlenko.utils.FilterStatus;
import main.com.yurii.pavlenko.utils.SortOrderOption;

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

    private JComboBox<FilterStatus> filterComboBox;
    private JComboBox<SortOrderOption> sortComboBox;

    private DefaultListModel<Task> listModel;
    private JList<Task> taskList;
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

        filterComboBox = new JComboBox<>(FilterStatus.values());
        sortComboBox = new JComboBox<>(SortOrderOption.values());

        sortComboBox.setRenderer(new SortComboBoxRenderer(() -> (FilterStatus) filterComboBox.getSelectedItem()));

        listModel = new DefaultListModel<>();
        taskList = new JList<>(listModel);
        taskList.setCellRenderer(new TaskCellRenderer());
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
        topPanel.add(filterComboBox);
        topPanel.add(sortComboBox);

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
                        Task task = listModel.getElementAt(index);
                        boolean newStatus = !task.isCompleted();
                        task.setCompleted(newStatus);

                        if (newStatus) {
                            task.setCompletedAt(LocalDateTime.now());
                        } else {
                            task.setCompletedAt(null);
                        }

                        controller.editTask(task.getId(), task);
                        refreshTasks(controller);
                        taskList.repaint();
                    } else if (e.getClickCount() == 2) {
                        editTaskAction.actionPerformed(null);
                    }
                }
            }
        });

        filterComboBox.addActionListener(e -> {
            FilterStatus selectedFilter = (FilterStatus) filterComboBox.getSelectedItem();
            SortOrderOption selectedSort = (SortOrderOption) sortComboBox.getSelectedItem();

            // UX Guard: Reset sorting to A_Z if the selected sort option becomes invalid for the new filter
            if (selectedFilter != FilterStatus.ALL && selectedSort == SortOrderOption.BY_STATUS) {
                sortComboBox.setSelectedItem(SortOrderOption.A_Z);
            } else if (selectedFilter == FilterStatus.ACTIVE && selectedSort == SortOrderOption.BY_COMPLETED) {
                sortComboBox.setSelectedItem(SortOrderOption.A_Z);
            }

            refreshTasks(controller);
        });

        sortComboBox.addActionListener(e -> {
            FilterStatus selectedFilter = (FilterStatus) filterComboBox.getSelectedItem();
            SortOrderOption selectedSort = (SortOrderOption) sortComboBox.getSelectedItem();

            // Action Guard: Prevent forcing disabled items via keyboard navigation shifts
            if (selectedFilter != FilterStatus.ALL && selectedSort == SortOrderOption.BY_STATUS) {
                sortComboBox.setSelectedItem(SortOrderOption.A_Z);
                return;
            }
            if (selectedFilter == FilterStatus.ACTIVE && selectedSort == SortOrderOption.BY_COMPLETED) {
                sortComboBox.setSelectedItem(SortOrderOption.A_Z);
                return;
            }

            refreshTasks(controller);
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

        FilterStatus selectedFilter = (FilterStatus) filterComboBox.getSelectedItem();
        SortOrderOption selectedSort = (SortOrderOption) sortComboBox.getSelectedItem();

        // 1. Calculate global footer statistics using pure dataset state
        List<Task> allTasks = controller.getTasks();
        int total = allTasks.size();
        int completedCount = (int) allTasks.stream().filter(Task::isCompleted).count();
        footerPanel.updateStatistics(total, completedCount, total - completedCount, total > 0 ? (completedCount * 100) / total : 0);

        // 2. Delegate data manipulations to isolated dedicated service sub-packages
        List<Task> processedTasks = TaskFilterService.filter(allTasks, selectedFilter);
        processedTasks.sort(TaskComparatorFactory.getComparator(selectedSort));

        // 3. Output pipeline results onto the interactive UI layer
        processedTasks.forEach(task -> listModel.addElement(task));
    }
}