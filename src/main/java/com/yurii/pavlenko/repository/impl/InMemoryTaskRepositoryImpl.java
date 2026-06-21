package com.yurii.pavlenko.repository.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.yurii.pavlenko.model.Task;
import com.yurii.pavlenko.repository.TaskRepository;

/**
 * In-memory implementation of the TaskRepository operating on a volatile list structures.
 */
public class InMemoryTaskRepositoryImpl implements TaskRepository {

    private final List<Task> tasks = new ArrayList<>();

    @Override
    public void save(Task task) {
        if (task != null) {
            tasks.add(task);
        }
    }

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(tasks); // Return defensive copy to guard underlying storage state
    }

    // UPDATED: Replaced volatile int index mapping with immutable UUID state checks
    @Override
    public void delete(UUID id) {
        if (id == null) return;
        tasks.removeIf(task -> id.equals(task.getId()));
    }

    @Override
    public void deleteCompleted() {
        tasks.removeIf(Task::isCompleted);
    }

    @Override
    public void clear() {
        tasks.clear();
    }

    // UPDATED: Replaced index replacements with dynamic token tracking adjustments
    @Override
    public void update(UUID id, Task updatedTask) {
        if (id == null || updatedTask == null) return;

        for (int i = 0; i < tasks.size(); i++) {
            if (id.equals(tasks.get(i).getId())) {
                tasks.set(i, updatedTask);
                break;
            }
        }
    }
}