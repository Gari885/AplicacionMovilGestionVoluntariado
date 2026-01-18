package com.example.appgestionvoluntariado.Models;

import com.google.gson.annotations.SerializedName;

public class InscripcionNueva {
    @SerializedName("dniVoluntario")
    private String dniVoluntario;

    @SerializedName("codActividad")
    private int codActividad;

    public InscripcionNueva(String dniVoluntario, int codActividad) {
        this.dniVoluntario = dniVoluntario;
        this.codActividad = codActividad;
    }
}
