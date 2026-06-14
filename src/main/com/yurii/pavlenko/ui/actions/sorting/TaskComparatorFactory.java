package main.com.yurii.pavlenko.ui.actions.sorting;

import main.com.yurii.pavlenko.model.Task;
import main.com.yurii.pavlenko.util.SortOrderOption;

import java.util.Comparator;

/**
 * Factory class providing safe, structured comparators for sorting tasks.
 */
public final class TaskComparatorFactory {

    private TaskComparatorFactory() {}

    /**
     * Resolves and returns a matching Comparator based on the selected UI SortOrderOption.
     */
    public static Comparator<Task> getComparator(SortOrderOption option) {
        if (option == null) {
            return (t1, t2) -> 0; // Neutral fallback comparator
        }

        return switch (option) {
            case A_Z -> Comparator.comparing(Task::getTitle, String.CASE_INSENSITIVE_ORDER);
            case Z_A -> Comparator.comparing(Task::getTitle, String.CASE_INSENSITIVE_ORDER).reversed();
            case BY_STATUS -> Comparator.comparing(Task::isCompleted);
            case BY_CREATED -> Comparator.comparing(Task::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()));
            case BY_EDITED -> Comparator.comparing(Task::getUpdatedAt, Comparator.nullsLast(Comparator.naturalOrder()));
            case BY_COMPLETED -> Comparator.comparing(Task::getCompletedAt, Comparator.nullsLast(Comparator.naturalOrder()));
        };
    }
}