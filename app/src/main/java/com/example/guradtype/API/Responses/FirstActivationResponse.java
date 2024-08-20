package com.example.guradtype.API.Responses;

public class FirstActivationResponse {
    private int id;
    private String username;
    private String activation_time;

    public FirstActivationResponse(int id, String username, String activation_time) {
        this.id = id;
        this.username = username;
        this.activation_time = activation_time;
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

    public String getActivation_time() {
        return activation_time;
    }

    public void setActivation_time(String activation_time) {
        this.activation_time = activation_time;
    }

    @Override
    public String toString() {
        return "FirstActivationResponse{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", activation_time='" + activation_time + '\'' +
                '}';
    }
}
