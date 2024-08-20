package com.example.guradtype.API.Responses;

public class FrequentWord {
    private String word;
    private int count;


    public FrequentWord() {

    }

    public FrequentWord(String word, int count) {
        this.word = word;
        this.count = count;
    }

    // Getters and setters
    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
