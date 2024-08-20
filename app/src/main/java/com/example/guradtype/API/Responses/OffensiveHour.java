package com.example.guradtype.API.Responses;

public class OffensiveHour {
    private String time_range;
    private int count;


    public OffensiveHour() {
    }

    public OffensiveHour(String time_range, int count) {
        this.time_range = time_range;
        this.count = count;
    }

    public String getTime_range() {
        return time_range;
    }

    public void setTime_range(String time_range) {
        this.time_range = time_range;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
