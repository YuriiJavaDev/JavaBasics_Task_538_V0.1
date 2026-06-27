package com.yurii.pavlenko.controller;

import com.yurii.pavlenko.model.Task;
import com.yurii.pavlenko.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TaskControllerTest {

    private TaskService mockService;
    private TaskController controller;

    @BeforeEach
    void setUp() {
        mockService = mock(TaskService.class);
        controller = new TaskController(mockService);
    }

    @Test
    void testAddTask_DelegatesToService() {
        controller.addTask("New Task", "High");
        verify(mockService).addTask("New Task", "High");
    }

    @Test
    void testGetTasks_ReturnsDataFromService() {
        Task task = new Task("Test");
        when(mockService.getTasks()).thenReturn(Collections.singletonList(task));

        List<Task> tasks = controller.getTasks();

        assertEquals(1, tasks.size());
        assertEquals("Test", tasks.get(0).getTitle());
        verify(mockService).getTasks();
    }

    @Test
    void testDeleteTask_DelegatesToService() {
        UUID id = UUID.randomUUID();
        controller.deleteTask(id);
        verify(mockService).deleteTask(id);
    }

    @Test
    void testDeleteCompletedTasks_DelegatesToService() {
        controller.deleteCompletedTasks();
        verify(mockService).deleteCompletedTasks();
    }

    @Test
    void testClearAllTasks_DelegatesToService() {
        controller.clearAllTasks();
        verify(mockService).clearAllTasks();
    }

    @Test
    void testEditTask_DelegatesToService() {
        UUID id = UUID.randomUUID();
        Task task = new Task("Edit");
        controller.editTask(id, task);
        verify(mockService).editTask(id, task);
    }
}