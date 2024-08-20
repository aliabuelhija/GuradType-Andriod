package com.example.guradtype.UI_Components;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.guradtype.Services.GuardTypeService;
import com.example.guradtype.R;
import com.example.guradtype.Data_Management.Word;
import com.example.guradtype.Data_Management.WordDatabase;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String PREF_USERNAME = "username";

    private static final String PREFS_NAME = "GuardTypePrefs";

    private String username;
    private String password;
   final String RefreshDB = "com.example.guradtype.REFRESH_WORDS" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Intent intent = getIntent();
        if (intent != null) {
            username = intent.getStringExtra("username");
            password = intent.getStringExtra("password");
        }

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
         username = sharedPreferences.getString(PREF_USERNAME, "default_username");
        initViews();
    }

    private void initViews() {
        TextView textViewHelp = findViewById(R.id.textView_help);
        TextView textViewEnableKeyboard = findViewById(R.id.textView_enable_keyboard);
        TextView textViewExit = findViewById(R.id.textView_exit);
        TextView textViewAddWords = findViewById(R.id.textView_add_words);
        TextView textViewRemoveWords = findViewById(R.id.textView_remove_words);
        TextView textViewShowStatics = findViewById(R.id.textView_show_statistics);


        textViewHelp.setOnClickListener(view -> help());
        textViewEnableKeyboard.setOnClickListener(this::enableKeyboard);
        textViewExit.setOnClickListener(view -> logout());
        textViewAddWords.setOnClickListener(view -> showCustomDialog("Add Word"));
        textViewRemoveWords.setOnClickListener(view -> showCustomDialog("Remove Word"));
        textViewShowStatics.setOnClickListener(view -> showStaticOptions());
        textViewHelp.setOnClickListener(view -> help());


    }

    private void help() {
        Intent intent = new Intent(MainActivity.this, HelpActivity.class);
        startActivity(intent);
        finish();
    }

    private void showStaticOptions() {
        Intent intent = new Intent(MainActivity.this, ActivityChooseStatistics.class);
        intent.putExtra(username, username);
        startActivity(intent);
        finish();
    }




    private void enableKeyboard(View view) {
        if (!areParentDetailsAvailable()) {
            Toast.makeText(this, "Please enter parent details first", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isKeyboardEnabled()) {
            Toast.makeText(this, "Please enable GuardType Keyboard from the settings.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            Toast.makeText(this, "Please select 'GuardType Keyboard' from the list", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "GuardType Keyboard is already enabled.", Toast.LENGTH_SHORT).show();
        }
    }

    private void logout() {
        editLoginState();
        startLoginActivity();
    }

    private void editLoginState() {
        SharedPreferences preferences = getSharedPreferences("GuardTypePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, ActivityLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private boolean areParentDetailsAvailable() {
        SharedPreferences preferences = getSharedPreferences("GuardTypePrefs", Context.MODE_PRIVATE);
        return preferences.contains("username") && preferences.contains("password");
    }

    private boolean isKeyboardEnabled() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        List<InputMethodInfo> inputMethodInfos = imm.getEnabledInputMethodList();
        String packageName = getPackageName();
        for (InputMethodInfo imi : inputMethodInfos) {
            if (imi.getPackageName().equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    private boolean isUserLoggedIn() {
        SharedPreferences preferences = getSharedPreferences("GuardTypePrefs", Context.MODE_PRIVATE);
        return preferences.getBoolean("isLoggedIn", false);
    }

    private void showCustomDialog(String title) {
        // Inflate the custom layout/view
        LayoutInflater inflater = getLayoutInflater();
        View customDialogView = inflater.inflate(R.layout.my_dialog, null);

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(customDialogView);

        // Set up the buttons
        AlertDialog dialog = builder.create();

        // custom dialog views
        TextView dialogTitle = customDialogView.findViewById(R.id.dialogTitle);
        EditText editTextWord = customDialogView.findViewById(R.id.editTextWord);
        EditText editTextPassword = customDialogView.findViewById(R.id.editTextPassword);

        Button buttonSubmit = customDialogView.findViewById(R.id.buttonSubmit);

        dialogTitle.setText(title);

        buttonSubmit.setOnClickListener(view -> {
            String word = editTextWord.getText().toString().trim();
            String inputPassword = editTextPassword.getText().toString();
            Log.d("ddddddd",inputPassword );
            Log.d("ddddddd",password );
            Log.d("word",password );


            if (word.isEmpty()) {
                Toast.makeText(MainActivity.this, "Word cannot be empty or just spaces", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!inputPassword.equals(password)) {
                Toast.makeText(MainActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                return;
            }
            if (title.equals("Add Word")) {
                addWord(word);
            } else if (title.equals("Remove Word")) {
                removeWord(word);
            }

            dialog.dismiss();
        });

        dialog.show();
    }

    private void addWord(String word) {
        new Thread(() -> {
            WordDatabase db = WordDatabase.getDatabase(getApplicationContext());
            Word existingWord = db.wordDao().findByWord(word);
            if (existingWord == null) {
                Word newWord = new Word(word);
                db.wordDao().insert(newWord);
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Word added: " + word, Toast.LENGTH_SHORT).show();
                    refreshKeyboardWords();
                });
            } else {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Word already exists: " + word, Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void removeWord(String word) {
        new Thread(() -> {
            WordDatabase db = WordDatabase.getDatabase(getApplicationContext());
            Word wordToRemove = db.wordDao().findByWord(word);
            if (wordToRemove != null) {
                db.wordDao().delete(wordToRemove);
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Word removed: " + word, Toast.LENGTH_SHORT).show();
                    refreshKeyboardWords();
                });
            } else {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Word not found: " + word, Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void refreshKeyboardWords() {
        Intent intent = new Intent(this, GuardTypeService.class);
        intent.setAction(RefreshDB);
        startService(intent);
    }

}
