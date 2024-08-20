package com.example.guradtype.UI_Components;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.guradtype.API.ApiService;
import com.example.guradtype.API.Responses.FrequentWord;
import com.example.guradtype.API.RetrofitClient;
import com.example.guradtype.R;
import com.example.guradtype.Utils.IntegerValueFormatter;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityFrequentWordsStatistics extends AppCompatActivity {
    private static final String PREF_USERNAME = "username";
    private static final String PREFS_NAME = "GuardTypePrefs";
    private PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frequent_words);

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(PREF_USERNAME, "default_username");

        pieChart = findViewById(R.id.pie_chart);
        TextView back = findViewById(R.id.back);
        back.setOnClickListener(view -> {
            Intent intent = new Intent(ActivityFrequentWordsStatistics.this, ActivityChooseStatistics.class);
            startActivity(intent);
            finish();
        });

        ApiService apiService = RetrofitClient.getApiService();
        apiService.getFrequentWords(username).enqueue(new Callback<List<FrequentWord>>() {
            @Override
            public void onResponse(Call<List<FrequentWord>> call, Response<List<FrequentWord>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayData(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<FrequentWord>> call, Throwable t) {
            }
        });
    }

    private void displayData(List<FrequentWord> frequentWords) {
        List<PieEntry> entries = new ArrayList<>();
        for (FrequentWord word : frequentWords) {
            entries.add(new PieEntry(word.getCount(), word.getWord()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Most Offensive Words");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextSize(16f); // Increase value text size
        dataSet.setValueTextColor(Color.BLACK); // Set value text color

        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new IntegerValueFormatter());

        pieChart.setData(pieData);
        pieChart.setEntryLabelTextSize(16f); // Increase entry label text size
        pieChart.setEntryLabelColor(Color.BLACK); // Set entry label text color
        pieChart.invalidate(); // Refresh the chart
    }
}
