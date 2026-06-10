package com.yurii.pavlenko.controller;

import com.yurii.pavlenko.model.Task;
import com.yurii.pavlenko.service.TaskService;

import java.util.List;

public class TaskController {

    //Controller хранит ссылку на Service
    private TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    public void onAddButtonClicked(String textFromUI) {
        service.addTask(textFromUI);
    }

    public List<Task> getTasks() {
        return service.getTasks();
    }

    public void deleteTask(int index) {
        service.deleteTask(index);
    }

    public void deleteCompletedTasks() {
        service.deleteCompletedTasks();
    }

    public void clearAllTasks() {
        service.clearAllTasks();
    }

    public void editTask(int index, String text) {
        service.editTask(index, text);
    }
}
