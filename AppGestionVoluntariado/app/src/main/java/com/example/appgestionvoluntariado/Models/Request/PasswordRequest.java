package com.example.appgestionvoluntariado.Models.Request;

public class PasswordRequest {
    private String oldPassword;
    private String newPassword;

    public PasswordRequest(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }
}
