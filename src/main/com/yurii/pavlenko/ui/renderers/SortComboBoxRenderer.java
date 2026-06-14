package main.com.yurii.pavlenko.ui.renderers;

import main.com.yurii.pavlenko.util.FilterStatus;
import main.com.yurii.pavlenko.util.SortOrderOption;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

/**
 * Custom renderer for the sorting dropdown that disables status sorting when filtering is active.
 */
public class SortComboBoxRenderer extends DefaultListCellRenderer {

    private final Supplier<FilterStatus> filterStatusSupplier;

    public SortComboBoxRenderer(Supplier<FilterStatus> filterStatusSupplier) {
        this.filterStatusSupplier = filterStatusSupplier;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof SortOrderOption option) {
            FilterStatus currentFilter = filterStatusSupplier.get();

            // If any filter is active, "By Status" sorting becomes irrelevant
            if (currentFilter != FilterStatus.ALL && option == SortOrderOption.BY_STATUS) {
                comp.setEnabled(false);
                comp.setForeground(Color.LIGHT_GRAY);

                // Prevent background highlighting for the disabled item when hovering
                if (isSelected && index >= 0) {
                    comp.setBackground(list.getBackground());
                }
            } else {
                comp.setEnabled(true);
                comp.setForeground(Color.BLACK);
            }
        }
        return comp;
    }
}