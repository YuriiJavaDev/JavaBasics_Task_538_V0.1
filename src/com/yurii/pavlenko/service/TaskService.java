package com.yurii.pavlenko.service;

import com.yurii.pavlenko.model.Task;

import java.util.List;

public interface TaskService {

    void addTask(String title);

    List<Task> getTasks();

    void deleteTask(int index);

    void deleteCompletedTasks();

    void clearAllTasks();

    void editTask(int index, String text);
}