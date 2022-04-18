package com.example.isiuniversity.models;

import com.google.gson.annotations.SerializedName;

public class UserResponse {
    @SerializedName("token") //  "to" changed to token
    private String token;

    private boolean error;
    private User user;

    public UserResponse(boolean error, User user) {
        this.error = error;
        this.user = user;
    }

    public boolean isError() {
        return error;
    }

    public User getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
