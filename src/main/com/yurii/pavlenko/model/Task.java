package main.com.yurii.pavlenko.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Task {
    private String title;
    private boolean completed;

    public Task() {}

    @JsonCreator
    public Task(@JsonProperty("title") String title,
                @JsonProperty("completed") boolean completed) {
        this.title = title;
        this.completed = completed;
    }

    public Task(String title) {
        this.title = title;
        this.completed = false;
    }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public boolean isCompleted() { return completed; }

    public void setCompleted(boolean completed) { this.completed = completed; }
}
