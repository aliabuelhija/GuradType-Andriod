package com.example.guradtype.API.Responses;
import com.google.type.Date;

public class RecognizeWordEntryResponse {
    private int id;
    private String username;
    private String text;
    private String  date;
    private String source;

    public RecognizeWordEntryResponse(int id, String username, String text, String  date) {
        this.id = id;
        this.username = username;
        this.text = text;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "RecognizeWordEntryResponse{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", text='" + text + '\'' +
                ", date='" + date + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}
