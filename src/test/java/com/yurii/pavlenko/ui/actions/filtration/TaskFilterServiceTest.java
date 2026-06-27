package com.yurii.pavlenko.ui.actions.filtration;

import com.yurii.pavlenko.model.Task;
import com.yurii.pavlenko.utils.FilterStatus;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TaskFilterServiceTest {

    @Test
    void testFilterAll() {
        Task t1 = new Task("T1");
        t1.setCompleted(true);
        List<Task> tasks = Arrays.asList(t1, new Task("T2"));

        List<Task> result = TaskFilterService.filter(tasks, FilterStatus.ALL);
        assertEquals(2, result.size());
    }

    @Test
    void testFilterCompleted() {
        Task t1 = new Task("T1");
        t1.setCompleted(true);
        Task t2 = new Task("T2");
        t2.setCompleted(false);
        List<Task> tasks = Arrays.asList(t1, t2);

        List<Task> result = TaskFilterService.filter(tasks, FilterStatus.COMPLETED);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isCompleted());
    }

    @Test
    void testFilterActive() {
        Task t1 = new Task("T1");
        t1.setCompleted(true);
        Task t2 = new Task("T2");
        t2.setCompleted(false);
        List<Task> tasks = Arrays.asList(t1, t2);

        List<Task> result = TaskFilterService.filter(tasks, FilterStatus.ACTIVE);
        assertEquals(1, result.size());
        assertFalse(result.get(0).isCompleted());
    }
}