package com.yurii.pavlenko.util;

import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import java.awt.*;

/**
 * Global operational utilities handling interface initialization, text styling configurations, and theme parameters.
 */
public class Util {

    /**
     * Bootstraps clean global Look and Feel themes and forces text edge smoothing routines.
     */
    public static void configureLookAndFeel() {
        try {
            // Force strict system rendering text antialiasing hints for sub-components
            System.setProperty("awtextra.robot.graphics.smoothing", "true");
            System.setProperty("swing.aatext", "true");

            // Iterate and assign clean modern Nimbus layout engine to bypass legacy Windows artifact anomalies
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Safe fallback trace context if the environment parameters reject modern themes
            System.err.println("Theme management execution failed. Falling back to core system themes.");
        }
    }

    /**
     * Globally injects font styling into the UIManager to enforce uniform list component typography.
     */
    public static void configureGlobalFonts() {
        // Создаем шрифт нужного размера (например, 16)
        Font font = new Font("Segoe UI", Font.PLAIN, 14);
        FontUIResource fontResource = new FontUIResource(font);

        // Применяем этот шрифт ко всем компонентам списков в приложении
        UIManager.put("List.font", fontResource);

        // Можно также применить к другим элементам, если нужно
        UIManager.put("Label.font", fontResource);
        UIManager.put("Button.font", fontResource);
        UIManager.put("TextField.font", fontResource);
    }
}