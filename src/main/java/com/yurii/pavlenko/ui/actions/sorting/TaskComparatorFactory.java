package com.yurii.pavlenko.ui.actions.sorting;

import com.yurii.pavlenko.model.Task;
import com.yurii.pavlenko.utils.SortOrderOption;
import java.util.Comparator;

public final class TaskComparatorFactory {

    private TaskComparatorFactory() {}

    public static Comparator<Task> getComparator(SortOrderOption option) {
        if (option == null) return (t1, t2) -> 0;

        return switch (option) {
            case A_Z -> Comparator.comparing(Task::getTitle, String.CASE_INSENSITIVE_ORDER);
            case Z_A -> Comparator.comparing(Task::getTitle, String.CASE_INSENSITIVE_ORDER).reversed();
            case BY_STATUS -> Comparator.comparing(Task::isCompleted);
            case BY_CREATED -> Comparator.comparing(Task::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()));
            case BY_EDITED -> Comparator.comparing(Task::getUpdatedAt, Comparator.nullsLast(Comparator.naturalOrder()));
            case BY_COMPLETED -> Comparator.comparing(Task::getCompletedAt, Comparator.nullsLast(Comparator.naturalOrder()));
            case BY_IMPORTANCE -> Comparator.comparingInt(TaskComparatorFactory::getImportancePriority).reversed();
        };
    }

    private static int getImportancePriority(Task task) {
        return switch (task.getImportance()) {
            case "Urgent" -> 3;
            case "Important" -> 2;
            default -> 1;
        };
    }
}