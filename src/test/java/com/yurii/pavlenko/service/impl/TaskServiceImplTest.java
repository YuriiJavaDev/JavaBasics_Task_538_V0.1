package com.yurii.pavlenko.service.impl;

import com.yurii.pavlenko.model.Task;
import com.yurii.pavlenko.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceImplTest {

    private TaskRepository mockRepo;
    private TaskServiceImpl taskService;

    @BeforeEach
    void setUp() {
        mockRepo = mock(TaskRepository.class);
        taskService = new TaskServiceImpl(mockRepo);
    }

    @Test
    void testAddTask_ValidTitle_SavesTask() {
        taskService.addTask("Test Task", "High");

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(mockRepo).save(taskCaptor.capture());

        assertEquals("Test Task", taskCaptor.getValue().getTitle());
        assertEquals("High", taskCaptor.getValue().getImportance());
    }

    @Test
    void testAddTask_EmptyTitle_DoesNotSave() {
        taskService.addTask("", "High");
        verify(mockRepo, never()).save(any());
    }

    @Test
    void testGetTasks_ReturnsList() {
        taskService.getTasks();
        verify(mockRepo).findAll();
    }

    @Test
    void testDeleteTask_CallsRepository() {
        UUID id = UUID.randomUUID();
        taskService.deleteTask(id);
        verify(mockRepo).delete(id);
    }

    @Test
    void testEditTask_ValidTask_UpdatesRepo() {
        UUID id = UUID.randomUUID();
        Task task = new Task("Updated Title");

        taskService.editTask(id, task);

        verify(mockRepo).update(eq(id), any(Task.class));
        assertNotNull(task.getUpdatedAt());
    }

    @Test
    void testEditTask_NullOrEmptyTask_DoesNotUpdate() {
        taskService.editTask(UUID.randomUUID(), null);
        verify(mockRepo, never()).update(any(), any());
    }
}