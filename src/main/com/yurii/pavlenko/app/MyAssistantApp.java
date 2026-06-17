package main.com.yurii.pavlenko.app;

import main.com.yurii.pavlenko.controller.TaskController;
import main.com.yurii.pavlenko.repository.TaskRepository;
import main.com.yurii.pavlenko.repository.impl.JsonTaskRepositoryImpl;
// import com.yurii.pavlenko.repository.impl.DatabaseTaskRepositoryImpl;
import main.com.yurii.pavlenko.service.TaskService;
import main.com.yurii.pavlenko.service.impl.TaskServiceImpl;
import main.com.yurii.pavlenko.ui.frames.TaskFrame;
import main.com.yurii.pavlenko.utils.ConfigureUtil;

import javax.swing.*;

public class MyAssistantApp {

    public static void main(String[] args) {

        ConfigureUtil.configureLookAndFeel();
        ConfigureUtil.configureGlobalFonts();

        TaskRepository repo = new JsonTaskRepositoryImpl();
        // TaskRepository repo = new DatabaseTaskRepositoryImpl();

        TaskService service = new TaskServiceImpl(repo);
        TaskController controller = new TaskController(service);

        SwingUtilities.invokeLater(() -> new TaskFrame(controller));
    }
}
