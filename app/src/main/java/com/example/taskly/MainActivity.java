package com.example.taskly;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.widget.NestedScrollView; // NEW: Required for scrolling combined lists

import android.app.AlertDialog; // NEW
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater; // NEW
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText; // NEW
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // --- Variables ---
    FloatingActionButton addIcon, fabNewNote, fabNewTask;
    RelativeLayout noteFabContainer, taskFabContainer;
    TextView labelNewNote, labelNewTask;

    // Animation
    boolean isOpen = false;
    Animation fabOpen, fabClose, labelOpen, labelClose;

    // Database & List
    // CHANGED: Two separate RecyclerViews
    RecyclerView noteRecyclerView, taskRecyclerView;

    // NOTE Data
    NoteDatabaseHelper noteDB; // Changed name from myDB for clarity
    ArrayList<NoteModel> noteList;
    NoteAdapter noteAdapter;

    // TASK Data
    TaskDatabaseHelper taskDB; // NEW
    ArrayList<TaskModel> taskList; // NEW
    TaskAdapter taskAdapter; // NEW

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1. Fullscreen Flag
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        // 2. Initialize Views (Updated IDs)
        addIcon = findViewById(R.id.add_icon);
        noteFabContainer = findViewById(R.id.note_fab_container);
        taskFabContainer = findViewById(R.id.task_fab_container);
        fabNewNote = findViewById(R.id.fab_new_note);
        fabNewTask = findViewById(R.id.fab_new_task);
        labelNewNote = findViewById(R.id.label_new_note);
        labelNewTask = findViewById(R.id.label_new_task);

        // CHANGED: Initialize two RecyclerViews
        noteRecyclerView = findViewById(R.id.noteRecyclerView);
        taskRecyclerView = findViewById(R.id.taskRecyclerView);

        // 3. Initialize Animations (Unchanged)
        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        labelOpen = AnimationUtils.loadAnimation(this, R.anim.label_open);
        labelClose = AnimationUtils.loadAnimation(this, R.anim.label_close);

        // 4. Setup Databases and Lists
        noteDB = new NoteDatabaseHelper(MainActivity.this);
        taskDB = new TaskDatabaseHelper(MainActivity.this); // NEW

        noteList = new ArrayList<>();
        taskList = new ArrayList<>(); // NEW

        // 5. Setup RecyclerViews

        // --- Note Setup ---
        noteAdapter = new NoteAdapter(MainActivity.this, noteList);
        noteRecyclerView.setAdapter(noteAdapter);
        noteRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteRecyclerView.setNestedScrollingEnabled(false); // Disable RecyclerView scrolling

        // --- Task Setup ---
        taskAdapter = new TaskAdapter(MainActivity.this, taskList);
        taskRecyclerView.setAdapter(taskAdapter);
        LinearLayoutManager taskLayoutManager = new LinearLayoutManager(this);
        taskLayoutManager.setReverseLayout(true);
        taskLayoutManager.setStackFromEnd(true); // Ensures list starts scrolled to the bottom (the newest item)

        taskRecyclerView.setLayoutManager(taskLayoutManager);
        taskRecyclerView.setNestedScrollingEnabled(false);

        // Load data immediately
        loadData(); // NEW combined loading method

        // 6. Click Listeners
        addIcon.setOnClickListener(v -> animateFab());

        fabNewNote.setOnClickListener(v -> {
            closeMenu();
            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
            startActivity(intent);
        });

        fabNewTask.setOnClickListener(v -> {
            closeMenu();
            Intent intent = new Intent(MainActivity.this, TaskActivity.class);
            startActivity(intent);
        });

        // Animation Listener (Unchanged)
        fabClose.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                if (!isOpen) {
                    noteFabContainer.setVisibility(View.GONE);
                    taskFabContainer.setVisibility(View.GONE);
                    labelNewNote.setVisibility(View.GONE);
                    labelNewTask.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    // --- Lifecycle Method ---
    @Override
    protected void onResume() {
        super.onResume();
        loadData(); // Refresh both lists every time we return
    }

    // --- Helper Method: Load ALL Data (COMBINED) ---
    void loadData() {
        // 1. Load Notes
        noteList.clear();
        Cursor noteCursor = noteDB.readAllData();
        if (noteCursor != null && noteCursor.getCount() > 0) {
            while (noteCursor.moveToNext()) {
                noteList.add(new NoteModel(
                        Integer.parseInt(noteCursor.getString(0)),
                        noteCursor.getString(1),
                        noteCursor.getString(2)
                ));
            }
        }
        noteAdapter.notifyDataSetChanged();

        // 2. Load Tasks
        taskList.clear();
        taskList.addAll(taskDB.readAllTasks());
        taskAdapter.notifyDataSetChanged();
    }

    // --- Helper: Quick Dialog to Add Task (NEW) ---
    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_new_task, null);
        builder.setView(dialogView);

        final EditText etTitle = dialogView.findViewById(R.id.edTaskTitle);
        final EditText etDesc = dialogView.findViewById(R.id.edTaskDesc);

        builder.setTitle("New Task")
                .setPositiveButton("Add", (dialog, which) -> {
                    String title = etTitle.getText().toString().trim();
                    String desc = etDesc.getText().toString().trim();
                    if(!title.isEmpty()){
                        taskDB.addTask(title, desc);
                        loadData(); // Refresh both lists after adding
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }


    // --- Helper Method: Animations (Unchanged) ---
    private void animateFab() {
        if (!isOpen) {
            // OPEN
            noteFabContainer.setVisibility(View.VISIBLE);
            taskFabContainer.setVisibility(View.VISIBLE);
            labelNewNote.setVisibility(View.VISIBLE);
            labelNewTask.setVisibility(View.VISIBLE);

            fabNewNote.startAnimation(fabOpen);
            fabNewTask.startAnimation(fabOpen);
            labelNewNote.startAnimation(labelOpen);
            labelNewTask.startAnimation(labelOpen);

            addIcon.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.fab_open));
            isOpen = true;
        } else {
            // CLOSE
            closeMenu();
        }
    }

    private void closeMenu() {
        if (isOpen) {
            fabNewNote.startAnimation(fabClose);
            fabNewTask.startAnimation(fabClose);
            labelNewNote.startAnimation(labelClose);
            labelNewTask.startAnimation(labelClose);

            addIcon.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.deep_blue));
            isOpen = false;
        }
    }
}