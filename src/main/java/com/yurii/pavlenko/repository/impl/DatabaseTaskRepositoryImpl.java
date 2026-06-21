package com.yurii.pavlenko.repository.impl;

import com.yurii.pavlenko.model.Task;
import com.yurii.pavlenko.repository.TaskRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Database-backed implementation of the TaskRepository targeting persistent SQL operations.
 */
public class DatabaseTaskRepositoryImpl implements TaskRepository {

    @Override
    public void save(Task task) {
        System.out.println(
                "INSERT INTO tasks (id, title, completed, created_at, updated_at, completed_at) VALUES (?, ?, ?, ?, ?, ?)"
        );
    }

    // UPDATED: Changed from int index to UUID id to comply with the updated interface contract
    @Override
    public void delete(UUID id) {
        System.out.println(
                "DELETE FROM tasks WHERE id = '" + id + "'"
        );
    }

    @Override
    public void deleteCompleted() {
        System.out.println(
                "DELETE FROM tasks WHERE completed = true"
        );
    }

    @Override
    public void clear() {
        System.out.println(
                "TRUNCATE TABLE tasks"
        );
    }

    // UPDATED: Changed from int index to UUID id to comply with the updated interface contract
    @Override
    public void update(UUID id, Task task) {
        System.out.println(
                "UPDATE tasks SET title = ?, completed = ?, updated_at = ?, completed_at = ? WHERE id = '" + id + "'"
        );
    }

    @Override
    public List<Task> findAll() {
        System.out.println(
                "SELECT * FROM tasks"
        );
        return new ArrayList<>();
    }
}