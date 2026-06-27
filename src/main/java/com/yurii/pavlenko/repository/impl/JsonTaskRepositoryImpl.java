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

public class JsonTaskRepositoryImpl implements TaskRepository {
    private static final Logger logger = LoggerFactory.getLogger(JsonTaskRepositoryImpl.class);
    private File file;
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public JsonTaskRepositoryImpl() {
        this(new File("tasks.json"));
    }

    // For tests
    public JsonTaskRepositoryImpl(File file) {
        this.file = file;
    }

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

    @Override
    public void delete(UUID id) {
        if (id == null) return;
        executeUpdate("Delete task with ID: " + id, tasks -> tasks.removeIf(task -> id.equals(task.getId())));
    }

    @Override
    public void deleteCompleted() {
        executeUpdate("Delete completed tasks", tasks -> tasks.removeIf(Task::isCompleted));
    }

    @Override
    public void clear() {
        executeUpdate("Clear all tasks", List::clear);
    }

    @Override
    public void update(UUID id, Task updatedTask) {
        if (id == null || updatedTask == null) return;
        executeUpdate("Update task with ID: " + id, tasks -> {
            for (int i = 0; i < tasks.size(); i++) {
                if (id.equals(tasks.get(i).getId())) {
                    tasks.set(i, updatedTask);
                    break;
                }
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