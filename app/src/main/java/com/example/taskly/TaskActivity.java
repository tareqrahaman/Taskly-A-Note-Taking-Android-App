package com.example.taskly;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TaskActivity extends AppCompatActivity {

    private EditText edTaskTitle, edTaskDesc;
    private FloatingActionButton fabSaveTask;
    private TaskDatabaseHelper taskDB;

    // New variables to hold existing task data if we are in EDIT mode
    private int taskId = -1; // -1 indicates a NEW task
    private String originalTitle, originalDesc;

    // --- onCreate Method ---
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        // Initialize views
        Toolbar toolbar = findViewById(R.id.toolbar_task);
        edTaskTitle = findViewById(R.id.edTaskTitle);
        edTaskDesc = findViewById(R.id.edTaskDesc);
        fabSaveTask = findViewById(R.id.fab_save_task);

        // Initialize Database Helper
        taskDB = new TaskDatabaseHelper(this);

        // --- GET DATA FROM INTENT AND SETUP UI ---
        getIntentDataAndSetupUI(toolbar);

        // Handle back button (finish)
        toolbar.setNavigationOnClickListener(v -> finish());

        // Set up Save/Update Button Listener
        fabSaveTask.setOnClickListener(v -> saveOrUpdateTask());
    }

    // --- New Method to Handle Intent Data and UI Setup ---
    private void getIntentDataAndSetupUI(Toolbar toolbar) {
        Intent intent = getIntent();

        // Check if an existing task ID was passed
        taskId = intent.getIntExtra("id", -1);

        if (taskId != -1) {
            // --- EDIT MODE ---
            originalTitle = intent.getStringExtra("title");
            originalDesc = intent.getStringExtra("description");

            // Populate EditText fields
            edTaskTitle.setText(originalTitle);
            edTaskDesc.setText(originalDesc);

            // Set Toolbar for editing
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Edit Task");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        } else {
            // --- NEW TASK MODE ---
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("New Task");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    // --- New Method to Switch between Save and Update ---
    private void saveOrUpdateTask() {
        String title = edTaskTitle.getText().toString().trim();
        String description = edTaskDesc.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Task title cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (taskId == -1) {
            // --- SAVE NEW TASK ---
            taskDB.addTask(title, description);
        } else {
            // --- UPDATE EXISTING TASK ---

            // Only update if content has changed to save database writes
            if (!title.equals(originalTitle) || !description.equals(originalDesc)) {
                taskDB.updateTask(String.valueOf(taskId), title, description);
            }
        }

        // Close the activity and return to the main list
        finish();
    }
}