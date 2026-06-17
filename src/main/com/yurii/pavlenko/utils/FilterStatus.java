package main.com.yurii.pavlenko.utils;

/**
 * Enumeration representing available task filtering states for the UI layer.
 */
public enum FilterStatus {
    ALL("All Tasks"),
    ACTIVE("Active"),
    COMPLETED("Completed");

    private final String description;

    FilterStatus(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}