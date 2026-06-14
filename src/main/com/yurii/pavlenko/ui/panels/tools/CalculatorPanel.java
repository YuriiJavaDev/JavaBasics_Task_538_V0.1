package main.com.yurii.pavlenko.ui.panels.tools;

import javax.swing.*;
import java.awt.*;

/**
 * Advanced arithmetic and engineering calculator with strict Mitsumi square execution
 * on the right wing and fluid "rubber" stretching on the engineering left wing.
 */
public class CalculatorPanel extends JPanel {

    private JLabel display;
    private JPanel engineeringGrid;
    private JPanel classicPad;
    private JPanel separatorContainer;

    public CalculatorPanel() {
        setBorder(BorderFactory.createTitledBorder("Arithmetic & Engineering Calculator"));
        setLayout(new BorderLayout(12, 12));

        initializeDisplay();
        initializeCalculatorBody();
    }

    private void initializeDisplay() {
        JLabel displayLabel = new JLabel("0");
        displayLabel.setOpaque(true);
        displayLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        displayLabel.setVerticalAlignment(SwingConstants.BOTTOM);

        displayLabel.setFont(new Font("Consolas", Font.BOLD, 36));
        displayLabel.setBackground(Color.WHITE);
        displayLabel.setPreferredSize(new Dimension(0, 60));

        displayLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(0, 12, 2, 12)
        ));

        this.display = displayLabel;
        add(displayLabel, BorderLayout.NORTH);
    }

    private void initializeCalculatorBody() {
        // Main panel layouts wings dynamically based on vertical constraints
        JPanel bodyPanel = new JPanel(new GridBagLayout()) {
            @Override
            public void doLayout() {
                int currentHeight = getHeight();
                if (currentHeight > 0) {
                    // 5 rows of buttons with 6px gaps between them
                    int calculatedButtonHeight = (currentHeight / 5) - 6;

                    if (calculatedButtonHeight > 0) {
                        // 1. Right wing keys must be STRICT SQUARES (Width = Height)
                        for (Component btn : classicPad.getComponents()) {
                            if (btn instanceof JButton) {
                                String text = ((JButton) btn).getText();
                                int w = calculatedButtonHeight;
                                int h = calculatedButtonHeight;

                                if ("0".equals(text)) {
                                    w = (calculatedButtonHeight * 2) + 6;
                                } else if ("+".equals(text) || "Enter".equals(text)) {
                                    h = (calculatedButtonHeight * 2) + 6;
                                    w = calculatedButtonHeight;
                                }

                                Dimension d = new Dimension(w, h);
                                btn.setPreferredSize(d);
                                btn.setMinimumSize(d);
                                btn.setMaximumSize(d);
                            }
                        }

                        // Set strict fixed width for the entire right pad container
                        int exactClassicWidth = (calculatedButtonHeight * 4) + (6 * 3) + 12;
                        Dimension padDimension = new Dimension(exactClassicWidth, currentHeight);
                        classicPad.setPreferredSize(padDimension);
                        classicPad.setMinimumSize(padDimension);
                        classicPad.setMaximumSize(padDimension);
                    }
                }
                super.doLayout();
            }
        };

        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.fill = GridBagConstraints.BOTH;
        mainGbc.weighty = 1.0;

        // =====================================================================
        // 1. RUBBER LEFT WING: Engineering Operations (Fluid 5x5 Grid)
        // =====================================================================
        engineeringGrid = new JPanel(new GridLayout(5, 5, 6, 6));
        String[] engButtons = {
                "sin",  "cos",  "tan",  "mod",  "n!",
                "sinh", "cosh", "tanh", "exp",  "abs",
                "ln",   "log",  "sqrt", "e",    "1/x",
                "x²",   "x^y",  "π",    "Deg",  "Rad",
                "(",    ")",    "C",    "Rand", "Ans"
        };
        for (String txt : engButtons) {
            engineeringGrid.add(createStyledButton(txt, true));
        }

        mainGbc.gridx = 0;
        mainGbc.weightx = 1.0; // Dynamic rubber scale
        bodyPanel.add(engineeringGrid, mainGbc);

        // =====================================================================
        // 2. VISUAL SEPARATOR WITH STANDARD GAP PADDING
        // =====================================================================
        separatorContainer = new JPanel(new BorderLayout());
        JSeparator verticalLine = new JSeparator(JSeparator.VERTICAL);
        verticalLine.setForeground(Color.LIGHT_GRAY);
        separatorContainer.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 6));
        separatorContainer.add(verticalLine, BorderLayout.CENTER);

        mainGbc.gridx = 1;
        mainGbc.weightx = 0.0;
        bodyPanel.add(separatorContainer, mainGbc);

        // =====================================================================
        // 3. FIXED SQUARE RIGHT WING: Mitsumi Classic Numeric Pad
        // =====================================================================
        classicPad = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(3, 3, 3, 3);

        // Row 0
        addClassicButton(classicPad, gbc, "%", 0, 0, 1, 1);
        addClassicButton(classicPad, gbc, "/", 1, 0, 1, 1);
        addClassicButton(classicPad, gbc, "*", 2, 0, 1, 1);
        addClassicButton(classicPad, gbc, "-", 3, 0, 1, 1);

        // Row 1
        addClassicButton(classicPad, gbc, "7", 0, 1, 1, 1);
        addClassicButton(classicPad, gbc, "8", 1, 1, 1, 1);
        addClassicButton(classicPad, gbc, "9", 2, 1, 1, 1);
        addClassicButton(classicPad, gbc, "+", 3, 1, 1, 2);

        // Row 2
        addClassicButton(classicPad, gbc, "4", 0, 2, 1, 1);
        addClassicButton(classicPad, gbc, "5", 1, 2, 1, 1);
        addClassicButton(classicPad, gbc, "6", 2, 2, 1, 1);

        // Row 3
        addClassicButton(classicPad, gbc, "1", 0, 3, 1, 1);
        addClassicButton(classicPad, gbc, "2", 1, 3, 1, 1);
        addClassicButton(classicPad, gbc, "3", 2, 3, 1, 1);
        addClassicButton(classicPad, gbc, "Enter", 3, 3, 1, 2);

        // Row 4
        addClassicButton(classicPad, gbc, "0", 0, 4, 2, 1);
        addClassicButton(classicPad, gbc, ".", 2, 4, 1, 1);

        mainGbc.gridx = 2;
        mainGbc.weightx = 0.0; // Strict layout anchoring
        bodyPanel.add(classicPad, mainGbc);

        add(bodyPanel, BorderLayout.CENTER);
    }

    private void addClassicButton(JPanel panel, GridBagConstraints gbc, String text,
                                  int x, int y, int width, int height) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        panel.add(createStyledButton(text, false), gbc);
    }

    private JButton createStyledButton(String text, boolean isEngineering) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", isEngineering ? Font.PLAIN : Font.BOLD, 16));
        button.setFocusPainted(false);

        if (isEngineering) {
            button.setBackground(new Color(242, 245, 249));
            button.setForeground(new Color(50, 100, 150));
        } else {
            button.setBackground(new Color(252, 252, 252));
            if (text.equals("Enter") || text.equals("+")) {
                button.setBackground(new Color(228, 238, 247));
            }
        }

        button.addActionListener(e -> System.out.println("Pressed: " + text));
        return button;
    }
}