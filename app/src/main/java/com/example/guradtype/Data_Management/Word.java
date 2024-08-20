package com.example.guradtype.Data_Management;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "words")
public class Word {
    @PrimaryKey
    @NonNull
    private String word;

    public Word(@NonNull String word) {
        this.word = word;
    }

    @NonNull
    public String getWord() {
        return word;
    }

    public void setWord(@NonNull String word) {
        this.word = word;
    }
}
