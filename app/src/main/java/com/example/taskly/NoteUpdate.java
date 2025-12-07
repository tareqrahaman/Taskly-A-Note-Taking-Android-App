package com.example.taskly;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class NoteUpdate extends AppCompatActivity {

    EditText edTitle, edDesc;
    FloatingActionButton fabUpdate;
    Toolbar toolbar;

    // Variables to hold the data passed from the adapter
    String id, title, description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1. Fullscreen Flag
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_note_update);

        // 1. Initialize Views
        edTitle = findViewById(R.id.edTitle_update);
        edDesc = findViewById(R.id.edDesc_update);
        fabUpdate = findViewById(R.id.fab_update_note);
        toolbar = findViewById(R.id.toolbar_update);

        // 2. Setup Toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // 3. Get Data from Intent and Set to Views
        getAndSetIntentData();

        // 4. Handle Update Click
        fabUpdate.setOnClickListener(v -> {
            String newTitle = edTitle.getText().toString().trim();
            String newDesc = edDesc.getText().toString().trim();

            if (!newTitle.isEmpty() || !newDesc.isEmpty()) {
                NoteDatabaseHelper myDB = new NoteDatabaseHelper(NoteUpdate.this);

                // Call the update method we created in Step 1
                myDB.updateData(id, newTitle, newDesc);

                finish(); // Close activity and return to list
            } else {
                Toast.makeText(this, "Cannot save empty note!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Helper method to retrieve data sent from NoteAdapter
    void getAndSetIntentData() {
        if (getIntent().hasExtra("id") &&
                getIntent().hasExtra("title") &&
                getIntent().hasExtra("description")) {

            // Get data
            // NoteModel uses Int for ID, but we need String for database arguments
            id = String.valueOf(getIntent().getIntExtra("id", 0));
            title = getIntent().getStringExtra("title");
            description = getIntent().getStringExtra("description");

            // Set data to EditTexts
            edTitle.setText(title);
            edDesc.setText(description);

        } else {
            Toast.makeText(this, "No data.", Toast.LENGTH_SHORT).show();
        }
    }
}