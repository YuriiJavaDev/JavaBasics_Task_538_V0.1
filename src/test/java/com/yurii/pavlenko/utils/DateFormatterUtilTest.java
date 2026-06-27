package com.yurii.pavlenko.utils;

import com.yurii.pavlenko.model.Task;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DateFormatterUtilTest {

    @Test
    void testGetFormattedDatesInfo_AllDatesPresent() {
        Task task = new Task();
        LocalDateTime now = LocalDateTime.of(2026, 6, 27, 10, 0);
        task.setCreatedAt(now);
        task.setUpdatedAt(now);
        task.setCompletedAt(now);
        task.setCompleted(true);

        String result = DateFormatterUtil.getFormattedDatesInfo(task);

        assertEquals(" [Task created 27.06.2026 10:00 ; Task edited 27.06.2026 10:00 ; Task completed 27.06.2026 10:00.]", result);
    }

    @Test
    void testGetFormattedDatesInfo_NullTask() {
        assertEquals("", DateFormatterUtil.getFormattedDatesInfo(null));
    }

    @Test
    void testGetFormattedDatesInfo_UnknownCreationDate() {
        Task task = new Task();
        task.setCreatedAt(null);

        String result = DateFormatterUtil.getFormattedDatesInfo(task);

        assertTrue(result.contains("date unknown"));
    }
}