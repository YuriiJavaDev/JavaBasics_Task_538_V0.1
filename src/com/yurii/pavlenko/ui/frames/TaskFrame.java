package com.yurii.pavlenko.ui.frames;

import com.yurii.pavlenko.controller.TaskController;
import com.yurii.pavlenko.ui.panels.MainTabbedPanel;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Image;
import java.io.IOException;
import java.util.Objects;

/**
 * Structural window shell configuring screen boundaries, centering positions, and global container mounting.
 */
public class TaskFrame extends JFrame {

    public TaskFrame(TaskController controller) {
        super("My Assistant");
        initComponents(controller);
        configureFrame();
    }

    private void initComponents(TaskController controller) {
        MainTabbedPanel mainTabbedPanel = new MainTabbedPanel(controller);
        add(mainTabbedPanel, BorderLayout.CENTER);
        loadWindowIcon();
    }

    private void loadWindowIcon() {
        try {
            String path = "src/resources/images/assistant01.png";
            java.io.File file = new java.io.File(path);

            if (file.exists()) {
                Image icon = ImageIO.read(file);
                setIconImage(icon);
            } else {
                System.err.println("Icon file not found at path: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Error reading icon: " + e.getMessage());
        }
    }

    private void configureFrame() {
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}