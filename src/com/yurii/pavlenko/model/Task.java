package com.yurii.pavlenko.model;

/**
 * Domain model entity representing a single manageable assignment holding text descriptors and completion state flags.
 */
public class Task {
    private String title;
    private boolean completed; // Added tracking flag to capture completion states

    public Task(String title) {
        this.title = title;
        this.completed = false;
    }

    public String getTitle() {
        return title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}