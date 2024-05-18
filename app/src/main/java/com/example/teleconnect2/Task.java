package com.example.teleconnect2;

public class Task {
    private String taskId;
    private String title;
    private String description;

    // Default constructor required for calls to DataSnapshot.getValue(Task.class)
    public Task() {}

    public Task(String taskId, String title, String description) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return title;  // Display the task title in the spinner
    }
}
