package com.yurii.pavlenko.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing a single task entity with tracking timestamps and a unique identifier.
 */
public class Task {
    private UUID id; // New immutable unique identifier
    private String title;
    private boolean completed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;

    public Task() {}

    @JsonCreator
    public Task(@JsonProperty("id") UUID id,
                @JsonProperty("title") String title,
                @JsonProperty("completed") boolean completed,
                @JsonProperty("createdAt") LocalDateTime createdAt,
                @JsonProperty("updatedAt") LocalDateTime updatedAt,
                @JsonProperty("completedAt") LocalDateTime completedAt) {
        // Fallback for legacy JSON records that don't have an ID yet
        this.id = id != null ? id : UUID.randomUUID();
        this.title = title;
        this.completed = completed;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.completedAt = completedAt;
    }

    public Task(String title) {
        this.id = UUID.randomUUID(); // Automatically generate a unique 36-character ID
        this.title = title;
        this.completed = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = null;
        this.completedAt = null;
    }

    public UUID getId() { return id; }

    public void setId(UUID id) { this.id = id; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public boolean isCompleted() { return completed; }

    public void setCompleted(boolean completed) { this.completed = completed; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }

    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}