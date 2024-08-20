package com.example.guradtype.API.Responses;

import com.example.guradtype.API.User;

public class SignUpResponse {
    private String message;
    private User user;

    public SignUpResponse(String message, User user) {
        this.message = message;
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "SignUpResponse{" +
                "message='" + message + '\'' +
                ", user=" + user +
                '}';
    }
}
