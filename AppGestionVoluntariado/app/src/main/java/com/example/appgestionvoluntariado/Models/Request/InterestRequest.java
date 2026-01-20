package com.example.appgestionvoluntariado.Models.Request;

import com.google.gson.annotations.SerializedName;

public class InterestRequest {
    @SerializedName("nombre")
    private String nombre;

    public InterestRequest(String nombre){
        this.nombre = nombre;
    }
}
