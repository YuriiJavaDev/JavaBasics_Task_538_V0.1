package main.com.yurii.pavlenko.repository.impl;

import main.com.yurii.pavlenko.model.Task;
import main.com.yurii.pavlenko.repository.TaskRepository;

import java.util.ArrayList;
import java.util.List;

public class DatabaseTaskRepositoryImpl implements TaskRepository {

    @Override
    public void save(Task task) {

        System.out.println(
                "INSERT INTO tasks ..."
        );
    }

    @Override
    public void delete(int index) {

    }

    @Override
    public void deleteCompleted() {

    }

    @Override
    public void clear() {

    }

    @Override
    public void update(int index, Task task) {

    }

    @Override
    public List<Task> findAll() {

        System.out.println(
                "SELECT * FROM tasks"
        );

        return new ArrayList<>();
    }
}
