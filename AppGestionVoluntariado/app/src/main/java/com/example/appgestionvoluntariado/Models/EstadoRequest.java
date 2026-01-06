package com.example.appgestionvoluntariado.Models;

import com.google.gson.annotations.SerializedName;

public class EstadoRequest {
    @SerializedName("estado")
    private String estado;

    public EstadoRequest(String estado) {
        this.estado = estado;
    }
}
