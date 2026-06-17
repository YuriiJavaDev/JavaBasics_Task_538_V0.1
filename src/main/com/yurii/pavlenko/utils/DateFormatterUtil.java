package main.com.yurii.pavlenko.utils;

import main.com.yurii.pavlenko.model.Task;

import java.time.format.DateTimeFormatter;

/**
 * Utility class responsible for formatting task-related timestamps for the UI layer.
 */
public final class DateFormatterUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private DateFormatterUtil() {}

    /**
     * Formats the creation, modification, and completion timestamps of a task into a unified string.
     */
    public static String getFormattedDatesInfo(Task task) {
        if (task == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        // Creation timestamp tracking
        if (task.getCreatedAt() != null) {
            sb.append(" [Task created ").append(task.getCreatedAt().format(FORMATTER));
        } else {
            sb.append(" [Task created: date unknown");
        }

        // Modification timestamp tracking
        if (task.getUpdatedAt() != null) {
            sb.append(" ; Task edited ").append(task.getUpdatedAt().format(FORMATTER));
        }

        // Completion timestamp tracking (only shows if task is currently completed)
        if (task.isCompleted() && task.getCompletedAt() != null) {
            sb.append(" ; Task completed ").append(task.getCompletedAt().format(FORMATTER));
        }

        sb.append(".]");
        return sb.toString();
    }
}