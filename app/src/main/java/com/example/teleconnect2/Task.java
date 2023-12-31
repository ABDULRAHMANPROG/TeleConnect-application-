package com.example.teleconnect2;


// Move the Task class outside of the TaskPage class
public class Task {
    private String taskId;
    private String title;
    private String description;

    public Task() {
        // Default constructor required for Firebase to map data
    }

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
}
