package com.example.teleconnect2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewAssignedTasksFragment extends Fragment {

    private ListView listViewTasks;
    private DatabaseReference adminTasksReference;
    private String currentUserId;
    private List<Task> taskList;
    private ArrayAdapter<Task> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_assigned_tasks, container, false);

        listViewTasks = view.findViewById(R.id.listViewTasks);
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        adminTasksReference = FirebaseDatabase.getInstance().getReference("AdminTasks");

        taskList = new ArrayList<>();
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, taskList);
        listViewTasks.setAdapter(adapter);

        loadAssignedTasks();

        return view;
    }

    private void loadAssignedTasks() {
        adminTasksReference.orderByChild("assignedBy").equalTo(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                taskList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Task task = snapshot.getValue(Task.class);
                    if (task != null) {
                        taskList.add(task);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to load tasks: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
