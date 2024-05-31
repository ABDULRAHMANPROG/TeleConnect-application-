package com.example.teleconnect2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LogsFragment extends Fragment {

    private RecyclerView recyclerViewLogs;
    private LogsAdapter logsAdapter;
    private List<LogEntry> logEntries;
    private DatabaseReference logsReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logs, container, false);

        recyclerViewLogs = view.findViewById(R.id.recyclerViewLogs);
        recyclerViewLogs.setLayoutManager(new LinearLayoutManager(getContext()));

        logEntries = new ArrayList<>();
        logsAdapter = new LogsAdapter(logEntries);
        recyclerViewLogs.setAdapter(logsAdapter);

        logsReference = FirebaseDatabase.getInstance().getReference("logs");
        fetchLogs();

        return view;
    }

    private void fetchLogs() {
        logsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                logEntries.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    LogEntry logEntry = snapshot.getValue(LogEntry.class);
                    if (logEntry != null) {
                        logEntries.add(logEntry);
                    }
                }
                logsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }
}
