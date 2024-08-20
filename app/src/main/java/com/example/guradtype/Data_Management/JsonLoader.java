package com.example.guradtype.Data_Management;

import android.content.Context;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class JsonLoader {

    public static void loadWordsFromJson(Context context, String jsonFilePath) {
        Gson gson = new Gson();
        try {
            InputStream is = context.getAssets().open(jsonFilePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            Word[] wordsArray = gson.fromJson(reader, Word[].class);
            reader.close();

            List<Word> words = Arrays.asList(wordsArray);
            WordDatabase db = WordDatabase.getDatabase(context);
            WordDao dao = db.wordDao();
            new Thread(() -> dao.insertAll(words)).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
