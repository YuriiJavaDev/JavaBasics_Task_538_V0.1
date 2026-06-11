package main.com.yurii.pavlenko.service;

import main.com.yurii.pavlenko.model.Task;

import java.util.List;

public interface TaskService {

    void addTask(String title);

    List<Task> getTasks();

    void deleteTask(int index);

    void deleteCompletedTasks();

    void clearAllTasks();

    // Исправлено: принимаем объект Task
    void editTask(int index, Task task);
}