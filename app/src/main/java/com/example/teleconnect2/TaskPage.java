package com.example.teleconnect2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TaskPage extends AppCompatActivity {

    private Spinner spinnerAdminTasks, spinnerEmployeeTasks;
    private Button btnUpdateTask, btnClearTask, btnCreateTask;
    private TextView taskInfoTextView;
    private EditText editTextTaskTitle, editTextTaskDescription;
    private DatabaseReference userTasksReference, adminTasksReference;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_page);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userTasksReference = FirebaseDatabase.getInstance().getReference("UserTasks").child(currentUserId);
        adminTasksReference = FirebaseDatabase.getInstance().getReference("AdminTasks");

        spinnerAdminTasks = findViewById(R.id.spinnerAdminTasks);
        spinnerEmployeeTasks = findViewById(R.id.spinnerEmployeeTasks);
        btnUpdateTask = findViewById(R.id.btnUpdateTask);
        btnClearTask = findViewById(R.id.btnClearTask);
        btnCreateTask = findViewById(R.id.btnCreateTask);
        editTextTaskTitle = findViewById(R.id.editTextTaskTitle);
        editTextTaskDescription = findViewById(R.id.editTextTaskDescription);
        taskInfoTextView = findViewById(R.id.taskInfoTextView);

        loadTasks();

        spinnerAdminTasks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Task selectedTask = (Task) spinnerAdminTasks.getSelectedItem();
                if (selectedTask != null) {
                    displayTaskInformation(selectedTask);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // No task selected
            }
        });

        spinnerEmployeeTasks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Task selectedTask = (Task) spinnerEmployeeTasks.getSelectedItem();
                if (selectedTask != null) {
                    displayTaskInformation(selectedTask);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // No task selected
            }
        });

        btnCreateTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTask();
            }
        });

        btnUpdateTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task selectedTask = (Task) spinnerEmployeeTasks.getSelectedItem();
                if (selectedTask != null) {
                    String taskId = selectedTask.getTaskId();
                    String updatedDescription = editTextTaskDescription.getText().toString().trim();

                    if (!updatedDescription.isEmpty()) {
                        userTasksReference.child(taskId).child("description").setValue(updatedDescription)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(TaskPage.this, "Task updated successfully", Toast.LENGTH_SHORT).show();
                                        selectedTask.setDescription(updatedDescription);
                                        displayTaskInformation(selectedTask);
                                        loadTasks();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(TaskPage.this, "Failed to update task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(TaskPage.this, "Please enter a description", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnClearTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task selectedTask = (Task) spinnerEmployeeTasks.getSelectedItem();
                if (selectedTask != null) {
                    String taskId = selectedTask.getTaskId();
                    userTasksReference.child(taskId).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(TaskPage.this, "Task deleted successfully", Toast.LENGTH_SHORT).show();
                                    loadTasks();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(TaskPage.this, "Failed to delete task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        Button btnNavigateToTaskPage = findViewById(R.id.btnBackToHome);
        btnNavigateToTaskPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TaskPage.this, homepage.class));
            }
        });
    }

    private void loadTasks() {
        // Load tasks assigned by admin
        adminTasksReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Task> adminTasks = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Task task = snapshot.getValue(Task.class);
                    if (task != null && task.getAssignedTo() != null && task.getAssignedTo().equals(currentUserId)) {
                        adminTasks.add(task);
                    }
                }
                displayAdminTasks(adminTasks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TaskPage.this, "Failed to fetch tasks: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Load tasks created by employee
        userTasksReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Task> employeeTasks = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Task task = snapshot.getValue(Task.class);
                    if (task != null) {
                        employeeTasks.add(task);
                    }
                }
                displayEmployeeTasks(employeeTasks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TaskPage.this, "Failed to fetch tasks: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createTask() {
        String taskTitle = editTextTaskTitle.getText().toString().trim();
        String taskDescription = editTextTaskDescription.getText().toString().trim();
        Logger.log("User created a task");

        if (taskTitle.isEmpty() || taskDescription.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String taskId = userTasksReference.push().getKey();
        Task task = new Task(taskId, taskTitle, taskDescription, currentUserId, null, false, "pending");

        userTasksReference.child(taskId).setValue(task)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(TaskPage.this, "Task created successfully", Toast.LENGTH_SHORT).show();
                        editTextTaskTitle.setText("");
                        editTextTaskDescription.setText("");
                        loadTasks();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TaskPage.this, "Failed to create task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayAdminTasks(List<Task> tasks) {
        ArrayAdapter<Task> adapter = new ArrayAdapter<>(this, R.layout.custom_spinner_item, tasks);
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        spinnerAdminTasks.setAdapter(adapter);
    }

    private void displayEmployeeTasks(List<Task> tasks) {
        ArrayAdapter<Task> adapter = new ArrayAdapter<>(this, R.layout.custom_spinner_item, tasks);
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        spinnerEmployeeTasks.setAdapter(adapter);
    }

    private void displayTaskInformation(Task selectedTask) {
        String taskInfo = "Task ID: " + selectedTask.getTaskId() + "\nTask Title: " + selectedTask.getTitle() + "\nTask Description: " + selectedTask.getDescription();
        taskInfoTextView.setText(taskInfo);
    }
}
