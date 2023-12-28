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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TaskPage extends AppCompatActivity {

    private Spinner spinnerTasks;
    private Button btnUpdateTask;
    private Button btnClearTask;
    private TextView taskInfoTextView;
    private DatabaseReference tasksReference;
    private EditText editTextTaskTitle;
    private EditText editTextTaskDescription;
    private Button btnCreateTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_page);

        tasksReference = FirebaseDatabase.getInstance().getReference("Tasks");

        spinnerTasks = findViewById(R.id.spinnerTasks);
        btnUpdateTask = findViewById(R.id.btnUpdateTask);
        btnClearTask = findViewById(R.id.btnClearTask);
        editTextTaskTitle = findViewById(R.id.editTextTaskTitle);
        editTextTaskDescription = findViewById(R.id.editTextTaskDescription);
        btnCreateTask = findViewById(R.id.btnCreateTask);

        loadTasks();
        taskInfoTextView = findViewById(R.id.taskInfoTextView);

        spinnerTasks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Task selectedTask = (Task) spinnerTasks.getSelectedItem();
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


        // btn update Task On Click Listener to save the updated description
        btnUpdateTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task selectedTask = (Task) spinnerTasks.getSelectedItem();
                if (selectedTask != null) {
                    String taskId = selectedTask.getTaskId();
                    String updatedDescription = editTextTaskDescription.getText().toString().trim();

                    if (!updatedDescription.isEmpty()) {
                        tasksReference.child(taskId).child("description").setValue(updatedDescription)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(TaskPage.this, "Task updated successfully", Toast.LENGTH_SHORT).show();
                                        // Update the selected task's description locally
                                        selectedTask.setDescription(updatedDescription);

                                        displayTaskInformation(selectedTask);

                                        loadTasks(); // Reload tasks if necessary
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
                Task selectedTask = (Task) spinnerTasks.getSelectedItem();
                if (selectedTask != null) {
                    String taskId = selectedTask.getTaskId();
                    tasksReference.child(taskId).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(TaskPage.this, "Task deleted successfully", Toast.LENGTH_SHORT).show();
                                    loadTasks(); // Refresh tasks after deletion
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
                // Launch the TaskPageActivity when the button is clicked
                startActivity(new Intent(TaskPage.this, homepage.class));
            }
        });
    }


    private void loadTasks() {
        tasksReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Task> taskList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Task task = snapshot.getValue(Task.class);
                    taskList.add(task);
                }
                displayTasks(taskList);
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

        if (taskTitle.isEmpty() || taskDescription.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String taskId = tasksReference.push().getKey();
        Task task = new Task(taskId, taskTitle, taskDescription);


        tasksReference.child(taskId).setValue(task)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(TaskPage.this, "Task created successfully", Toast.LENGTH_SHORT).show();
                        editTextTaskTitle.setText("");
                        editTextTaskDescription.setText("");
                        loadTasks(); // Reload the tasks after creating a new one
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TaskPage.this, "Failed to create task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayTasks(List<Task> tasks) {
        ArrayAdapter<Task> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tasks);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTasks.setAdapter(adapter);
    }

    // Update the displayTaskInformation method to show the description in TextView
    private void displayTaskInformation(Task selectedTask) {
        String taskInfo = "Task ID: " + selectedTask.getTaskId() + "\nTask Title: " + selectedTask.getTitle()+ "\n Task descrption "+selectedTask.getDescription();
        taskInfoTextView.setText(taskInfo);

    }
}