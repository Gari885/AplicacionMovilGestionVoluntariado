package com.example.appgestionvoluntariado.Models;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Organizacion {

    @SerializedName("cif")
    private String cif;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("email")
    private String email;

    // --- NUEVOS CAMPOS DETECTADOS EN EL JSON ---
    @SerializedName("sector")
    private String sector; // Puede ser null, String lo aguanta bien

    @SerializedName("direccion")
    private String direccion;

    @SerializedName("localidad")
    private String localidad;

    @SerializedName("cp")
    private String cp; // Código postal (Mejor String para no perder ceros iniciales)

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("contacto")
    private String contacto;

    @SerializedName("estado")
    private String estado; // "aprobado", "pendiente"...

    // --- CAMBIO IMPORTANTE: Lista de Actividades ---
    // En el JSON, la organización tiene una lista de "actividades".
    // Reutilizamos la clase Voluntariado que creamos antes.
    @SerializedName("actividades")
    private List<Voluntariado> actividades;

    // --- CONSTRUCTOR VACÍO (Obligatorio para Gson/Retrofit) ---
    public Organizacion() {
    }

    // --- GETTERS ---
    public String getCif() { return cif; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getSector() { return sector; }
    public String getDireccion() { return direccion; }
    public String getLocalidad() { return localidad; }
    public String getCp() { return cp; }
    public String getDescripcion() { return descripcion; }
    public String getContacto() { return contacto; }
    public String getEstado() { return estado; }

    // Este getter te devolverá la lista de voluntariados de esa ONG
    public List<Voluntariado> getActividades() { return actividades; }

    // --- SETTERS (Opcionales) ---
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setEmail(String email) { this.email = email; }

    public void setEstado(String nuevoEstado) {
        this.estado = nuevoEstado;
    }
    // ... genera el resto si los necesitas
}
