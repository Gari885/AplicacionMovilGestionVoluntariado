package com.example.appgestionvoluntariado.Models;

import com.google.gson.annotations.SerializedName;

public class VolunteerEnrollRequest {
    @SerializedName("dni")
    private String volunteerDni;

    public VolunteerEnrollRequest(String volunteerDni) {
        this.volunteerDni = volunteerDni;
    }
}
