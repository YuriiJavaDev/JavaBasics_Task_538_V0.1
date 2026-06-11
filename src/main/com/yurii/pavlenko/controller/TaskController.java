package main.com.yurii.pavlenko.controller;

import main.com.yurii.pavlenko.model.Task;
import main.com.yurii.pavlenko.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    private TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
        logger.info("TaskController initialized.");
    }

    public void onAddButtonClicked(String textFromUI) {
        logger.info("Adding new task: '{}'", textFromUI);
        service.addTask(textFromUI);
    }

    public List<Task> getTasks() {
        return service.getTasks();
    }

    public void deleteTask(int index) {
        logger.info("Attempting to delete task at index: {}", index);
        service.deleteTask(index);
    }

    public void deleteCompletedTasks() {
        logger.info("Deleting all completed tasks.");
        service.deleteCompletedTasks();
    }

    public void clearAllTasks() {
        logger.warn("CLEARING ALL TASKS requested!");
        service.clearAllTasks();
    }

    public void editTask(int index, Task task) {
        logger.info("Editing task at index: {} with status: {}", index, task.isCompleted());
        service.editTask(index, task);
    }
}
