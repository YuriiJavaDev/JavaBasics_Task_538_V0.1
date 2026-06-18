package main.com.yurii.pavlenko.ui.panels.tools;

import main.com.yurii.pavlenko.utils.CalculatorHotkeyConfigurator;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * High-performance arithmetic and engineering calculator layout.
 * Delegates physical keyboard mapping setup to specialized configurator entity.
 */
public class CalculatorPanel extends JPanel {

    private JLabel display;
    private JLabel memoryLabel;
    private JPanel engineeringGrid;
    private JPanel classicPad;

    // Панель дисплея для точечной подсветки рамки
    private JPanel displayPanel;

    private final Map<String, JButton> buttonMap = new HashMap<>();
    private ActionListener globalController;

    // Цвета для рамки табло
    private final Color focusBorderColor = new Color(145, 175, 205);
    private final Color defaultBorderColor = Color.GRAY;

    public CalculatorPanel() {
        setBorder(BorderFactory.createTitledBorder("Arithmetic & Engineering Calculator"));
        setLayout(new BorderLayout(12, 12));

        initializeDisplay();
        initializeCalculatorBody();

        // Настройка горячих клавиш
        CalculatorHotkeyConfigurator.configureHotkeys(this, buttonMap);

        // 1. Делаем панель калькулятора полноценным участником фокуса
        setFocusable(true);

        // 2. Чтобы при клике мышкой на пустые зоны фокус переходил на калькулятор
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
            }
        });

        // 3. Стандартный FocusListener. Теперь Swing сам будет гарантированно
        // тушить фокус в Погоде и Конвертере, когда фокус переходит сюда!
        this.addFocusListener(new java.awt.event.FocusListener() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                activateCalculatorFocus();
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                // Проверяем: если фокус ушел на кнопку внутри нашего же калькулятора,
                // рамку НЕ тушим!
                Component opposite = e.getOppositeComponent();
                if (opposite instanceof JButton && buttonMap.containsValue(opposite)) {
                    return;
                }
                deactivateCalculatorFocus();
            }
        });
    }

    /**
     * Включает активную синюю рамку вокруг табло.
     */
    private void activateCalculatorFocus() {
        displayPanel.setBorder(createUniformDisplayBorder(focusBorderColor));
        displayPanel.repaint();
    }

    /**
     * Гасит рамку калькулятора (делает серой).
     */
    private void deactivateCalculatorFocus() {
        displayPanel.setBorder(createUniformDisplayBorder(defaultBorderColor));
        displayPanel.repaint();
    }

    /**
     * Создает базовую подложку с абсолютно одинаковыми отступами по всему периметру (6px).
     * Рисует красивую, ЖИРНУЮ рамку в 4 пикселя.
     */
    private Border createUniformDisplayBorder(Color borderColor) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(6, 6, 6, 6),   // Идеально симметричные зазоры вокруг табло
                BorderFactory.createLineBorder(borderColor, 4)  // Увеличенная толщина рамки до 4px
        );
    }

    private void initializeDisplay() {
        displayPanel = new JPanel(new BorderLayout());
        displayPanel.setBackground(Color.WHITE); // Само пространство табло делаем белым
        displayPanel.setPreferredSize(new Dimension(0, 80));

        // Стартовая стандартная серая рамка (толщина 4px)
        displayPanel.setBorder(createUniformDisplayBorder(defaultBorderColor));

        // Клик по самой панели табло заставляет родительский калькулятор забрать фокус!
        displayPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                requestFocusInWindow(); // Запрашиваем фокус для всей CalculatorPanel
            }
        });

        // Убираем вертикальные инсеты, давая BorderLayout центрировать текст без обрезки снизу
        JPanel textContainer = new JPanel(new BorderLayout());
        textContainer.setOpaque(false);
        textContainer.setBorder(BorderFactory.createEmptyBorder(2, 12, 2, 12));

        // Клик по контейнеру текста тоже переводит фокус на калькулятор
        textContainer.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                requestFocusInWindow();
            }
        });

        memoryLabel = new JLabel(" ");
        memoryLabel.setFont(new Font("Consolas", Font.PLAIN, 14));
        memoryLabel.setForeground(Color.GRAY);
        textContainer.add(memoryLabel, BorderLayout.NORTH);

        display = new JLabel("0");
        display.setOpaque(false);
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setVerticalAlignment(SwingConstants.CENTER);
        display.setFont(new Font("Consolas", Font.BOLD, 36));

        // Клик непосредственно по тексту цифр на дисплее также переводит фокус
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

        JPanel leftEngineeringWing = new JPanel(new BorderLayout(0, 6));

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

        leftEngineeringWing.add(memoryControlPanel, BorderLayout.NORTH);

        engineeringGrid = new JPanel(new GridLayout(5, 5, 6, 6));
        String[] mathButtons = {
                "sin",  "cos",  "tan",  "Deg",  "Rad",
                "asin", "acos", "atan", "(",    ")",
                "x²",   "x^y",  "sqrt", "∛x",   "1/x",
                "ln",   "log",  "e",    "10^x", "exp",
                "n!",   "abs",  "mod",  "Rand", "Ans"
        };

        for (String txt : mathButtons) {
            engineeringGrid.add(createStyledButton(txt, true));
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
        button.setFocusable(false); // Кнопки не берут фокус на себя

        // Клик по кнопке переводит фокус на панель калькулятора.
        // Погода и Конвертер узнают об этом и тушат свои рамки!
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
            }
        });

        if (text.equals("Enter")) {
            button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        } else if (isEngineering) {
            button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
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

    public void registerController(ActionListener controller) {
        this.globalController = controller;
        registerButtonsRecursively(engineeringGrid.getParent(), controller);

        for (Component comp : classicPad.getComponents()) {
            if (comp instanceof JButton button) {
                button.addActionListener(controller);
            }
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
}