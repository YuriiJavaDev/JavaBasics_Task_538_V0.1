package main.com.yurii.pavlenko.ui.panels.tools;

import main.com.yurii.pavlenko.utils.CalculatorHotkeyConfigurator;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * High-performance arithmetic and engineering calculator layout.
 * Integrates dedicated radio-button context switches for angular metrics.
 */
public class CalculatorPanel extends JPanel {

    private JLabel display;
    private JLabel memoryLabel;
    private JLabel formulaLabel;
    private JPanel engineeringGrid;
    private JPanel classicPad;

    private JRadioButton degRadio;
    private JRadioButton radRadio;
    private ButtonGroup angleGroup;

    private JPanel displayPanel;

    private final Map<String, JButton> buttonMap = new HashMap<>();
    private ActionListener globalController;

    private final Color focusBorderColor = new Color(145, 175, 205);
    private final Color defaultBorderColor = Color.GRAY;

    public CalculatorPanel() {
        setBorder(BorderFactory.createTitledBorder("Arithmetic & Engineering Calculator"));
        setLayout(new BorderLayout(12, 12));

        initializeDisplay();
        initializeCalculatorBody();

        CalculatorHotkeyConfigurator.configureHotkeys(this, buttonMap);

        setFocusable(true);

        this.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
            }
        });

        this.addFocusListener(new java.awt.event.FocusListener() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                activateCalculatorFocus();
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                Component opposite = e.getOppositeComponent();
                if (opposite instanceof JButton && buttonMap.containsValue(opposite)) {
                    return;
                }
                deactivateCalculatorFocus();
            }
        });
    }

    private void activateCalculatorFocus() {
        displayPanel.setBorder(createUniformDisplayBorder(focusBorderColor));
        displayPanel.repaint();
    }

    private void deactivateCalculatorFocus() {
        displayPanel.setBorder(createUniformDisplayBorder(defaultBorderColor));
        displayPanel.repaint();
    }

    private Border createUniformDisplayBorder(Color borderColor) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(6, 6, 6, 6),
                BorderFactory.createLineBorder(borderColor, 4)
        );
    }

    private JPanel createAngleLayoutComponent() {
        JPanel radioPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 15, 0));
        radioPanel.setOpaque(false);

        degRadio = new JRadioButton("Deg", true);
        radRadio = new JRadioButton("Rad", false);

        Font radioFont = new Font("Segoe UI", Font.PLAIN, 12);
        degRadio.setFont(radioFont);
        radRadio.setFont(radioFont);

        degRadio.setFocusable(false);
        radRadio.setFocusable(false);

        // Чтобы клик по радиокнопкам возвращал фокус на калькулятор для работы горячих клавиш
        java.awt.event.MouseAdapter radioFocusFix = new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
            }
        };
        degRadio.addMouseListener(radioFocusFix);
        radRadio.addMouseListener(radioFocusFix);

        angleGroup = new ButtonGroup();
        angleGroup.add(degRadio);
        angleGroup.add(radRadio);

        radioPanel.add(degRadio);
        radioPanel.add(radRadio);

        return radioPanel;
    }

    private void initializeDisplay() {
        displayPanel = new JPanel(new BorderLayout());
        displayPanel.setBackground(Color.WHITE);
        displayPanel.setPreferredSize(new Dimension(0, 85));
        displayPanel.setBorder(createUniformDisplayBorder(defaultBorderColor));

        displayPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                requestFocusInWindow();
            }
        });

        JPanel textContainer = new JPanel(new BorderLayout());
        textContainer.setOpaque(false);
        textContainer.setBorder(BorderFactory.createEmptyBorder(2, 12, 2, 12));

        JPanel topLabelsPanel = new JPanel(new BorderLayout());
        topLabelsPanel.setOpaque(false);

        textContainer.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                requestFocusInWindow();
            }
        });

        memoryLabel = new JLabel(" ");
        memoryLabel.setFont(new Font("Consolas", Font.PLAIN, 12));
        memoryLabel.setForeground(Color.GRAY);
        topLabelsPanel.add(memoryLabel, BorderLayout.WEST);

        formulaLabel = new JLabel(" ");
        formulaLabel.setFont(new Font("Consolas", Font.ITALIC, 12));
        formulaLabel.setForeground(new Color(100, 130, 160));
        topLabelsPanel.add(formulaLabel, BorderLayout.EAST);

        textContainer.add(topLabelsPanel, BorderLayout.NORTH);

        display = new JLabel("0");
        display.setOpaque(false);
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setVerticalAlignment(SwingConstants.CENTER);
        display.setFont(new Font("Consolas", Font.BOLD, 32));

        display.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                requestFocusInWindow();
            }
        });

        textContainer.add(display, BorderLayout.CENTER);
        displayPanel.add(textContainer, BorderLayout.CENTER);
        add(displayPanel, BorderLayout.NORTH);
    }

    private void initializeCalculatorBody() {
        JPanel bodyPanel = new JPanel(new GridBagLayout());
        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.fill = GridBagConstraints.BOTH;
        mainGbc.weighty = 1.0;

        JPanel leftEngineeringWing = new JPanel(new BorderLayout(0, 4));

        JPanel memoryControlPanel = new JPanel(new GridBagLayout());
        GridBagConstraints memGbc = new GridBagConstraints();
        memGbc.fill = GridBagConstraints.BOTH;
        memGbc.weighty = 1.0;
        memGbc.insets = new Insets(3, 3, 3, 3);

        JButton btnC = createStyledButton("C", true);
        JButton btnToggleM = createStyledButton("M+", true);
        JButton btnMR = createStyledButton("MR", true);
        JButton btnMC = createStyledButton("MC", true);

        btnC.setActionCommand("C");
        btnToggleM.setActionCommand("M+");
        btnMR.setActionCommand("MR");
        btnMC.setActionCommand("MC");

        Dimension singleMemSize = new Dimension(60, 45);
        Dimension doubleMemSize = new Dimension((60 * 2) + 6, 45);

        btnC.setPreferredSize(singleMemSize);
        btnC.setMinimumSize(singleMemSize);
        btnToggleM.setPreferredSize(singleMemSize);
        btnToggleM.setMinimumSize(singleMemSize);
        btnMR.setPreferredSize(singleMemSize);
        btnMR.setMinimumSize(singleMemSize);
        btnMC.setPreferredSize(doubleMemSize);
        btnMC.setMinimumSize(doubleMemSize);

        memGbc.gridy = 0;
        memGbc.gridx = 0; memGbc.gridwidth = 1; memGbc.weightx = 1.0; memoryControlPanel.add(btnC, memGbc);
        memGbc.gridx = 1; memGbc.gridwidth = 1; memGbc.weightx = 1.0; memoryControlPanel.add(btnToggleM, memGbc);
        memGbc.gridx = 2; memGbc.gridwidth = 1; memGbc.weightx = 1.0; memoryControlPanel.add(btnMR, memGbc);
        memGbc.gridx = 3; memGbc.gridwidth = 2; memGbc.weightx = 2.0; memoryControlPanel.add(btnMC, memGbc);

        Dimension strictMemPanelSize = new Dimension(264, 51);
        memoryControlPanel.setPreferredSize(strictMemPanelSize);
        memoryControlPanel.setMinimumSize(strictMemPanelSize);
        memoryControlPanel.setMaximumSize(strictMemPanelSize);

        // Промежуточный контейнер для верхней части: Сброс/Память + Новые Радиокнопки
        JPanel topCombinedPanel = new JPanel(new BorderLayout(0, 2));
        topCombinedPanel.setOpaque(false);
        topCombinedPanel.add(memoryControlPanel, BorderLayout.NORTH);
        topCombinedPanel.add(createAngleLayoutComponent(), BorderLayout.SOUTH);

        leftEngineeringWing.add(topCombinedPanel, BorderLayout.NORTH);

        // Уменьшаем высоту инженерных кнопок с 45px до 38px, чтобы освободить место под тумблер
        engineeringGrid = new JPanel(new GridLayout(5, 5, 6, 6));
        String[] mathButtons = {
                "sin",  "cos",  "tan",  "π",    "e",
                "asin", "acos", "atan", "(",    ")",
                "x²",   "x³",   "x^y",  "10^x", "1/x",
                "ln",   "log",  "exp",  "sqrt", "cbrt",
                "n!",   "abs",  "mod",  "Rand", "Ans"
        };

        Dimension engButtonSize = new Dimension(60, 38);
        for (String txt : mathButtons) {
            JButton engBtn = createStyledButton(txt, true);
            engBtn.setPreferredSize(engButtonSize);
            engBtn.setMinimumSize(engButtonSize);
            engineeringGrid.add(engBtn);
        }

        leftEngineeringWing.add(engineeringGrid, BorderLayout.CENTER);

        mainGbc.gridx = 0;
        mainGbc.weightx = 1.0;
        bodyPanel.add(leftEngineeringWing, mainGbc);

        JPanel separatorContainer = new JPanel(new BorderLayout());
        JSeparator verticalLine = new JSeparator(JSeparator.VERTICAL);
        verticalLine.setForeground(Color.LIGHT_GRAY);
        separatorContainer.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 6));
        separatorContainer.add(verticalLine, BorderLayout.CENTER);

        mainGbc.gridx = 1;
        mainGbc.weightx = 0.0;
        bodyPanel.add(separatorContainer, mainGbc);

        classicPad = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        Dimension baseButtonSize = new Dimension(60, 60);

        addClassicButton(classicPad, gbc, "%", 0, 0, 1, 1, baseButtonSize);
        addClassicButton(classicPad, gbc, "/", 1, 0, 1, 1, baseButtonSize);
        addClassicButton(classicPad, gbc, "*", 2, 0, 1, 1, baseButtonSize);
        addClassicButton(classicPad, gbc, "-", 3, 0, 1, 1, baseButtonSize);

        addClassicButton(classicPad, gbc, "7", 0, 1, 1, 1, baseButtonSize);
        addClassicButton(classicPad, gbc, "8", 1, 1, 1, 1, baseButtonSize);
        addClassicButton(classicPad, gbc, "9", 2, 1, 1, 1, baseButtonSize);
        addClassicButton(classicPad, gbc, "+", 3, 1, 1, 2, baseButtonSize);

        addClassicButton(classicPad, gbc, "4", 0, 2, 1, 1, baseButtonSize);
        addClassicButton(classicPad, gbc, "5", 1, 2, 1, 1, baseButtonSize);
        addClassicButton(classicPad, gbc, "6", 2, 2, 1, 1, baseButtonSize);

        addClassicButton(classicPad, gbc, "1", 0, 3, 1, 1, baseButtonSize);
        addClassicButton(classicPad, gbc, "2", 1, 3, 1, 1, baseButtonSize);
        addClassicButton(classicPad, gbc, "3", 2, 3, 1, 1, baseButtonSize);
        addClassicButton(classicPad, gbc, "Enter", 3, 3, 1, 2, baseButtonSize);

        addClassicButton(classicPad, gbc, "0", 0, 4, 2, 1, baseButtonSize);
        addClassicButton(classicPad, gbc, ".", 2, 4, 1, 1, baseButtonSize);

        Dimension strictPadSize = new Dimension(264, 330);
        classicPad.setPreferredSize(strictPadSize);
        classicPad.setMinimumSize(strictPadSize);
        classicPad.setMaximumSize(strictPadSize);

        mainGbc.gridx = 2;
        mainGbc.weightx = 0.0;
        bodyPanel.add(classicPad, mainGbc);

        add(bodyPanel, BorderLayout.CENTER);
    }

    private void addClassicButton(JPanel panel, GridBagConstraints gbc, String text,
                                  int x, int y, int width, int height, Dimension baseSize) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;

        JButton button = createStyledButton(text, false);

        int targetWidth = baseSize.width;
        int targetHeight = baseSize.height;

        if (width == 2) targetWidth = (baseSize.width * 2) + 6;
        if (height == 2) targetHeight = (baseSize.height * 2) + 6;

        Dimension targetDimension = new Dimension(targetWidth, targetHeight);
        button.setPreferredSize(targetDimension);
        button.setMinimumSize(targetDimension);

        panel.add(button, gbc);
    }

    private JButton createStyledButton(String text, boolean isEngineering) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFocusable(false);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
            }
        });

        if (text.equals("Enter")) {
            button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        } else if (isEngineering) {
            button.setFont(new Font("Segoe UI", Font.PLAIN, 13)); // Слегка уменьшим кегль для длинных надписей (sinh/cosh)
        } else {
            button.setFont(new Font("Segoe UI", Font.BOLD, 22));
        }

        if (text.equals("C") || text.equals("M+") || text.equals("MR") || text.equals("MC")) {
            button.setBackground(new Color(228, 238, 247));
            button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        } else if (isEngineering) {
            button.setBackground(new Color(242, 245, 249));
            button.setForeground(new Color(50, 100, 150));
        } else {
            button.setBackground(new Color(252, 252, 252));
            button.setForeground(Color.BLACK);

            if (text.equals("Enter") || text.equals("+")) {
                button.setBackground(new Color(228, 238, 247));
            }
        }

        button.setActionCommand(text);
        buttonMap.put(text, button);
        return button;
    }

    public void updateDisplay(String text) {
        display.setText(text);
    }

    public void updateMemoryDisplay(String memoryValue) {
        if (memoryValue == null || memoryValue.isEmpty() || "0".equals(memoryValue) || "0.0".equals(memoryValue)) {
            memoryLabel.setText(" ");
        } else {
            memoryLabel.setText("M: " + memoryValue);
        }
    }

    public void updateFormulaDisplay(String formula) {
        if (formula == null || formula.isEmpty()) {
            formulaLabel.setText(" ");
        } else {
            formulaLabel.setText(formula);
        }
    }

    public void registerController(ActionListener controller) {
        this.globalController = controller;
        registerButtonsRecursively(engineeringGrid.getParent(), controller);

        for (Component comp : classicPad.getComponents()) {
            if (comp instanceof JButton button) {
                button.addActionListener(controller);
            }
        }

        // РЕФАКТОРИНГ: Регистрируем радиокнопки в Контроллере
        if (degRadio != null && radRadio != null) {
            degRadio.addActionListener(controller);
            radRadio.addActionListener(controller);
        }
    }

    private void registerButtonsRecursively(Component container, ActionListener controller) {
        if (container instanceof JButton button) {
            button.addActionListener(controller);
        } else if (container instanceof Container c) {
            for (Component child : c.getComponents()) {
                registerButtonsRecursively(child, controller);
            }
        }
    }

    // Вспомогательные геттеры для программного переключения радиокнопок через Hotkeys
    public JRadioButton getDegRadio() { return degRadio; }
    public JRadioButton getRadRadio() { return radRadio; }
}