package com.yurii.pavlenko.ui.renderers;

import com.yurii.pavlenko.utils.FilterStatus;
import com.yurii.pavlenko.utils.SortOrderOption;

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

            boolean isStatusDisabled = (currentFilter != FilterStatus.ALL && option == SortOrderOption.BY_STATUS);
            boolean isCompletionDisabled = (currentFilter == FilterStatus.ACTIVE && option == SortOrderOption.BY_COMPLETED);
            boolean isImportanceDisabled = (currentFilter != FilterStatus.ALL && option == SortOrderOption.BY_IMPORTANCE);

            if (isStatusDisabled || isCompletionDisabled || isImportanceDisabled) {
                comp.setEnabled(false);
                comp.setForeground(Color.LIGHT_GRAY);
                if (isSelected && index >= 0) comp.setBackground(list.getBackground());
            } else {
                comp.setEnabled(true);
                comp.setForeground(Color.BLACK);
            }
        }
        return comp;
    }
}