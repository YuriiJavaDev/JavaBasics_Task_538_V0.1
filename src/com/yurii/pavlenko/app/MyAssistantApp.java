package com.yurii.pavlenko.app;

import com.yurii.pavlenko.controller.TaskController;
import com.yurii.pavlenko.repository.TaskRepository;
import com.yurii.pavlenko.repository.impl.InMemoryTaskRepositoryImpl;
// import com.yurii.pavlenko.repository.impl.JsonTaskRepositoryImpl;
// import com.yurii.pavlenko.repository.impl.DatabaseTaskRepositoryImpl;
import com.yurii.pavlenko.service.TaskService;
import com.yurii.pavlenko.service.impl.TaskServiceImpl;
import com.yurii.pavlenko.ui.frames.TaskFrame;
import com.yurii.pavlenko.util.Util;

import javax.swing.*;

public class MyAssistantApp {

    public static void main(String[] args) {

        Util.configureLookAndFeel();
        Util.configureGlobalFonts();

        TaskRepository repo = new InMemoryTaskRepositoryImpl();

        // TaskRepository repo = new JsonTaskRepositoryImpl();
        // TaskRepository repo = new DatabaseTaskRepositoryImpl();

        TaskService service = new TaskServiceImpl(repo);
        TaskController controller = new TaskController(service);

        SwingUtilities.invokeLater(() -> new TaskFrame(controller));
    }
}
