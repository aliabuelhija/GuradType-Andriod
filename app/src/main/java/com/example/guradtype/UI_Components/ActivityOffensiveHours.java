package com.example.guradtype.UI_Components;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.guradtype.API.ApiService;
import com.example.guradtype.API.Responses.OffensiveHour;
import com.example.guradtype.API.RetrofitClient;
import com.example.guradtype.R;
import com.example.guradtype.Utils.IntegerValueFormatter;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityOffensiveHours extends AppCompatActivity {
    private static final String PREF_USERNAME = "username";
    private static final String PREFS_NAME = "GuardTypePrefs";
    private HorizontalBarChart barChart;
    private static final String TAG = "OffensiveHoursActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offensive_hours);

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(PREF_USERNAME, "default_username");

        barChart = findViewById(R.id.bar_chart);
        TextView back = findViewById(R.id.back);
        back.setOnClickListener(view -> {
            Intent intent = new Intent(ActivityOffensiveHours.this, ActivityChooseStatistics.class);
            startActivity(intent);
            finish();

        });

        ApiService apiService = RetrofitClient.getApiService();
        apiService.getOffensiveHours(username).enqueue(new Callback<List<OffensiveHour>>() {
            @Override
            public void onResponse(Call<List<OffensiveHour>> call, Response<List<OffensiveHour>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<OffensiveHour> hours = response.body();
                    Log.d(TAG, "Received hours data: " + hours.toString());
                    displayData(hours);
                } else {
                    Log.d(TAG, "Response not successful or body is null. Response code: " + response.code());
                    Log.d(TAG, "Response message: " + response.message());
                    Log.d(TAG, "Response body: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<OffensiveHour>> call, Throwable t) {
                Log.e(TAG, "API call failed", t);
            }
        });
    }

    private void displayData(List<OffensiveHour> offensiveHours) {
        List<BarEntry> entries = new ArrayList<>();
        final List<String> labels = new ArrayList<>();

        for (int i = 0; i < offensiveHours.size(); i++) {
            OffensiveHour hour = offensiveHours.get(i);
            Log.d(TAG, "Hour: " + hour.getTime_range() + ", Count: " + hour.getCount());
            entries.add(new BarEntry(i, hour.getCount()));
            labels.add(hour.getTime_range());
        }

        BarDataSet dataSet = new BarDataSet(entries, "Offensive Content Hours");
        dataSet.setColor(Color.WHITE);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f); // Set text size for values inside bars

        BarData barData = new BarData(dataSet);
        barData.setValueFormatter(new IntegerValueFormatter());

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // Show every hour
        xAxis.setLabelCount(labels.size());
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value >= 0 && value < labels.size()) {
                    return labels.get((int) value);
                } else {
                    return "";
                }
            }
        });

        // Set ValueFormatter for the Y axis to show integer values
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setValueFormatter(new IntegerValueFormatter());
        leftAxis.setGranularity(1f); // Show integer values only

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setValueFormatter(new IntegerValueFormatter());
        rightAxis.setGranularity(1f); // Show integer values only

        // Enable values inside the bars
        dataSet.setValueTextSize(12f); // Set text size for values inside bars
        dataSet.setValueTextColor(Color.BLACK); // Set text color for values inside bars

        // Set click listener to show values on touch
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                // Display value as a toast message
                Toast.makeText(ActivityOffensiveHours.this, "Value: " + (int) e.getY(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {
            }
        });

        barChart.setData(barData);
        barChart.setFitBars(true); // make the bars fit into the chart
        barChart.getDescription().setEnabled(false);

        barChart.invalidate(); // refresh

        // Add zoom effect
        barChart.zoom(2f, 1f, 0, 0); // Zoom x2 on the x-axis
        barChart.moveViewToX(0); // Move the view to the start
    }
}
