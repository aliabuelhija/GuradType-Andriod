package com.example.guradtype.Utils;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.List;

public class IntegerValueFormatter extends ValueFormatter {
    @Override
    public String getFormattedValue(float value) {
        return String.valueOf((int) value);
    }
}

