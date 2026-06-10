package com.yurii.pavlenko.service.impl;

import com.yurii.pavlenko.model.Task;
import com.yurii.pavlenko.repository.TaskRepository;
import com.yurii.pavlenko.service.TaskService;

import java.util.List;

public class TaskServiceImpl implements TaskService {

    private final TaskRepository repo;

    public TaskServiceImpl(TaskRepository repo) {
        this.repo = repo;
    }

    @Override
    public void addTask(String title) {
        if (title == null || title.isEmpty()) {
            return;
        }
        repo.save(new Task(title));
    }

    @Override
    public List<Task> getTasks() {
        return repo.findAll();
    }

    @Override
    public void deleteTask(int index) {
        repo.delete(index);
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
    public void editTask(int index, String text) {
        if (text == null || text.isBlank()) {
            return;
        }

        repo.update(index, new Task(text));
    }
}
