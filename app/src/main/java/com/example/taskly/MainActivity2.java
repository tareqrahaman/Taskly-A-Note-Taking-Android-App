package com.example.taskly;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity2 extends AppCompatActivity {

    EditText edTitle, edDesc;
    FloatingActionButton fabSave;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1. Fullscreen Flag
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_new_note);

        // 1. Initialize Views
        edTitle = findViewById(R.id.edTitle);
        edDesc = findViewById(R.id.edDesc);
        fabSave = findViewById(R.id.fab_save_note);
        toolbar = findViewById(R.id.toolbar_note);

        // 2. Setup Toolbar (The Back Arrow)
        setSupportActionBar(toolbar);
        // This enables the back arrow
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // This handles the click event of the back arrow
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // 3. Handle Save Button Click
        fabSave.setOnClickListener(v -> {
            String title = edTitle.getText().toString().trim();
            String description = edDesc.getText().toString().trim();

            if (!title.isEmpty() || !description.isEmpty()) {
                // Save to Database
                NoteDatabaseHelper myDB = new NoteDatabaseHelper(MainActivity2.this);
                myDB.addNote(title, description);

                // Close this activity and go back to Main
                finish();
            } else {
                Toast.makeText(this, "Please type something...", Toast.LENGTH_SHORT).show();
            }
        });
    }
}