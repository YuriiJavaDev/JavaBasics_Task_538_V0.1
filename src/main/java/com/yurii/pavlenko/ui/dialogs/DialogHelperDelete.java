package com.yurii.pavlenko.ui.dialogs;

import javax.swing.*;
import java.awt.*;

/**
 * Utility class handling reusable confirmation dialogs across UI components.
 */
public final class DialogHelperDelete {

    private DialogHelperDelete() {}

    /**
     * Shows a standard confirmation warning dialog.
     * * @param parent the parent component for centering the dialog
     * @param message the message to display to the user
     * @return true if the user clicked YES, false otherwise
     */
    public static boolean showDeleteConfirmation(Component parent, String message) {
        int response = JOptionPane.showConfirmDialog(
                parent,
                message,
                "Think three times before hitting the DELETE button!",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        return response == JOptionPane.YES_OPTION;
    }
}