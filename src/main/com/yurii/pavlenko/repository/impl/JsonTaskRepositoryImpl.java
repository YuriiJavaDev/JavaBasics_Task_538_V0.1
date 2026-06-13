package main.com.yurii.pavlenko.repository.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import main.com.yurii.pavlenko.model.Task;
import main.com.yurii.pavlenko.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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

    @Override
    public void delete(int index) {
        executeUpdate("Delete task at index: " + index, tasks -> {
            if (index >= 0 && index < tasks.size()) tasks.remove(index);
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

    @Override
    public void update(int index, Task task) {
        // Мы просто передаем действие в executeUpdate.
        // Он сам считает список, применит наше изменение и сам запишет файл.
        executeUpdate("Update task at index: " + index, tasks -> {
            if (index >= 0 && index < tasks.size()) {
                tasks.set(index, task);
                logger.info("DEBUG: The task in the list has been replaced. New status: {}", task.isCompleted());
            } else {
                logger.error("DEBUG: ERROR: Index {} out of bounds of size list {}", index, tasks.size());
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