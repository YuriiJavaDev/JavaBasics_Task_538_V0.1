package com.yurii.pavlenko.repository.impl;

import java.util.ArrayList;
import java.util.List;

import com.yurii.pavlenko.model.Task;
import com.yurii.pavlenko.repository.TaskRepository;

public class InMemoryTaskRepositoryImpl implements TaskRepository {

    private List<Task> tasks = new ArrayList<>();

    public void save(Task task) {
        tasks.add(task);
    }

    public List<Task> findAll() {
        return tasks;
    }

    @Override
    public void delete(int index) {
        tasks.remove(index);
    }

    @Override
    public void deleteCompleted() {
        tasks.removeIf(Task::isCompleted);
    }

    @Override
    public void clear() {
        tasks.clear();
    }

    @Override
    public void update(int index, Task task) {
        tasks.set(index, task);
    }
}
