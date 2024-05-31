package com.example.teleconnect2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LogsAdapter extends RecyclerView.Adapter<LogsAdapter.LogViewHolder> {

    private List<LogEntry> logEntries;

    public LogsAdapter(List<LogEntry> logEntries) {
        this.logEntries = logEntries;
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_item, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        LogEntry logEntry = logEntries.get(position);
        holder.textLogEmail.setText(logEntry.getEmail());
        holder.textLogMessage.setText(logEntry.getMessage());
        holder.textLogTimestamp.setText(logEntry.getTimestamp());
    }

    @Override
    public int getItemCount() {
        return logEntries.size();
    }

    public static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView textLogEmail;
        TextView textLogMessage;
        TextView textLogTimestamp;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            textLogEmail = itemView.findViewById(R.id.textLogEmail);
            textLogMessage = itemView.findViewById(R.id.textLogMessage);
            textLogTimestamp = itemView.findViewById(R.id.textLogTimestamp);
        }
    }
}
