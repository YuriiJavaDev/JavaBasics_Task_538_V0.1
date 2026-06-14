package main.com.yurii.pavlenko.ui.renderers;

import main.com.yurii.pavlenko.util.FilterStatus;
import main.com.yurii.pavlenko.util.SortOrderOption;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

/**
 * Custom renderer for the sorting dropdown that disables status and completion sorting based on active filters.
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

            // Condition 1: "By Status" is irrelevant if ANY filter is applied
            boolean isStatusDisabled = (currentFilter != FilterStatus.ALL && option == SortOrderOption.BY_STATUS);

            // Condition 2: "By Completion Date" is irrelevant if we filter only "ACTIVE" tasks
            boolean isCompletionDisabled = (currentFilter == FilterStatus.ACTIVE && option == SortOrderOption.BY_COMPLETED);

            if (isStatusDisabled || isCompletionDisabled) {
                comp.setEnabled(false);
                comp.setForeground(Color.LIGHT_GRAY);

                // Prevent background highlights on hover for disabled entries
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