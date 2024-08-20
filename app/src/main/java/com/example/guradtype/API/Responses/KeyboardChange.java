package com.example.guradtype.API.Responses;

public class KeyboardChange {

    private String date;
    private int changes_count;

    public KeyboardChange() {
    }

    public KeyboardChange(String date, int changes_count) {
        this.date = date;
        this.changes_count = changes_count;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getChanges_count() {
        return changes_count;
    }

    public void setChanges_count(int changes_count) {
        this.changes_count = changes_count;
    }
}
