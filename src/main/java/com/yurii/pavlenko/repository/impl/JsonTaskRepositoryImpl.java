package com.yurii.pavlenko.repository.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.yurii.pavlenko.model.Task;
import com.yurii.pavlenko.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * JSON-backed implementation of the TaskRepository using Jackson for object serialization.
 */
public class JsonTaskRepositoryImpl implements TaskRepository {
    private static final Logger logger = LoggerFactory.getLogger(JsonTaskRepositoryImpl.class);
    private final File file = new File("tasks.json");
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public List<Task> findAll() {
        if (!file.exists()) return new ArrayList<>();
        try {
            return mapper.readValue(file, new TypeReference<List<Task>>() {});
        } catch (IOException e) {
            logger.error("Error reading tasks.json: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public void save(Task task) {
        executeUpdate("Save task: " + task.getTitle(), tasks -> tasks.add(task));
    }

    // UPDATED: Now removes tasks matching the unique identity key
    @Override
    public void delete(UUID id) {
        if (id == null) return;
        executeUpdate("Delete task with ID: " + id, tasks -> {
            boolean removed = tasks.removeIf(task -> id.equals(task.getId()));
            if (removed) {
                logger.info("DEBUG: Task with ID {} successfully removed from the temporary list.", id);
            } else {
                logger.error("DEBUG: ERROR: Task with ID {} not found for deletion.", id);
            }
        });
    }

    @Override
    public void deleteCompleted() {
        executeUpdate("Delete completed tasks", tasks -> tasks.removeIf(Task::isCompleted));
    }

    @Override
    public void clear() {
        executeUpdate("Clear all tasks", List::clear);
    }

    // UPDATED: Now replaces tasks based on matching UUID tokens instead of volatile indices
    @Override
    public void update(UUID id, Task updatedTask) {
        if (id == null || updatedTask == null) return;
        executeUpdate("Update task with ID: " + id, tasks -> {
            boolean found = false;
            for (int i = 0; i < tasks.size(); i++) {
                if (id.equals(tasks.get(i).getId())) {
                    tasks.set(i, updatedTask);
                    logger.info("DEBUG: The task in the list has been replaced. New status: {}", updatedTask.isCompleted());
                    found = true;
                    break;
                }
            }
            if (!found) {
                logger.error("DEBUG: ERROR: Task with ID {} not found for updating operations.", id);
            }
        });
    }

    private void executeUpdate(String actionName, Consumer<List<Task>> action) {
        List<Task> tasks = findAll();
        action.accept(tasks);
        saveAll(tasks);
        logger.info("SUCCESS: {}", actionName);
    }

    private void saveAll(List<Task> tasks) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, tasks);
        } catch (IOException e) {
            logger.error("Error saving tasks: {}", e.getMessage());
        }
    }
}