package com.example.appgestionvoluntariado.Models.Request;

import com.google.gson.annotations.SerializedName;

public class VoluntarioInscribirseRequest {
    // El servidor espera "dni", no "dniVoluntario"
    @SerializedName("dni")
    private String dniVoluntario;

    public VoluntarioInscribirseRequest(String dniVoluntario) {
        this.dniVoluntario = dniVoluntario;
    }

}
