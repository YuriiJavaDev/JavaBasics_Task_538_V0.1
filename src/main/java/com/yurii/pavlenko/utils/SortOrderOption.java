package com.yurii.pavlenko.utils;

/**
 * Enumeration representing all supported task sorting criteria within the tracking system.
 */
public enum SortOrderOption {
    A_Z("Alphabetical (A-Z)"),
    Z_A("Alphabetical (Z-A)"),
    BY_STATUS("By Status"),
    BY_CREATED("By Creation Date"),
    BY_EDITED("By Modification Date"),
    BY_COMPLETED("By Completion Date"),
    BY_IMPORTANCE("By Importance");

    private final String description;

    SortOrderOption(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}