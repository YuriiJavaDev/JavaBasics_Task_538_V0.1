package main.com.yurii.pavlenko.utils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility configurator initializing seamless physical keyboard hotkeys mapping
 * and dynamic contextual tooltips for engineering functions.
 */
public final class CalculatorHotkeyConfigurator {

    private CalculatorHotkeyConfigurator() {
        // Утилитарный класс не должен инстанцироваться напрямую
    }

    /**
     * Configures hotkeys for the provided calculator panel and sets up smart tooltips.
     */
    public static void configureHotkeys(JPanel panel, Map<String, JButton> buttonMap) {
        InputMap inputMap = panel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap actionMap = panel.getActionMap();

        // 1. Цифры основного ряда и правого блока Numpad
        for (int i = 0; i <= 9; i++) {
            String strNum = String.valueOf(i);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_0 + i, 0), "press_" + strNum);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD0 + i, 0), "press_" + strNum);
            actionMap.put("press_" + strNum, createTriggerAction(buttonMap, strNum));
        }

        // 2. Арифметические операции (Основной блок + Numpad)
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, 0), "press_plus");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ADD, 0), "press_plus");
        actionMap.put("press_plus", createTriggerAction(buttonMap, "+"));

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0), "press_minus");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, 0), "press_minus");
        actionMap.put("press_minus", createTriggerAction(buttonMap, "-"));

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_MULTIPLY, 0), "press_multiply");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_8, KeyEvent.SHIFT_DOWN_MASK), "press_multiply");
        actionMap.put("press_multiply", createTriggerAction(buttonMap, "*"));

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, 0), "press_divide");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DIVIDE, 0), "press_divide");
        actionMap.put("press_divide", createTriggerAction(buttonMap, "/"));

        // 3. Специальные знаки и управляющие клавиши
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_PERIOD, 0), "press_dot");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, 0), "press_dot");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DECIMAL, 0), "press_dot");
        actionMap.put("press_dot", createTriggerAction(buttonMap, "."));

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "press_enter");
        actionMap.put("press_enter", createTriggerAction(buttonMap, "Enter"));

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "press_clear");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "press_clear");
        actionMap.put("press_clear", createTriggerAction(buttonMap, "C"));

        // 4. Блок работы с памятью (Ctrl + M / R / L)
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_M, KeyEvent.CTRL_DOWN_MASK), "press_MPlus");
        actionMap.put("press_MPlus", createTriggerAction(buttonMap, "M+"));

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK), "press_MR");
        actionMap.put("press_MR", createTriggerAction(buttonMap, "MR"));

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_DOWN_MASK), "press_MC");
        actionMap.put("press_MC", createTriggerAction(buttonMap, "MC"));

        // 5. Инженерные функции: Скобки и базовые степени
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_9, KeyEvent.SHIFT_DOWN_MASK), "press_open_bracket");
        actionMap.put("press_open_bracket", createTriggerAction(buttonMap, "("));

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_0, KeyEvent.SHIFT_DOWN_MASK), "press_close_bracket");
        actionMap.put("press_close_bracket", createTriggerAction(buttonMap, ")"));

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK), "press_square");
        actionMap.put("press_square", createTriggerAction(buttonMap, "x²"));

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_6, KeyEvent.SHIFT_DOWN_MASK), "press_power");
        actionMap.put("press_power", createTriggerAction(buttonMap, "x^y"));

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_5, KeyEvent.SHIFT_DOWN_MASK), "press_mod");
        actionMap.put("press_mod", createTriggerAction(buttonMap, "mod"));

        // 6. Тригонометрия, логарифмы и константные значения
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK), "press_sin");
        actionMap.put("press_sin", createTriggerAction(buttonMap, "sin"));

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK), "press_cos");
        actionMap.put("press_cos", createTriggerAction(buttonMap, "cos"));

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.CTRL_DOWN_MASK), "press_tan");
        actionMap.put("press_tan", createTriggerAction(buttonMap, "tan"));

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_DOWN_MASK), "press_sqrt");
        actionMap.put("press_sqrt", createTriggerAction(buttonMap, "sqrt"));

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK), "press_ln");
        actionMap.put("press_ln", createTriggerAction(buttonMap, "ln"));

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_G, KeyEvent.CTRL_DOWN_MASK), "press_log");
        actionMap.put("press_log", createTriggerAction(buttonMap, "log"));

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK), "press_pi");
        actionMap.put("press_pi", createTriggerAction(buttonMap, "π"));

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK), "press_e_const");
        actionMap.put("press_e_const", createTriggerAction(buttonMap, "e"));

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_1, KeyEvent.SHIFT_DOWN_MASK), "press_factorial");
        actionMap.put("press_factorial", createTriggerAction(buttonMap, "n!"));

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK), "press_ans");
        actionMap.put("press_ans", createTriggerAction(buttonMap, "Ans"));

        // Переключение угловых мер одной кнопкой Ctrl + D
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_DOWN_MASK), "toggle_angle");
        actionMap.put("toggle_angle", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton degBtn = buttonMap.get("Deg");
                if (degBtn != null && degBtn.isEnabled()) {
                    degBtn.doClick(120);
                }
            }
        });

        // Автоматически навешиваем контекстные подсказки для сложных кнопок
        configureButtonToolTips(buttonMap);
    }

    /**
     * Automatically maps and assigns hotkey hint tooltips to appropriate buttons.
     */
    private static void configureButtonToolTips(Map<String, JButton> buttonMap) {
        Map<String, String> hints = new HashMap<>();

        // Кнопки управления и памяти
        hints.put("C", "Hotkey: ESC or Backspace");
        hints.put("M+", "Hotkey: Ctrl + M");
        hints.put("MR", "Hotkey: Ctrl + R");
        hints.put("MC", "Hotkey: Ctrl + L");

        // Инженерный блок
        hints.put("sin", "Hotkey: Ctrl + S");
        hints.put("cos", "Hotkey: Ctrl + O");
        hints.put("tan", "Hotkey: Ctrl + T");
        hints.put("Deg", "Toggle mode Hotkey: Ctrl + D");
        hints.put("Rad", "Toggle mode Hotkey: Ctrl + D");
        hints.put("x²", "Hotkey: Ctrl + Q");
        hints.put("x^y", "Hotkey: Shift + 6 (^)");
        hints.put("sqrt", "Hotkey: Ctrl + H");
        hints.put("ln", "Hotkey: Ctrl + N");
        hints.put("log", "Hotkey: Ctrl + G");
        hints.put("π", "Hotkey: Ctrl + P");
        hints.put("e", "Hotkey: Ctrl + E");
        hints.put("n!", "Hotkey: Shift + 1 (!)");
        hints.put("mod", "Hotkey: Shift + 5 (%)");
        hints.put("Ans", "Hotkey: Ctrl + A");
        hints.put("(", "Hotkey: Shift + 9");
        hints.put(")", "Hotkey: Shift + 0");

        // Применяем тултипы только к тем кнопкам, которые есть на панели
        hints.forEach((btnKey, tipText) -> {
            JButton button = buttonMap.get(btnKey);
            if (button != null) {
                button.setToolTipText(tipText);
            }
        });
    }

    private static Action createTriggerAction(Map<String, JButton> buttonMap, String buttonKey) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton targetButton = buttonMap.get(buttonKey);
                if (targetButton != null && targetButton.isEnabled()) {
                    targetButton.doClick(120);
                }
            }
        };
    }
}