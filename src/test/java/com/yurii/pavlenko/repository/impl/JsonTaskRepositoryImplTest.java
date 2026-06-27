package com.yurii.pavlenko.repository.impl;

import com.yurii.pavlenko.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class JsonTaskRepositoryImplTest {

    private JsonTaskRepositoryImpl repository;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        File tempFile = tempDir.resolve("tasks_test.json").toFile();
        repository = new JsonTaskRepositoryImpl(tempFile);
    }

    @Test
    void testSaveAndFindAll() {
        Task task = new Task("Test Task");
        repository.save(task);

        List<Task> tasks = repository.findAll();
        assertEquals(1, tasks.size());
        assertEquals("Test Task", tasks.get(0).getTitle());
    }

    @Test
    void testDelete() {
        Task task = new Task("Task to delete");
        repository.save(task);
        repository.delete(task.getId());

        assertTrue(repository.findAll().isEmpty());
    }
}