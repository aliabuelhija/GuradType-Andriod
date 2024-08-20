package com.example.guradtype.API.Responses;
import com.google.type.Date;

public class CheckSentenceResponse {
    private int id;
    private String username;
    private String text;
    private String date;
    private String source;
    private boolean offensive;

    public CheckSentenceResponse(int id, String username, String text, String date, String source, boolean offensive) {
        this.id = id;
        this.username = username;
        this.text = text;
        this.date = date;
        this.source = source;
        this.offensive = offensive;
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isOffensive() {
        return offensive;
    }

    public void setOffensive(boolean offensive) {
        this.offensive = offensive;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "CheckSentenceResponse{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", text='" + text + '\'' +
                ", date='" + date + '\'' +
                ", source='" + source + '\'' +
                ", offensive=" + offensive +
                '}';
    }
}

