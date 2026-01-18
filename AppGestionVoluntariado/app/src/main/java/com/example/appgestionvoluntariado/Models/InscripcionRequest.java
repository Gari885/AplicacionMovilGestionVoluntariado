package com.example.appgestionvoluntariado.Models;

import com.google.gson.annotations.SerializedName;

public class InscripcionRequest {
    @SerializedName("dni_voluntario")
    private String dniVoluntario;

    public InscripcionRequest(String dniVoluntario) {
        this.dniVoluntario = dniVoluntario;
    }
}
