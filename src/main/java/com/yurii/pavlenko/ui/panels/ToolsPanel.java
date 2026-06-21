package com.yurii.pavlenko.ui.panels;

import com.yurii.pavlenko.ui.panels.tools.CalculatorPanel;
import com.yurii.pavlenko.ui.panels.tools.CurrencyConverterPanel;
import com.yurii.pavlenko.ui.panels.tools.WeatherPanel;
import com.yurii.pavlenko.model.tools.calculator.CalculatorModel;
import com.yurii.pavlenko.service.tools.calculator.CalculatorService;
import com.yurii.pavlenko.service.tools.calculator.impl.CalculatorServiceImpl;
import com.yurii.pavlenko.controller.tools.calculator.CalculatorController;

// Weather architectural imports
import com.yurii.pavlenko.service.tools.weather.WeatherService;
import com.yurii.pavlenko.service.tools.weather.impl.WeatherServiceImpl;
import com.yurii.pavlenko.controller.tools.weather.WeatherController;

// New currency architectural imports
import com.yurii.pavlenko.service.tools.currency.CurrencyService;
import com.yurii.pavlenko.service.tools.currency.impl.CurrencyServiceImpl;
import com.yurii.pavlenko.controller.tools.currency.CurrencyController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Main tools viewport layout partitioning screen space between utilities.
 * Configured with a stable fixed calculator widget and elastic rubber currency and weather widgets.
 */
public class ToolsPanel extends JPanel {

    public ToolsPanel() {
        setLayout(new BorderLayout(12, 0));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        // =====================================================================
        // CLEAN ARCHITECTURE INITIALIZATION FOR WEATHER FORECAST TOOL
        // =====================================================================
        WeatherPanel weatherPanel = new WeatherPanel();
        WeatherService weatherService = new WeatherServiceImpl();
        WeatherController weatherController = new WeatherController(weatherService, weatherPanel);

        // Left Area: Large Weather Widget (RUBBER - takes all center-west space fluidly)
        weatherPanel.setPreferredSize(new Dimension(450, 0)); // Fluid height, elastic stretch
        add(weatherPanel, BorderLayout.CENTER);

        // Right Area: Structural container for Currency Converter and Calculator
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setPreferredSize(new Dimension(650, 0)); // Main standard width allocation

        GridBagConstraints gridBagConstr = new GridBagConstraints();
        gridBagConstr.gridx = 0;          // Single column stack
        gridBagConstr.fill = GridBagConstraints.BOTH; // Expand elements to fill allocated layout lanes
        gridBagConstr.weightx = 1.0;

        // =====================================================================
        // CLEAN ARCHITECTURE INITIALIZATION FOR CURRENCY CONVERTER TOOL
        // =====================================================================
        CurrencyService currencyService = new CurrencyServiceImpl();
        CurrencyConverterPanel currencyPanel = new CurrencyConverterPanel();
        CurrencyController currencyController = new CurrencyController(currencyPanel, currencyService);
        currencyPanel.registerController(currencyController);

        // =====================================================================
        // Row 1: Currency Converter (RUBBER - Absorbs all vertical variations)
        // =====================================================================
        gridBagConstr.gridy = 0;
        gridBagConstr.weighty = 1.0;    // Elastic vertical stretch behavior enabled
        gridBagConstr.insets = new Insets(0, 0, 12, 0); // Bottom margin spacing before calculator
        rightPanel.add(currencyPanel, gridBagConstr);

        // =====================================================================
        // CLEAN ARCHITECTURE INITIALIZATION FOR CALCULATOR TOOL
        // =====================================================================
        CalculatorService calcService = new CalculatorServiceImpl();
        CalculatorModel calcModel = new CalculatorModel();
        CalculatorPanel calculatorPanel = new CalculatorPanel();
        CalculatorController calcController = new CalculatorController(calcModel, calcService, calculatorPanel);

        // =====================================================================
        // Row 2: Arithmetic Calculator (MONOLITHIC - Rigid Fixed Boundaries)
        // =====================================================================
        gridBagConstr.gridy = 1;
        gridBagConstr.weighty = 0.0;    // Strict fixed size execution, no elastic scaling
        gridBagConstr.insets = new Insets(0, 0, 0, 0); // Reset padding for layout foundation

        rightPanel.add(calculatorPanel, gridBagConstr);

        add(rightPanel, BorderLayout.EAST);
    }
}