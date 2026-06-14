package main.com.yurii.pavlenko.ui.panels;

import main.com.yurii.pavlenko.controller.TaskController;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

/**
 * Composite container component organizing application navigation tabs and viewport switches.
 */
public class MainTabbedPanel extends JPanel {

    private final JTabbedPane tabbedPane;

    public MainTabbedPanel(TaskController controller) {
        setLayout(new BorderLayout());
        tabbedPane = new JTabbedPane();

        initializeTabs(controller);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private void initializeTabs(TaskController controller) {

        tabbedPane.addTab("Tasks", new TaskPanel(controller));
        tabbedPane.addTab("Tools", new ToolsPanel());
        tabbedPane.addTab("AI Chat", new JPanel());
    }
}