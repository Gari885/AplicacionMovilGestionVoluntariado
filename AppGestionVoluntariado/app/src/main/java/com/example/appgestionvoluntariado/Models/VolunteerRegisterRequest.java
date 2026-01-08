package com.example.appgestionvoluntariado.Models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class VolunteerRegisterRequest {

    @SerializedName("nombre")
    private String fullName;

    @SerializedName("email")
    private String email;

    @SerializedName("dni")
    private String dni;

    @SerializedName("password")
    private String password;

    @SerializedName("estado")
    private String status;

    @SerializedName("zona")
    private String zone;

    @SerializedName("ciclo")
    private String cycle;

    @SerializedName("fechaNacimiento")
    private String birthDate;

    @SerializedName("experiencia")
    private String experience;

    @SerializedName("coche")
    private String hasCar;

    @SerializedName("idiomas")
    private List<String> languages;

    @SerializedName("habilidades")
    private List<String> skills;

    @SerializedName("intereses")
    private List<String> interests;

    @SerializedName("disponibilidad")
    private List<String> availability;

    public VolunteerRegisterRequest(String fullName, String dni, String email, String password,
                                     String zone, String cycle, String birthDate, String experience,
                                     String hasCar, List<String> languages, List<String> skills,
                                     List<String> interests, List<String> availability,
                                     String status) {
        this.fullName = fullName;
        this.dni = dni;
        this.email = email;
        this.password = password;
        this.zone = zone;
        this.cycle = cycle;
        this.birthDate = birthDate;
        this.experience = experience;
        this.hasCar = hasCar;
        this.languages = languages;
        this.skills = skills;
        this.interests = interests;
        this.availability = availability;
        this.status = status;
    }
}
