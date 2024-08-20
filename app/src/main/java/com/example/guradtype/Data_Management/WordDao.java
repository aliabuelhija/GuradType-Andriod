package com.example.guradtype.Data_Management;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WordDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Word word);

    @Delete
    void delete(Word word);

    @Query("SELECT * FROM words WHERE word = :word")
    Word findByWord(String word);

    @Query("DELETE FROM words WHERE word = :word")
    void deleteByWord(String word);

    @Query("SELECT * FROM words")
    List<Word> getAllWords();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<Word> words);
}
