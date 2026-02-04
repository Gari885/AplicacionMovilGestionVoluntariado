package com.example.appgestionvoluntariado.Models.Response;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("token")
    private String token;

    @SerializedName("refreshToken")
    private String refreshToken;

    @SerializedName("expiresIn")
    private String expiresIn;

    @SerializedName("localId")
    private String localId;

    @SerializedName("email")
    private String email;

    @SerializedName("emailVerified")
    private boolean emailVerified;

    @SerializedName("rol")
    private String rol;

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public String getLocalId() {
        return localId;
    }

    public String getEmail() {
        return email;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public String getRol() {
        return rol;
    }
}
