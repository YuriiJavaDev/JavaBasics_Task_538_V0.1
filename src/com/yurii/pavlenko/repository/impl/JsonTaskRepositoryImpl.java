package com.yurii.pavlenko.repository.impl;

import com.yurii.pavlenko.model.Task;
import com.yurii.pavlenko.repository.TaskRepository;

import java.util.ArrayList;
import java.util.List;

public class JsonTaskRepositoryImpl implements TaskRepository {

    @Override
    public void save(Task task) {

        System.out.println(
                "Сохраняем задачу в JSON-файл"
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
                "Читаем задачи из JSON-файла"
        );

        return new ArrayList<>();
    }
}
