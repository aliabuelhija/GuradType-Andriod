package com.example.guradtype.API.Requests;

public class KeyboardChangeRequest {
    private String username;
    private String old_keyboard;
    private String new_keyboard;

    public KeyboardChangeRequest(String username, String old_keyboard, String new_keyboard) {
        this.username = username;
        this.old_keyboard = old_keyboard;
        this.new_keyboard = new_keyboard;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOld_keyboard() {
        return old_keyboard;
    }

    public void setOld_keyboard(String old_keyboard) {
        this.old_keyboard = old_keyboard;
    }

    public String getNew_keyboard() {
        return new_keyboard;
    }

    public void setNew_keyboard(String new_keyboard) {
        this.new_keyboard = new_keyboard;
    }
}
