package com.yurii.pavlenko.repository;

import com.yurii.pavlenko.model.Task;
import java.util.List;
import java.util.UUID;

public interface TaskRepository {
    void save(Task task);
    List<Task> findAll();
    void delete(UUID id); // Updated
    void update(UUID id, Task task); // Updated
    void deleteCompleted();
    void clear();
}