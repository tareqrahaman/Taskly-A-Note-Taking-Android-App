package com.example.taskly;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class NoteDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "TasklyNotes.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "my_notes";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TITLE = "note_title";
    private static final String COLUMN_DESC = "note_desc";
    private final Context context;

    public NoteDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_DESC + " TEXT);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // --- ADD NOTE ---
    public void addNote(String title, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_DESC, description);

        long result = db.insert(TABLE_NAME, null, cv);

        if (result == -1) {
            Toast.makeText(context, "Failed to save note", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Note Saved!", Toast.LENGTH_SHORT).show();
        }
        db.close(); // Always close the DB connection
    }

    // You will need methods like readAllData(), updateData(), deleteData() here later
    // matching your Tutor's logic for the Adapter.
    public void deleteData(String row_id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, "_id=?", new String[]{row_id});
        if(result == -1){
            Toast.makeText(context, "Failed to Delete.", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Successfully Deleted.", Toast.LENGTH_SHORT).show();
        }
    }

    public Cursor readAllData() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        // A Cursor is like a pointer that moves through the rows of your database
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public void updateData(String row_id, String title, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        // Put the new values
        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_DESC, description);

        // Update the row where the ID matches
        long result = db.update(TABLE_NAME, cv, "_id=?", new String[]{row_id});

        if(result == -1){
            Toast.makeText(context, "Failed to Update!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Note Updated!", Toast.LENGTH_SHORT).show();
        }
    }
}