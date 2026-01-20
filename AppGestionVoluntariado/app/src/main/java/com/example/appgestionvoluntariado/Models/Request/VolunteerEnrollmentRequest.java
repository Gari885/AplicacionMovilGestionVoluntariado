package com.example.appgestionvoluntariado.Models.Request;

import com.google.gson.annotations.SerializedName;

public class VolunteerEnrollmentRequest {

    @SerializedName("dni_voluntario")
    String volunteerDNI;

    public VolunteerEnrollmentRequest(String dni) {
        volunteerDNI = dni;
    }
}
