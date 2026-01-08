package com.example.appgestionvoluntariado.Models;

import java.util.List;

public class ProjectCreationRequest {

    private String cifOrganizacion;
    private String nombre;
    private String descripcion;
    private String fechaInicio; 
    private String fechaFin; 
    private int maxParticipantes; 
    private List<String> ods;

    public ProjectCreationRequest() {
    }

    public ProjectCreationRequest(String organizationCif, String title, String description, String startDate, String endDate, int maxParticipants, List<String> ods) {
        this.cifOrganizacion = organizationCif;
        this.nombre = title;
        this.descripcion = description;
        this.fechaInicio = startDate;
        this.fechaFin = endDate;
        this.maxParticipantes = maxParticipants;
        this.ods = ods;
    }

    public String getOrganizationCif() { return cifOrganizacion; }
    public void setOrganizationCif(String organizationCif) { this.cifOrganizacion = organizationCif; }

    public String getTitle() { return nombre; }
    public void setTitle(String title) { this.nombre = title; }

    public String getDescription() { return descripcion; }
    public void setDescription(String description) { this.descripcion = description; }

    public String getStartDate() { return fechaInicio; }
    public void setStartDate(String startDate) { this.fechaInicio = startDate; }

    public String getEndDate() { return fechaFin; }
    public void setEndDate(String endDate) { this.fechaFin = endDate; }

    public int getMaxParticipants() { return maxParticipantes; }
    public void setMaxParticipants(int maxParticipants) { this.maxParticipantes = maxParticipants; }

    public List<String> getOds() { return ods; }
    public void setOds(List<String> ods) { this.ods = ods; }
}
