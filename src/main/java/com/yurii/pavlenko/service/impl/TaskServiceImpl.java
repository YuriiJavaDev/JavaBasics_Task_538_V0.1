package com.yurii.pavlenko.service.impl;

import com.yurii.pavlenko.model.Task;
import com.yurii.pavlenko.repository.TaskRepository;
import com.yurii.pavlenko.service.TaskService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class TaskServiceImpl implements TaskService {
    private final TaskRepository repo;

    public TaskServiceImpl(TaskRepository repo) {
        this.repo = repo;
    }

    @Override
    public void addTask(String title, String importance) {
        if (title == null || title.isBlank()) return;
        Task task = new Task(title);
        task.setImportance(importance);
        repo.save(task);
    }

    @Override
    public List<Task> getTasks() {
        return repo.findAll();
    }

    @Override
    public void deleteTask(UUID id) {
        repo.delete(id);
    }

    @Override
    public void deleteCompletedTasks() {
        repo.deleteCompleted();
    }

    @Override
    public void clearAllTasks() {
        repo.clear();
    }

    @Override
    public void editTask(UUID id, Task task) {
        if (task == null || task.getTitle().isBlank()) return;
        task.setUpdatedAt(LocalDateTime.now());
        repo.update(id, task);
    }
}