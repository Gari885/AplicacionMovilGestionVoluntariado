package com.example.appgestionvoluntariado.Models.Request;

import com.google.gson.annotations.SerializedName;

public class GoogleLoginRequest {
    @SerializedName("token")
    private String token;

    public GoogleLoginRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
