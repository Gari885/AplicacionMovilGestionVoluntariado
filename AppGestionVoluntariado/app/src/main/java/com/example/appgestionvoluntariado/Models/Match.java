package com.example.appgestionvoluntariado.Models;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Match implements Serializable {

    @SerializedName("id_inscripcion")
    private int idInscripcion;

    @SerializedName("dni_voluntario")
    private String dniVoluntario;

    @SerializedName("nombre_voluntario")
    private String nombreVoluntario;

    // Fíjate que en tu JSON este campo viene en camelCase, así que el name debe coincidir
    @SerializedName("codActividad")
    private int codActividad;

    @SerializedName("nombre_actividad")
    private String nombreActividad;

    @SerializedName("estado")
    private String estado;

    // Constructor vacío (necesario para muchas librerías de JSON)
    public Match() {
    }

    // Constructor completo
    public Match(int idInscripcion, String dniVoluntario, String nombreVoluntario, int codActividad, String nombreActividad, String estado) {
        this.idInscripcion = idInscripcion;
        this.dniVoluntario = dniVoluntario;
        this.nombreVoluntario = nombreVoluntario;
        this.codActividad = codActividad;
        this.nombreActividad = nombreActividad;
        this.estado = estado;
    }

    // Getters y Setters
    public int getIdInscripcion() {
        return idInscripcion;
    }

    public void setIdInscripcion(int idInscripcion) {
        this.idInscripcion = idInscripcion;
    }

    public String getDniVoluntario() {
        return dniVoluntario;
    }

    public void setDniVoluntario(String dniVoluntario) {
        this.dniVoluntario = dniVoluntario;
    }

    public String getNombreVoluntario() {
        return nombreVoluntario;
    }

    public void setNombreVoluntario(String nombreVoluntario) {
        this.nombreVoluntario = nombreVoluntario;
    }

    public int getCodActividad() {
        return codActividad;
    }

    public void setCodActividad(int codActividad) {
        this.codActividad = codActividad;
    }

    public String getNombreActividad() {
        return nombreActividad;
    }

    public void setNombreActividad(String nombreActividad) {
        this.nombreActividad = nombreActividad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
