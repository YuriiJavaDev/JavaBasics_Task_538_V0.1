package com.yurii.pavlenko.utils;

import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import java.awt.*;

/**
 * Global operational utilities handling interface initialization, text styling configurations, and theme parameters.
 */
public class ConfigureUtil {

    /**
     * Bootstraps clean global Look and Feel themes and forces text edge smoothing routines.
     */
    public static void configureLookAndFeel() {
        try {
            System.setProperty("awtextra.robot.graphics.smoothing", "true");
            System.setProperty("swing.aatext", "true");

            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Theme management execution failed. Falling back to core system themes.");
        }
    }

    /**
     * Globally injects font styling into the UIManager to enforce uniform list component typography.
     */
    public static void configureGlobalFonts() {
        Font font = new Font("Segoe UI", Font.PLAIN, 14);
        FontUIResource fontResource = new FontUIResource(font);

        UIManager.put("List.font", fontResource);
        UIManager.put("Label.font", fontResource);
        UIManager.put("Button.font", fontResource);
        UIManager.put("TextField.font", fontResource);
    }
}