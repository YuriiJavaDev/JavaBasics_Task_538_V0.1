package main.com.yurii.pavlenko.ui.actions.filtration;

import main.com.yurii.pavlenko.model.Task;
import main.com.yurii.pavlenko.util.FilterStatus;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service handling data filtration logic based on the selected UI filter status.
 */
public final class TaskFilterService {

    private TaskFilterService() {}

    /**
     * Filters the incoming list of tasks based on the provided FilterStatus configuration.
     */
    public static List<Task> filter(List<Task> tasks, FilterStatus status) {
        if (status == null || status == FilterStatus.ALL) {
            return tasks;
        }

        return tasks.stream()
                .filter(task -> status == FilterStatus.COMPLETED ? task.isCompleted() : !task.isCompleted())
                .collect(Collectors.toList());
    }
}