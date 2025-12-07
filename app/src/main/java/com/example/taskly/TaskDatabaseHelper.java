package com.example.taskly;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class TaskDatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "TasklyTasks.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "task_table";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TITLE = "task_title";
    private static final String COLUMN_DESC = "task_description";
    private static final String COLUMN_STATUS = "task_status"; // 0=Pending, 1=Completed

    public TaskDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_TITLE + " TEXT, " +
                        COLUMN_DESC + " TEXT, " +
                        COLUMN_STATUS + " INTEGER);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // --- CREATE ---
    public void addTask(String title, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_DESC, description);
        cv.put(COLUMN_STATUS, 0); // Default status is 0 (Pending)

        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Failed to add Task.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Task Added Successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    // --- READ ---
    public ArrayList<TaskModel> readAllTasks() {
        ArrayList<TaskModel> arrayList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ID + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        if (db != null) {
            cursor = db.rawQuery(query, null);
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String title = cursor.getString(1);
                String description = cursor.getString(2);
                int status = cursor.getInt(3);
                arrayList.add(new TaskModel(id, title, description, status));
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        return arrayList;
    }

    // --- UPDATE STATUS (Crucial for a Todo List) ---
    public void updateTaskStatus(int row_id, int newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_STATUS, newStatus);

        long result = db.update(TABLE_NAME, cv, COLUMN_ID + "=?", new String[]{String.valueOf(row_id)});

        if (result == -1) {
            Toast.makeText(context, "Status change failed.", Toast.LENGTH_SHORT).show();
        }
    }

    // --- DELETE ---
    public void deleteTask(String row_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, "_id=?", new String[]{row_id});

        if (result == -1) {
            Toast.makeText(context, "Failed to Delete Task.", Toast.LENGTH_SHORT).show();
        }
    }

    // --- READ SINGLE TASK ---
    public TaskModel readSingleTask(int row_id) {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = " + row_id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        TaskModel task = null;

        if (db != null) {
            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(0);
                String title = cursor.getString(1);
                String description = cursor.getString(2);
                int status = cursor.getInt(3); // Assuming status is column index 3

                task = new TaskModel(id, title, description, status);
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        return task;
    }

    // --- UPDATE TASK ---
    public void updateTask(String row_id, String title, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_DESC, description);

        long result = db.update(TABLE_NAME, cv, COLUMN_ID + "=?", new String[]{row_id});

        if (result == -1) {
            Toast.makeText(context, "Task update failed!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Task Updated!", Toast.LENGTH_SHORT).show();
        }
    }
}