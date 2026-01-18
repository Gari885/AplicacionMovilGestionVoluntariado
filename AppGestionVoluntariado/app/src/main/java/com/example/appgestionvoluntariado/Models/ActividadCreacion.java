package com.example.appgestionvoluntariado.Models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ActividadCreacion {
    @SerializedName("cifOrganizacion") private String cifOrganizacion;
    @SerializedName("nombre") private String nombre;
    @SerializedName("descripcion") private String descripcion;
    @SerializedName("fechaInicio") private String fechaInicio;
    @SerializedName("fechaFin") private String fechaFin;
    @SerializedName("maxParticipantes") private int maxParticipantes;
    @SerializedName("ods") private List<String> ods;

    @SerializedName("direccion") private String direccion;
    @SerializedName("habilidades") private List<String> habilidades;

    public ActividadCreacion() {}

    public ActividadCreacion(String cifOrganizacion, String nombre, String descripcion, String fechaInicio, String fechaFin, int maxParticipantes, List<String> ods) {
        this.cifOrganizacion = cifOrganizacion;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.maxParticipantes = maxParticipantes;
        this.ods = ods;
    }

    public void setCifOrganizacion(String cifOrganizacion) { this.cifOrganizacion = cifOrganizacion; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }
    public void setFechaFin(String fechaFin) { this.fechaFin = fechaFin; }
    public void setMaxParticipantes(int maxParticipantes) { this.maxParticipantes = maxParticipantes; }
    public void setOds(List<String> ods) { this.ods = ods; }
    public void setHabilidades(List<String> habilidades) { this.habilidades = habilidades; }

    public void setMaxParticipants(int maxPart) {
        this.maxParticipantes = maxPart;
    }
}
