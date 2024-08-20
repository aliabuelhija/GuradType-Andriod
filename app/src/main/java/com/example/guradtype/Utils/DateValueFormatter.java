package com.example.guradtype.Utils;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.List;

public class DateValueFormatter extends ValueFormatter {
    private final List<String> dates;

    public DateValueFormatter(List<String> dates) {
        this.dates = dates;
    }

    @Override
    public String getFormattedValue(float value) {
        int index = (int) value;
        if (index >= 0 && index < dates.size()) {
            String date = dates.get(index);
            return date.substring(5); // מחזיר את התאריך ללא השנה
        }
        return "";
    }
}
