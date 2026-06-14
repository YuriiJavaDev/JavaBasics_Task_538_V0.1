package main.com.yurii.pavlenko.ui.panels;

import main.com.yurii.pavlenko.ui.panels.tools.CalculatorPanel;
import main.com.yurii.pavlenko.ui.panels.tools.CurrencyConverterPanel;
import main.com.yurii.pavlenko.ui.panels.tools.WeatherPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Main tools viewport layout partitioning screen space between utilities.
 */
public class ToolsPanel extends JPanel {

    public ToolsPanel() {
        setLayout(new BorderLayout(12, 0));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        // Left Area: Large Weather Widget (Takes all remaining center-west space)
        WeatherPanel weatherPanel = new WeatherPanel();
        weatherPanel.setPreferredSize(new Dimension(500, 0)); // Lock width, height fluid
        add(weatherPanel, BorderLayout.CENTER);

        // Right Area: Container configured via GridBagLayout to allow flexible height rows
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setPreferredSize(new Dimension(700, 0)); // Lock width for right side utilities

        GridBagConstraints gridBagConstr = new GridBagConstraints();
        gridBagConstr.gridx = 0;          // Single column layout
        gridBagConstr.fill = GridBagConstraints.BOTH; // Expand components to fill their designated space
        gridBagConstr.insets = new Insets(0, 0, 12, 0); // Bottom margin spacing between blocks (replaces GridLayout gaps)

        // Row 1: Currency Converter (Takes less vertical space)
        gridBagConstr.gridy = 0;
        gridBagConstr.weightx = 1.0;
        gridBagConstr.weighty = 0.25;    // 35% of total vertical space allocated here
        rightPanel.add(new CurrencyConverterPanel(), gridBagConstr);

        // Row 2: Arithmetic Calculator (Takes more vertical space for the grid buttons)
        gridBagConstr.gridy = 1;
        gridBagConstr.weighty = 0.75;    // 65% of total vertical space allocated here
        gridBagConstr.insets = new Insets(0, 0, 0, 0); // Reset bottom margin for the last element
        rightPanel.add(new CalculatorPanel(), gridBagConstr);

        add(rightPanel, BorderLayout.EAST);
    }
}