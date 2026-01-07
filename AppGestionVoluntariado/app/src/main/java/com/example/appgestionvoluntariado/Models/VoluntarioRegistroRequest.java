package com.example.appgestionvoluntariado.Models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class VoluntarioRegistroRequest {

    // --- CAMPOS OBLIGATORIOS (CAMBIADOS PARA COINCIDIR CON EL SERVIDOR) ---

    @SerializedName("nombre") // <--- CAMBIO IMPORTANTE
    private String nombreCompleto;

    @SerializedName("email")  // <--- CAMBIO IMPORTANTE
    private String correo;

    @SerializedName("dni")
    private String dni;

    @SerializedName("password")
    private String password;

    @SerializedName("estado") // <--- AÑADIR ESTO
    private String estado;

    @SerializedName("zona")
    private String zona;

    @SerializedName("ciclo")
    private String ciclo;

    @SerializedName("fechaNacimiento") // OJO: Revisa si tu API espera "fechaNac" o "fechaNacimiento"
    private String fechaNac;

    @SerializedName("experiencia")
    private String experiencia;

    @SerializedName("coche")
    private String coche;

    // --- LISTAS ---
    @SerializedName("idiomas")
    private List<String> idiomas;

    @SerializedName("habilidades")
    private List<String> habilidades;

    @SerializedName("intereses")
    private List<String> intereses;

    @SerializedName("disponibilidad")
    private List<String> disponibilidad;

    // --- CONSTRUCTOR ---
    public VoluntarioRegistroRequest(String nombreCompleto, String dni, String correo, String password,
                                     String zona, String ciclo, String fechaNac, String experiencia,
                                     String coche, List<String> idiomas, List<String> habilidades,
                                     List<String> intereses, List<String> disponibilidad,
                                     String estado) {
        this.nombreCompleto = nombreCompleto;
        this.dni = dni;
        this.correo = correo;
        this.password = password;
        this.zona = zona;
        this.ciclo = ciclo;
        this.fechaNac = fechaNac;
        this.experiencia = experiencia;
        this.coche = coche;
        this.idiomas = idiomas;
        this.habilidades = habilidades;
        this.intereses = intereses;
        this.disponibilidad = disponibilidad;
        this.estado = estado;
    }
}