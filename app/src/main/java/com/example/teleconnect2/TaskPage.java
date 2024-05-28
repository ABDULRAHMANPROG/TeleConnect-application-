package com.example.teleconnect2;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.auth.FirebaseUser;
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
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_page);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            tasksReference = FirebaseDatabase.getInstance().getReference("Tasks").child(currentUser.getUid());
        } else {
            // Handle case where user is not authenticated
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        spinnerTasks = findViewById(R.id.spinnerTasks);
        btnUpdateTask = findViewById(R.id.btnUpdateTask);
        btnClearTask = findViewById(R.id.btnClearTask);
        editTextTaskTitle = findViewById(R.id.editTextTaskTitle);
        editTextTaskDescription = findViewById(R.id.editTextTaskDescription);
        btnCreateTask = findViewById(R.id.btnCreateTask);
        taskInfoTextView = findViewById(R.id.taskInfoTextView);

        loadTasks();

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
                Task selectedTask = (Task) spinnerTasks.getSelectedItem();
                if (selectedTask != null) {
                    String taskId = selectedTask.getTaskId();
                    tasksReference.child(taskId).removeValue()
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

    private void displayTasks(List<Task> tasks) {
        ArrayAdapter<Task> adapter = new ArrayAdapter<Task>(this, R.layout.custom_spinner_item, tasks) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    convertView = inflater.inflate(R.layout.custom_spinner_dropdown_item, parent, false);
                }

                TextView textView = (TextView) convertView;
                textView.setText(getItem(position).getTitle());

                return convertView;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    convertView = inflater.inflate(R.layout.custom_spinner_item, parent, false);
                }

                TextView textView = (TextView) convertView;
                textView.setText(getItem(position).getTitle());

                return convertView;
            }
        };

        spinnerTasks.setAdapter(adapter);
    }

    private void displayTaskInformation(Task selectedTask) {
        String taskInfo = "Task ID: " + selectedTask.getTaskId() + "\nTask Title: " + selectedTask.getTitle() + "\nTask Description: " + selectedTask.getDescription();
        taskInfoTextView.setText(taskInfo);
    }
}
