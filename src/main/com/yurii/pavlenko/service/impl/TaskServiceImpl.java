package main.com.yurii.pavlenko.service.impl;

import main.com.yurii.pavlenko.model.Task;
import main.com.yurii.pavlenko.repository.TaskRepository;
import main.com.yurii.pavlenko.service.TaskService;
import java.util.List;

public class TaskServiceImpl implements TaskService {
    private final TaskRepository repo;

    public TaskServiceImpl(TaskRepository repo) {
        this.repo = repo;
    }

    @Override
    public void addTask(String title) {
        if (title == null || title.isBlank()) return;
        repo.save(new Task(title));
    }

    @Override
    public List<Task> getTasks() { return repo.findAll(); }

    @Override
    public void deleteTask(int index) { repo.delete(index); }

    @Override
    public void deleteCompletedTasks() { repo.deleteCompleted(); }

    @Override
    public void clearAllTasks() { repo.clear(); }

    @Override
    public void editTask(int index, Task task) {
        if (task == null || task.getTitle().isBlank()) return;
        repo.update(index, task);
    }
}
