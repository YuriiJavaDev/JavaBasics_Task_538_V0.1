package com.yurii.pavlenko.ui.actions.sorting;

import com.yurii.pavlenko.model.Task;
import com.yurii.pavlenko.utils.SortOrderOption;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskComparatorFactoryTest {

    @Test
    void testSortAlphabetical() {
        Task t1 = new Task("Apple");
        Task t2 = new Task("Banana");
        List<Task> tasks = new ArrayList<>(List.of(t2, t1));

        tasks.sort(TaskComparatorFactory.getComparator(SortOrderOption.A_Z));
        assertEquals("Apple", tasks.get(0).getTitle());

        tasks.sort(TaskComparatorFactory.getComparator(SortOrderOption.Z_A));
        assertEquals("Banana", tasks.get(0).getTitle());
    }

    @Test
    void testSortImportance() {
        Task t1 = new Task("Low"); t1.setImportance("Low");
        Task t2 = new Task("Urgent"); t2.setImportance("Urgent");
        List<Task> tasks = new ArrayList<>(List.of(t1, t2));

        tasks.sort(TaskComparatorFactory.getComparator(SortOrderOption.BY_IMPORTANCE));
        assertEquals("Urgent", tasks.get(0).getTitle());
    }

    @Test
    void testSortStatus() {
        Task t1 = new Task("Active"); t1.setCompleted(false);
        Task t2 = new Task("Done"); t2.setCompleted(true);
        List<Task> tasks = new ArrayList<>(List.of(t2, t1));

        tasks.sort(TaskComparatorFactory.getComparator(SortOrderOption.BY_STATUS));
        // false (Active) < true (Completed) в natural order
        assertEquals("Active", tasks.get(0).getTitle());
    }
}