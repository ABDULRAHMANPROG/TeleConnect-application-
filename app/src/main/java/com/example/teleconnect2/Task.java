package com.example.teleconnect2;

public class Task {
    private String taskId;
    private String title;
    private String description;
    private String assignedBy; // UID of the admin who assigned the task
    private String assignedTo; // UID of the employee the task is assigned to
    private boolean eSignatureRequired; // Whether the task requires an e-signature
    private String status; // Status of the task (e.g., pending, completed)

    // Default constructor required for calls to DataSnapshot.getValue(Task.class)
    public Task() {}

    // Constructor for tasks created by employees
    public Task(String taskId, String title, String description) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.status = "pending";
    }

    // Constructor for tasks assigned by admins
    public Task(String taskId, String title, String description, String assignedBy, String assignedTo, boolean eSignatureRequired, String status) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.assignedBy = assignedBy;
        this.assignedTo = assignedTo;
        this.eSignatureRequired = eSignatureRequired;
        this.status = status;
    }

    // Getter and Setter methods

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

    public String getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public boolean isESignatureRequired() {
        return eSignatureRequired;
    }

    public void setESignatureRequired(boolean eSignatureRequired) {
        this.eSignatureRequired = eSignatureRequired;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return title + " - Status: " + status;
    }
}
