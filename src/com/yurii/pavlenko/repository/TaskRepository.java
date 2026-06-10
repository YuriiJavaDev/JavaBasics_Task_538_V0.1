package com.yurii.pavlenko.repository;

import com.yurii.pavlenko.model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskRepository {
    void save(Task task);
    List<Task> findAll();
    void delete(int index);
    void update(int index, Task task);
    void clear();
    void deleteCompleted();
}
