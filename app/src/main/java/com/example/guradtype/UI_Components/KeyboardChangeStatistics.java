package com.example.guradtype.UI_Components;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.guradtype.API.ApiService;
import com.example.guradtype.API.Responses.KeyboardChange;
import com.example.guradtype.API.RetrofitClient;
import com.example.guradtype.R;
import com.example.guradtype.Utils.DateValueFormatter;
import com.example.guradtype.Utils.IntegerValueFormatter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KeyboardChangeStatistics extends AppCompatActivity {
    private static final String PREF_USERNAME = "username";
    private static final String PREFS_NAME = "GuardTypePrefs";
    private LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard_change);

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(PREF_USERNAME, "default_username");

        lineChart = findViewById(R.id.line_chart);
        TextView back = findViewById(R.id.back);
        back.setOnClickListener(view -> {
            Intent intent = new Intent(KeyboardChangeStatistics.this, ActivityChooseStatistics.class);
            startActivity(intent);
            finish();

        });

        ApiService apiService = RetrofitClient.getApiService();
        apiService.getKeyboardChanges(username).enqueue(new Callback<List<KeyboardChange>>() {
            @Override
            public void onResponse(Call<List<KeyboardChange>> call, Response<List<KeyboardChange>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<KeyboardChange> changes = response.body();
                    List<String> dates = new ArrayList<>();
                    for (KeyboardChange change : changes) {
                        dates.add(change.getDate());
                        Log.d("KeyboardChange", "Date: " + change.getDate() + ", Count: " + change.getChanges_count());
                    }
                    displayData(changes, dates);
                } else {
                    Log.d("KeyboardChange", "Response not successful or body is null");
                }
            }

            @Override
            public void onFailure(Call<List<KeyboardChange>> call, Throwable t) {
                Log.e("KeyboardChange", "API call failed", t);
            }
        });
    }

    private void displayData(List<KeyboardChange> keyboardChanges, List<String> dates) {
        List<Entry> entries = new ArrayList<>();
        int index = 0;
        for (KeyboardChange change : keyboardChanges) {
            entries.add(new Entry(index++, change.getChanges_count()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Keyboard Changes");
        dataSet.setColor(Color.BLUE); // Change line color
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setCircleColor(Color.WHITE); // Change circle color
        dataSet.setCircleHoleColor(Color.BLUE); // Change hole color
        dataSet.setCircleRadius(5f);
        dataSet.setLineWidth(2f);
        dataSet.setHighLightColor(Color.YELLOW); // Change highlight color
        dataSet.setDrawValues(false); // Disable values on the data points

        LineData lineData = new LineData(dataSet);
        lineData.setValueFormatter(new IntegerValueFormatter());

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new DateValueFormatter(dates));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelRotationAngle(-45);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setValueFormatter(new IntegerValueFormatter());
        leftAxis.setGranularity(1f); // Ensure granularity is 1 for integer steps

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false); // Disable the right axis

        // Update the legend
        Legend legend = lineChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setEnabled(true);

        // Remove any additional description labels
        lineChart.getDescription().setEnabled(false);

        lineChart.setData(lineData);
        lineChart.invalidate(); // refresh
    }
}
