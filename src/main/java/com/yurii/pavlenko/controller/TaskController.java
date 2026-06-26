package com.yurii.pavlenko.controller;

import com.yurii.pavlenko.model.Task;
import com.yurii.pavlenko.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
        logger.info("TaskController initialized.");
    }

    public void addTask(String title, String importance) {
        logger.info("Adding new task: '{}' with importance: {}", title, importance);
        service.addTask(title, importance);
    }

    public List<Task> getTasks() {
        return service.getTasks();
    }

    public void deleteTask(UUID id) {
        logger.info("Attempting to delete task with ID: {}", id);
        service.deleteTask(id);
    }

    public void deleteCompletedTasks() {
        logger.info("Deleting all completed tasks.");
        service.deleteCompletedTasks();
    }

    public void clearAllTasks() {
        logger.warn("CLEARING ALL TASKS requested!");
        service.clearAllTasks();
    }

    public void editTask(UUID id, Task task) {
        logger.info("Editing task with ID: {} and status: {}", id, task.isCompleted());
        service.editTask(id, task);
    }
}