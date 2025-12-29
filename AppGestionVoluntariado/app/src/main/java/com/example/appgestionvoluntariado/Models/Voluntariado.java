package com.example.appgestionvoluntariado.Models;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Voluntariado {

    // 1. ID de la actividad
    @SerializedName("codActividad")
    private int id;

    // 2. Título o Nombre (En el JSON es "nombre")
    @SerializedName("nombre")
    private String titulo;

    // 3. Estado (Nuevo campo: "En Curso", etc.)
    @SerializedName("estado")
    private String estado;

    // 4. Dirección (En tu clase antigua era "zona")
    @SerializedName("direccion")
    private String direccion;

    // 5. Fechas
    @SerializedName("fechaInicio")
    private String fechaInicio;

    @SerializedName("fechaFin")
    private String fechaFin;

    // 6. Participantes
    @SerializedName("maxParticipantes")
    private int maxParticipantes;

    // 7. Listas (ODS y Habilidades)
    // Usamos List en lugar de ArrayList para ser más flexibles
    @SerializedName("ods")
    private List<String> ods;

    @SerializedName("habilidades")
    private List<String> necesidades; // Antes lo llamabas necesidades, el JSON lo llama habilidades

    // 8. Organización (Nuevos campos)
    @SerializedName("nombre_organizacion")
    private String nombreOrganizacion;

    @SerializedName("cif_organizacion")
    private String cifOrganizacion;

    // --- CONSTRUCTOR VACÍO (Necesario para Retrofit/Gson) ---
    public Voluntariado() {
    }

    // --- GETTERS (Para acceder a los datos) ---

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

    // --- SETTERS (Opcionales, pero recomendables) ---
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    // ... puedes generar el resto automáticamente en Android Studio (Click derecho > Generate > Getter and Setter)
}
