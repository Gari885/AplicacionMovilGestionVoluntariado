package com.example.appgestionvoluntariado.Models;

import java.util.List;


public class ActividadCreacionRequest {

    // Nombres exactos del Swagger
    private String cifOrganizacion;
    private String nombre;
    private String descripcion;
    private String fechaInicio; // Formato ISO: "2025-12-25T10:00:00"
    private String fechaFin; // Formato ISO: "2025-12-25T10:00:00"

    private int maxParticipantes; // El nuevo campo integer
    private List<String> ods;

    // Constructor vacío
    public ActividadCreacionRequest() {
    }

    // Constructor completo
    public ActividadCreacionRequest(String cifOrganizacion, String nombre, String descripcion, String fechaInicio,String fechaFin, int maxParticipantes, List<String> ods) {
        this.cifOrganizacion = cifOrganizacion;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.maxParticipantes = maxParticipantes;
        this.ods = ods;
    }

    // Getters y Setters
    public String getCifOrganizacion() { return cifOrganizacion; }
    public void setCifOrganizacion(String cifOrganizacion) { this.cifOrganizacion = cifOrganizacion; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }

    public int getMaxParticipantes() { return maxParticipantes; }
    public void setMaxParticipantes(int maxParticipantes) { this.maxParticipantes = maxParticipantes; }

    public List<String> getOds() { return ods; }
    public void setOds(List<String> ods) { this.ods = ods; }
}
