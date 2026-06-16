package main.com.yurii.pavlenko.ui.panels.tools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * High-performance arithmetic and engineering calculator layout.
 * Enforces a strictly monolithic classic numeric pad on the right wing while
 * allowing the engineering operations grid on the left wing to scale fluidly.
 */
public class CalculatorPanel extends JPanel {

    private JLabel display;
    private JLabel memoryLabel;
    private JPanel engineeringGrid;
    private JPanel classicPad;

    public CalculatorPanel() {
        setBorder(BorderFactory.createTitledBorder("Arithmetic & Engineering Calculator"));
        setLayout(new BorderLayout(12, 12));

        initializeDisplay();
        initializeCalculatorBody();
    }

    private void initializeDisplay() {
        JPanel displayPanel = new JPanel(new BorderLayout());
        displayPanel.setBackground(Color.WHITE);
        displayPanel.setPreferredSize(new Dimension(0, 75));
        displayPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)
        ));

        // Small top-left memory status indicator
        // MONOLITHIC: We init with an empty string to ensure the layout allocates space immediately
        memoryLabel = new JLabel(" ");
        memoryLabel.setFont(new Font("Consolas", Font.PLAIN, 14));
        memoryLabel.setForeground(Color.GRAY);
        displayPanel.add(memoryLabel, BorderLayout.NORTH);

        // Main numeric display field
        display = new JLabel("0");
        display.setOpaque(false);
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setVerticalAlignment(SwingConstants.BOTTOM);
        display.setFont(new Font("Consolas", Font.BOLD, 36));
        displayPanel.add(display, BorderLayout.CENTER);

        add(displayPanel, BorderLayout.NORTH);
    }

    private void initializeCalculatorBody() {
        JPanel bodyPanel = new JPanel(new GridBagLayout());
        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.fill = GridBagConstraints.BOTH;
        mainGbc.weighty = 1.0;

        JPanel leftEngineeringWing = new JPanel(new BorderLayout(0, 6));

        // ---------------------------------------------------------------------
        // STAGE A: Fixed Memory and Reset Control Row
        // ---------------------------------------------------------------------
        JPanel memoryControlPanel = new JPanel(new GridBagLayout());
        GridBagConstraints memGbc = new GridBagConstraints();
        memGbc.fill = GridBagConstraints.BOTH;
        memGbc.weighty = 1.0;
        memGbc.insets = new Insets(3, 3, 3, 3);

        JButton btnC = createStyledButton("C", true);
        JButton btnToggleM = createStyledButton("M+", true);
        JButton btnMR = createStyledButton("MR", true);
        JButton btnMC = createStyledButton("MC", true);

        // Explicitly set action commands to prevent spacing mismatch issues
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

        // ---------------------------------------------------------------------
        // STAGE B: Fluid Scaling Mathematical Grid Layout
        // ---------------------------------------------------------------------
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

        // =====================================================================
        // VISUAL SEPARATOR
        // =====================================================================
        JPanel separatorContainer = new JPanel(new BorderLayout());
        JSeparator verticalLine = new JSeparator(JSeparator.VERTICAL);
        verticalLine.setForeground(Color.LIGHT_GRAY);
        separatorContainer.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 6));
        separatorContainer.add(verticalLine, BorderLayout.CENTER);

        mainGbc.gridx = 1;
        mainGbc.weightx = 0.0;
        bodyPanel.add(separatorContainer, mainGbc);

        // =====================================================================
        // MONOLITHIC RIGHT WING: Mitsumi Numeric Pad Configuration
        // =====================================================================
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

        // Scale button dimension factoring in the 6px gap sizes
        if (width == 2) {
            targetWidth = (baseSize.width * 2) + 6;
        }
        if (height == 2) {
            targetHeight = (baseSize.height * 2) + 6;
        }

        Dimension targetDimension = new Dimension(targetWidth, targetHeight);
        button.setPreferredSize(targetDimension);
        button.setMinimumSize(targetDimension);

        panel.add(button, gbc);
    }

    private JButton createStyledButton(String text, boolean isEngineering) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);

        if (text.equals("Enter")) {
            button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        } else if (isEngineering) {
            button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        } else {
            button.setFont(new Font("Segoe UI", Font.BOLD, 22));
        }

        // Apply dedicated styles to the top memory control layer row
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

        return button;
    }

    public void updateDisplay(String text) {
        display.setText(text);
    }

    /**
     * Updates memory status based on logic.
     * FIX (BUG 404): Removed parent removal, keeping monolithic size structure intact.
     */
    public void updateMemoryDisplay(String memoryValue) {
        if (memoryValue == null || memoryValue.isEmpty() || "0".equals(memoryValue) || "0.0".equals(memoryValue)) {
            // BUGFIX: Instead of removal, we set a single space to force the layout to keep allocating vertical height.
            // parent.remove(memoryLabel); <- Removed
            memoryLabel.setText(" ");
        } else {
            // BUGFIX: Keeps the monolithic size structure consistent when adding value
            memoryLabel.setText("M: " + memoryValue);
        }
    }

    public void registerController(ActionListener controller) {
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