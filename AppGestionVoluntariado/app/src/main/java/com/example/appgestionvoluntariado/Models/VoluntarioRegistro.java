package com.example.appgestionvoluntariado.Models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class VoluntarioRegistro {
    @SerializedName("nombreCompleto") private String nombreCompleto;
    @SerializedName("dni") private String dni;
    @SerializedName("correo") private String correo;
    @SerializedName("password") private String password;
    @SerializedName("zona") private String zona;
    @SerializedName("ciclo") private String ciclo;
    @SerializedName("fechaNacimiento") private String fechaNacimiento;
    @SerializedName("experiencia") private String experiencia;
    @SerializedName("coche") private String coche; // Can be boolean or string in backend, YAML says string example "Si"
    @SerializedName("idiomas") private List<String> idiomas;
    @SerializedName("habilidades") private List<String> habilidades; // IDs/Names during register might be Strings
    @SerializedName("intereses") private List<String> intereses;
    @SerializedName("disponibilidad") private List<String> disponibilidad;

    public VoluntarioRegistro(String nombreCompleto, String dni, String correo, String password, String zona, String ciclo,
                              String fechaNacimiento, String experiencia, String coche, List<String> idiomas,
                              List<String> habilidades, List<String> intereses, List<String> disponibilidad) {
        this.nombreCompleto = nombreCompleto;
        this.dni = dni;
        this.correo = correo;
        this.password = password;
        this.zona = zona;
        this.ciclo = ciclo;
        this.fechaNacimiento = fechaNacimiento;
        this.experiencia = experiencia;
        this.coche = coche;
        this.idiomas = idiomas;
        this.habilidades = habilidades;
        this.intereses = intereses;
        this.disponibilidad = disponibilidad;
    }
}
