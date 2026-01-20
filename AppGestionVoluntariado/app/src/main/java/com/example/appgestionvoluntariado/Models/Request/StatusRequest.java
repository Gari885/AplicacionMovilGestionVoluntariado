package com.example.appgestionvoluntariado.Models.Request;

import com.google.gson.annotations.SerializedName;

public class StatusRequest {
    @SerializedName("estado")
    private String status;

    public StatusRequest(String status) {
        this.status = status;
    }

    public String getStatus() { return status; }
}
