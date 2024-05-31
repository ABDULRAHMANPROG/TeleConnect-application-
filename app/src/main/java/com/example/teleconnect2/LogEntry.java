package com.example.teleconnect2;

public class LogEntry {
    private String uid;
    private String email;
    private String message;
    private String timestamp;

    // Default constructor required for calls to DataSnapshot.getValue(LogEntry.class)
    public LogEntry() {
    }

    public LogEntry(String uid, String email, String message, String timestamp) {
        this.uid = uid;
        this.email = email;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
