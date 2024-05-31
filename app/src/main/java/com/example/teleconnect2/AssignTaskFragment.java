package com.example.teleconnect2;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AssignTaskFragment extends Fragment {

    private static final String TAG = "AssignTaskFragment";

    private EditText editTextTaskTitle;
    private EditText editTextTaskDescription;
    private Spinner spinnerAssignTo;
    private CheckBox checkBoxESignatureRequired;
    private Button btnAssignTask;
    private DatabaseReference adminTasksReference;
    private DatabaseReference usersReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_assign_task, container, false);

        editTextTaskTitle = view.findViewById(R.id.editTextTaskTitle);
        editTextTaskDescription = view.findViewById(R.id.editTextTaskDescription);
        spinnerAssignTo = view.findViewById(R.id.spinnerAssignTo);
        checkBoxESignatureRequired = view.findViewById(R.id.checkBoxESignatureRequired);
        btnAssignTask = view.findViewById(R.id.btnAssignTask);

        adminTasksReference = FirebaseDatabase.getInstance().getReference("AdminTasks");
        usersReference = FirebaseDatabase.getInstance().getReference("users");

        loadEmployees();

        btnAssignTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assignTask();
            }
        });

        return view;
    }

    private void loadEmployees() {
        Log.d(TAG, "Loading employees...");
        usersReference.orderByChild("role").equalTo("user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> employeeList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userEmail = snapshot.child("email").getValue(String.class);
                    String userId = snapshot.getKey();
                    Log.d(TAG, "User ID: " + userId + ", Email: " + userEmail);
                    if (userEmail != null && userId != null) {
                        employeeList.add(userEmail + " (" + userId + ")");
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, employeeList);
                adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
                spinnerAssignTo.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to load employees", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to load employees: " + databaseError.getMessage());
            }
        });
    }

    private void assignTask() {
        String taskTitle = editTextTaskTitle.getText().toString().trim();
        String taskDescription = editTextTaskDescription.getText().toString().trim();
        String selectedEmployee = (String) spinnerAssignTo.getSelectedItem();
        Logger.log("Admin created a task");

        if (selectedEmployee == null) {
            Toast.makeText(getActivity(), "Please select an employee", Toast.LENGTH_SHORT).show();
            return;
        }

        String assignedTo = selectedEmployee.substring(selectedEmployee.indexOf("(") + 1, selectedEmployee.indexOf(")"));
        boolean eSignatureRequired = checkBoxESignatureRequired.isChecked();

        if (taskTitle.isEmpty() || taskDescription.isEmpty() || assignedTo.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String taskId = adminTasksReference.push().getKey();
        Task task = new Task(taskId, taskTitle, taskDescription, FirebaseAuth.getInstance().getCurrentUser().getUid(), assignedTo, eSignatureRequired, "pending");

        adminTasksReference.child(taskId).setValue(task).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Task assigned successfully", Toast.LENGTH_SHORT).show();
                    clearFields();
                } else {
                    Toast.makeText(getActivity(), "Failed to assign task", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void clearFields() {
        editTextTaskTitle.setText("");
        editTextTaskDescription.setText("");
        checkBoxESignatureRequired.setChecked(false);
        spinnerAssignTo.setSelection(0);
    }
}
