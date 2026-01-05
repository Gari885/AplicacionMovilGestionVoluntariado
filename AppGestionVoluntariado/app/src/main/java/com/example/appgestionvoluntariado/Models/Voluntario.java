package com.example.appgestionvoluntariado.Models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Voluntario {

    @SerializedName("dni")
    private String dni;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("apellido1")
    private String apellido1;

    @SerializedName("apellido2")
    private String apellido2;

    @SerializedName("correo") // El JSON dice "correo", tu clase usaba "email"
    private String email;

    @SerializedName("zona")
    private String zona;

    @SerializedName("fechaNacimiento")
    private String fechaNacimiento;

    @SerializedName("experiencia")
    private String experiencia;

    @SerializedName("coche")
    private boolean coche;

    // OJO: En el JSON vienen como String separada por comas, no como Array
    @SerializedName("habilidades")
    private String habilidades;

    @SerializedName("intereses")
    private String intereses;

    @SerializedName("idiomas")
    private String idiomas;

    @SerializedName("estado_voluntario")
    private String estadoVoluntario; // "ACEPTADO", "RECHAZADO", etc.

    @SerializedName("inscripciones")
    private List<Inscripcion> inscripciones;


    // --- CONSTRUCTOR VACÍO (Obligatorio) ---
    public Voluntario() {
    }

    // --- GETTERS ---

    public String getDni() { return dni; }
    public String getNombre() { return nombre; }

    // Helper para obtener nombre completo
    public String getNombreCompleto() {
        return nombre + " " + apellido1 + " " + (apellido2 != null ? apellido2 : "");
    }

    public String getApellido1() { return apellido1; }
    public String getApellido2() { return apellido2; }
    public String getEmail() { return email; }
    public String getZona() { return zona; }
    public String getFechaNacimiento() { return fechaNacimiento; }
    public String getExperiencia() { return experiencia; }
    public boolean tieneCoche() { return coche; }

    public String getHabilidades() { return habilidades; }
    public String getIntereses() { return intereses; }
    public String getIdiomas() { return idiomas; }
    public String getEstadoVoluntario() { return estadoVoluntario; }
    public List<Inscripcion> getInscripciones() { return inscripciones; }

    public void setEstado(String nuevoEstado) {
        this.estadoVoluntario = nuevoEstado;
    }


    // --- CLASE INTERNA PARA LAS INSCRIPCIONES ---
    public static class Inscripcion {
        @SerializedName("id_inscripcion")
        private int idInscripcion;

        @SerializedName("actividad")
        private String actividad;

        @SerializedName("estado")
        private String estado;

        public int getIdInscripcion() { return idInscripcion; }
        public String getActividad() { return actividad; }
        public String getEstado() { return estado; }
    }
}
