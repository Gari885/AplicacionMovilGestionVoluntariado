package com.example.appgestionvoluntariado.Models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Voluntariado {

    // --- CAMPOS QUE VIENEN DEL JSON (API) ---

    @SerializedName("codActividad")
    private int id;

    @SerializedName("nombre")
    private String titulo;

    @SerializedName("estado")
    private String estado;

    @SerializedName("direccion")
    private String direccion;

    @SerializedName("fechaInicio")
    private String fechaInicio;

    @SerializedName("fechaFin")
    private String fechaFin;

    @SerializedName("maxParticipantes")
    private int maxParticipantes;

    // Listas
    @SerializedName("ods")
    private List<String> ods;

    @SerializedName("habilidades")
    private List<String> necesidades;

    // Organización
    @SerializedName("nombre_organizacion")
    private String nombreOrganizacion;

    @SerializedName("cif_organizacion")
    private String cifOrganizacion;

    // --- CAMPO NUEVO (NO VIENE DEL JSON) ---
    // Este campo lo rellenaremos nosotros manualmente tras cruzar datos con inscripciones
    private boolean inscrito = false; // Por defecto false

    // --- CONSTRUCTOR VACÍO ---
    public Voluntariado() {
    }

    // --- GETTERS ---

    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getEstado() { return estado; }
    public String getDireccion() { return direccion; }
    public String getFechaInicio() { return fechaInicio; }
    public String getFechaFin() { return fechaFin; }
    public int getMaxParticipantes() { return maxParticipantes; }
    public List<String> getOds() { return ods; }
    public List<String> getNecesidades() { return necesidades; }
    public String getNombreOrganizacion() { return nombreOrganizacion; }
    public String getCifOrganizacion() { return cifOrganizacion; }

    // --- GETTER Y SETTER PARA 'INSCRITO' ---

    // El Adapter llamará a esto para saber si pintar el botón Rojo o Verde
    public boolean isInscrito() {
        return inscrito;
    }

    // Tu Fragment llamará a esto cuando cruce los datos
    public void setInscrito(boolean inscrito) {
        this.inscrito = inscrito;
    }

    // --- EXTRA: Método getDescripcion ---
    // En tu Adapter usas .getDescripcion(), pero en el JSON no viene descripción.
    // He añadido esto para que no te de error el código, devolviendo el nombre de la ONG.
    public String getDescripcion() {
        return "Organizado por: " + nombreOrganizacion;
        // O si tienes un campo descripción en BD, añádelo arriba con @SerializedName
    }
}