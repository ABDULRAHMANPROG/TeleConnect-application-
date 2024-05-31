package com.example.teleconnect2;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Logger {

    private static final DatabaseReference logsReference = FirebaseDatabase.getInstance().getReference("logs");

    public static void log(String message) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return; // User not logged in, don't log
        }

        String uid = user.getUid();
        String email = user.getEmail();
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        Map<String, Object> logEntry = new HashMap<>();
        logEntry.put("uid", uid);
        logEntry.put("email", email);
        logEntry.put("message", message);
        logEntry.put("timestamp", timestamp);

        logsReference.push().setValue(logEntry);
    }
}
