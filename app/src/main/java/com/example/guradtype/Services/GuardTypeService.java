package com.example.guradtype.Services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.inputmethodservice.InputMethodService;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.guradtype.API.ApiService;
import com.example.guradtype.API.Requests.CheckSentenceRequest;
import com.example.guradtype.API.Requests.RecognizeWordEntryRequest;
import com.example.guradtype.API.Responses.CheckSentenceResponse;
import com.example.guradtype.API.Responses.RecognizeWordEntryResponse;
import com.example.guradtype.API.RetrofitClient;
import com.example.guradtype.Data_Management.Word;
import com.example.guradtype.Data_Management.WordDatabase;
import com.example.guradtype.R;
import com.example.guradtype.databinding.GuardTypeBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GuardTypeService extends InputMethodService {

    private static final String TAG = "ptttt";
    private static final String PREFS_NAME = "GuardTypePrefs";
    private static final String PREF_IS_LOGGED_IN = "isLoggedIn";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_PASSWORD = "password";
    private static final String JSON_FILE_NAME = "words.json";
    private static final String INTENT_ACTION_REFRESH_WORDS = "com.example.guradtype.REFRESH_WORDS";

    private GuardTypeBinding binding;
    private boolean isUppercase = true;
    private View rootView;
    private Set<String> offensiveWords = new HashSet<>();
    private StringBuilder typedText = new StringBuilder();
    private Set<String> checkedWords = new HashSet<>();
    private Set<String> detectedOffensiveWords = new HashSet<>();
    private String username;
    private String password;

    private final String[] englishUppercaseChars = {
            "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P",
            "A", "S", "D", "F", "G", "H", "J", "K", "L",
            "Z", "X", "C", "V", "B", "N", "M"
    };

    @Override
    public View onCreateInputView() {
        if (!isUserLoggedIn()) {
            Toast.makeText(this, "Please log in to the app to enable the keyboard", Toast.LENGTH_SHORT).show();
            switchToDefaultKeyboard();
            return null;
        }

        if (!isGuardTypeKeyboardEnabled()) {
            Toast.makeText(this, "Please enable GuardType Keyboard from the settings.", Toast.LENGTH_SHORT).show();
            switchToDefaultKeyboard();
            return null;
        }

        startKeyboardMonitorService();

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.guard_type, null);
        binding = GuardTypeBinding.bind(rootView);

        loadCredentials();
        loadWordsFromJson(this, JSON_FILE_NAME);
        loadOffensiveWords();

        setUpButtonClickListeners();
        capClick();
        updateKeyboardLayout();

        setInputView(binding.getRoot());
        return binding.getRoot();
    }

    private boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(PREF_IS_LOGGED_IN, false);
    }

    private boolean isGuardTypeKeyboardEnabled() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        String defaultInputMethod = Settings.Secure.getString(getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
        List<InputMethodInfo> inputMethodInfos = imm.getEnabledInputMethodList();
        for (InputMethodInfo imi : inputMethodInfos) {
            if (imi.getId().equals(defaultInputMethod) && imi.getPackageName().equals(getPackageName())) {
                return true;
            }
        }
        return false;
    }

    private void loadCredentials() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        username = sharedPreferences.getString(PREF_USERNAME, "Unknown");
        password = sharedPreferences.getString(PREF_PASSWORD, "Unknown");
    }

    private void loadWordsFromJson(Context context, String fileName) {
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            InputStreamReader reader = new InputStreamReader(inputStream);
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Word>>() {
            }.getType();
            List<Word> words = gson.fromJson(reader, listType);
            reader.close();

            WordDatabase db = WordDatabase.getDatabase(context);
            new Thread(() -> {
                for (Word word : words) {
                    if (db.wordDao().findByWord(word.getWord()) == null) {
                        db.wordDao().insert(word);
                    }
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadOffensiveWords() {
        new Thread(() -> {
            WordDatabase db = WordDatabase.getDatabase(getApplicationContext());
            List<Word> words = db.wordDao().getAllWords();
            synchronized (offensiveWords) {
                offensiveWords.clear();
                for (Word word : words) {
                    offensiveWords.add(word.getWord().toLowerCase()); // Store words in lowercase
                }
            }
        }).start();
    }

    private void updateKeyboardLayout() {
        String[] currentLayout;
        if (isUppercase) {
            currentLayout = englishUppercaseChars;
        } else {
            currentLayout = convertToLowerCase(englishUppercaseChars);
        }
        updateKeys(currentLayout);
    }

    private void updateKeys(String[] layout) {
        Button[] buttons = {
                binding.buttonQ, binding.buttonW, binding.buttonE, binding.buttonR, binding.buttonT,
                binding.buttonY, binding.buttonU, binding.buttonI, binding.buttonO, binding.buttonP,
                binding.buttonA, binding.buttonS, binding.buttonD, binding.buttonF, binding.buttonG,
                binding.buttonH, binding.buttonJ, binding.buttonK, binding.buttonL, binding.buttonZ,
                binding.buttonX, binding.buttonC, binding.buttonV, binding.buttonB, binding.buttonN, binding.buttonM
        };
        for (int i = 0; i < layout.length; i++) {
            buttons[i].setText(layout[i]);
        }
    }

    private String[] convertToLowerCase(String[] uppercaseChars) {
        String[] lowercaseChars = new String[uppercaseChars.length];
        for (int i = 0; i < uppercaseChars.length; i++) {
            lowercaseChars[i] = uppercaseChars[i].toLowerCase();
        }
        return lowercaseChars;
    }

    private void setUpButtonClickListeners() {
        View.OnClickListener buttonClickListener = v -> {
            if (v instanceof Button) {
                buttonClick(((Button) v).getText().toString());
            }
        };

        binding.button1.setOnClickListener(buttonClickListener);
        binding.button2.setOnClickListener(buttonClickListener);
        binding.button3.setOnClickListener(buttonClickListener);
        binding.button4.setOnClickListener(buttonClickListener);
        binding.button5.setOnClickListener(buttonClickListener);
        binding.button6.setOnClickListener(buttonClickListener);
        binding.button7.setOnClickListener(buttonClickListener);
        binding.button8.setOnClickListener(buttonClickListener);
        binding.button9.setOnClickListener(buttonClickListener);
        binding.button0.setOnClickListener(buttonClickListener);
        binding.buttonQ.setOnClickListener(buttonClickListener);
        binding.buttonW.setOnClickListener(buttonClickListener);
        binding.buttonE.setOnClickListener(buttonClickListener);
        binding.buttonR.setOnClickListener(buttonClickListener);
        binding.buttonT.setOnClickListener(buttonClickListener);
        binding.buttonY.setOnClickListener(buttonClickListener);
        binding.buttonU.setOnClickListener(buttonClickListener);
        binding.buttonI.setOnClickListener(buttonClickListener);
        binding.buttonO.setOnClickListener(buttonClickListener);
        binding.buttonP.setOnClickListener(buttonClickListener);
        binding.buttonA.setOnClickListener(buttonClickListener);
        binding.buttonS.setOnClickListener(buttonClickListener);
        binding.buttonD.setOnClickListener(buttonClickListener);
        binding.buttonF.setOnClickListener(buttonClickListener);
        binding.buttonG.setOnClickListener(buttonClickListener);
        binding.buttonH.setOnClickListener(buttonClickListener);
        binding.buttonJ.setOnClickListener(buttonClickListener);
        binding.buttonK.setOnClickListener(buttonClickListener);
        binding.buttonL.setOnClickListener(buttonClickListener);
        binding.buttonZ.setOnClickListener(buttonClickListener);
        binding.buttonX.setOnClickListener(buttonClickListener);
        binding.buttonC.setOnClickListener(buttonClickListener);
        binding.buttonV.setOnClickListener(buttonClickListener);
        binding.buttonB.setOnClickListener(buttonClickListener);
        binding.buttonN.setOnClickListener(buttonClickListener);
        binding.buttonM.setOnClickListener(buttonClickListener);
        // Special buttons
        binding.buttonDelete.setOnClickListener(view -> deleteButtonClick());
        binding.buttonSpace.setOnClickListener(view -> spaceButtonClick());
        binding.buttonEnter.setOnClickListener(view -> enterButtonClick());
    }

    private void capClick() {
        binding.buttonCap.setOnClickListener(view -> {
            isUppercase = !isUppercase;
            if (isUppercase) {
                binding.buttonCap.setBackgroundResource(R.drawable.caps_down);
            } else {
                binding.buttonCap.setBackgroundResource(R.drawable.caps_up);
            }
            updateKeyboardLayout();
        });
    }


    private void buttonClick(String buttonText) {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            ic.commitText(buttonText, 1);
            typedText.append(buttonText.toLowerCase()); // Convert typed text to lowercase
        }
    }

    private void deleteButtonClick() {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            ic.deleteSurroundingText(1, 0); // Delete one character before the cursor
            if (typedText.length() > 0) {
                typedText.deleteCharAt(typedText.length() - 1);
            }
        }
    }

    private void spaceButtonClick() {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            ic.commitText(" ", 1);
            typedText.append(" ");
        }
    }

    private void enterButtonClick() {
        InputConnection ic = getCurrentInputConnection();

        if (ic != null) {
            CharSequence currentText = ic.getExtractedText(new ExtractedTextRequest(), 0).text;
            if (currentText != null) {
                typedText.setLength(0);
                typedText.append(currentText);

                Log.d("Keyboard", "Text being checked: " + typedText.toString());

                // Check for offensive words in the entire text
                checkOffensiveWords();
            }

            // Send the enter key event
            ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
            ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));

            // Clear the text buffer and checked words list
            typedText.setLength(0);
            checkedWords.clear();
        }
    }


    private void checkOffensiveWords() {
        String[] words = typedText.toString().split("\\s+");
        boolean foundInCache = false;
        checkedWords.clear();
        for (String word : words) {
            Log.d(TAG, "Checking word: " + word);
            String lowerCaseWord = word.toLowerCase(); // Convert word to lowercase
            if (!checkedWords.contains(lowerCaseWord)) {
                checkedWords.add(lowerCaseWord); // Add the word to checkedWords whether it's offensive or not
                if (offensiveWords.contains(lowerCaseWord)) {
                    Log.e(TAG, "I found this word " + word);
                    detectedOffensiveWords.add(lowerCaseWord);
                    foundInCache = true;
                }
            }
            checkedWords.clear();
        }
        if (foundInCache) {
            sendDetectedOffensiveEntry(typedText.toString());
        }

        if (!foundInCache) {
            checkSentenceOnServer(typedText.toString());

        }
        checkedWords.clear();
        typedText.setLength(0);

    }

    private void sendDetectedOffensiveEntry(String sentence) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(PREF_USERNAME, "default_username");

        RecognizeWordEntryRequest request = new RecognizeWordEntryRequest(username, sentence);

        ApiService apiService = RetrofitClient.getApiService();
        Call<RecognizeWordEntryResponse> call = apiService.createRecognizeWordEntry(request);
        call.enqueue(new Callback<RecognizeWordEntryResponse>() {
            @Override
            public void onResponse(Call<RecognizeWordEntryResponse> call, Response<RecognizeWordEntryResponse> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Sentence entry request successful: " + response.body().toString());
                } else {
                    Log.d(TAG, "Sentence entry request failed: " + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<RecognizeWordEntryResponse> call, Throwable t) {
                Log.d(TAG, "Sentence entry request failed: " + t.getMessage());
            }
        });
    }

    private void checkSentenceOnServer(String sentence) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(PREF_USERNAME, "default_username");

        CheckSentenceRequest request = new CheckSentenceRequest(username, sentence);

        ApiService apiService = RetrofitClient.getApiService();
        Call<CheckSentenceResponse> call = apiService.checkSentence(request);
        call.enqueue(new Callback<CheckSentenceResponse>() {
            @Override
            public void onResponse(Call<CheckSentenceResponse> call, Response<CheckSentenceResponse> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Sentence check request successful: " + response.body().toString());
                } else {
                    Log.d(TAG, "Sentence check request failed: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<CheckSentenceResponse> call, Throwable t) {
                Log.d(TAG, "Sentence check request failed: " + t.getMessage());
            }
        });
    }

    private void switchToDefaultKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            // Show input method picker
            imm.showInputMethodPicker();

            // Display toast message on the UI thread
            new Handler(Looper.getMainLooper()).post(() ->
                    Toast.makeText(getApplicationContext(), "Please select another keyboard from the settings.", Toast.LENGTH_LONG).show()
            );
        } else {
            new Handler(Looper.getMainLooper()).post(() ->
                    Toast.makeText(getApplicationContext(), "Failed to switch keyboard. Please select another keyboard manually.", Toast.LENGTH_LONG).show()
            );
        }
    }

    private void startKeyboardMonitorService() {
        Intent intent = new Intent(this, KeyboardMonitorService.class);
        startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && INTENT_ACTION_REFRESH_WORDS.equals(intent.getAction())) {
            loadOffensiveWords();
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
