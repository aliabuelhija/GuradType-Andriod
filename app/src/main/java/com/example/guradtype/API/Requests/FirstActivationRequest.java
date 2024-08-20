package com.example.guradtype.API.Requests;

public class FirstActivationRequest {
    private String username;
    public FirstActivationRequest(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
