package com.yurii.pavlenko.service;

import com.yurii.pavlenko.model.Task;

import java.util.List;
import java.util.UUID;

public interface TaskService {

    void addTask(String title, String importance);

    List<Task> getTasks();

    void deleteTask(UUID id);

    void deleteCompletedTasks();

    void clearAllTasks();

    void editTask(UUID id, Task task);
}