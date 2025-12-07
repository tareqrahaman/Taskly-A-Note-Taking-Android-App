package com.example.taskly;

public class NoteModel {

    private int id;
    private String title;
    private String description;

    // Constructor for reading from DB
    public NoteModel(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    // Constructor for creating new notes (ID is auto-generated)
    public NoteModel(String title, String description) {
        this.title = title;
        this.description = description;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
