package com.example.guradtype.API.Responses;

import com.google.type.Date;

public class KeyboardChangeResponse {
    private int id;
    private String username;
    private String oldKeyboard;
    private String newKeyboard;
    private String changeTime;

    public KeyboardChangeResponse(int id, String username, String oldKeyboard, String newKeyboard, String changeTime) {
        this.id = id;
        this.username = username;
        this.oldKeyboard = oldKeyboard;
        this.newKeyboard = newKeyboard;
        this.changeTime = changeTime;
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

    public String getOldKeyboard() {
        return oldKeyboard;
    }

    public void setOldKeyboard(String oldKeyboard) {
        this.oldKeyboard = oldKeyboard;
    }

    public String getNewKeyboard() {
        return newKeyboard;
    }

    public void setNewKeyboard(String newKeyboard) {
        this.newKeyboard = newKeyboard;
    }

    public String getChangeTime() {
        return changeTime;
    }

    public void setChangeTime(String changeTime) {
        this.changeTime = changeTime;
    }

    @Override
    public String toString() {
        return "KeyboardChangeResponse{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", oldKeyboard='" + oldKeyboard + '\'' +
                ", newKeyboard='" + newKeyboard + '\'' +
                ", changeTime='" + changeTime + '\'' +
                '}';
    }
}
