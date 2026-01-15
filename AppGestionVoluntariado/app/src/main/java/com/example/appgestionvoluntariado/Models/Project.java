package com.example.appgestionvoluntariado.Models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Project {

    @SerializedName("codActividad")
    private int id;

    @SerializedName("nombre")
    private String title;

    @SerializedName("descripcion") // Añadido para el popup de detalles
    private String description;

    @SerializedName("estado")
    private String status;

    @SerializedName("direccion")
    private String address;

    @SerializedName("fechaInicio")
    private String startDate;

    @SerializedName("fechaFin")
    private String endDate;

    @SerializedName("maxParticipantes")
    private int maxParticipants;

    @SerializedName("ods")
    private List<String> ods;

    @SerializedName("habilidades")
    private List<String> requiredSkills;

    @SerializedName("nombre_organizacion")
    private String organizationName;

    @SerializedName("cif_organizacion")
    private String organizationCif;

    private boolean isEnrolled = false;

    public Project() {
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getStatus() { return status; }
    public String getAddress() { return address; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public int getMaxParticipants() { return maxParticipants; }
    public List<String> getOds() { return ods; }
    public List<String> getRequiredSkills() { return requiredSkills; }
    public String getOrganizationName() { return organizationName; }
    public String getOrganizationCif() { return organizationCif; }

    public boolean isEnrolled() {
        return isEnrolled;
    }

    public void setEnrolled(boolean enrolled) {
        isEnrolled = enrolled;
    }

    // Método corregido para devolver la descripción real de la actividad
    public String getDescription() {
        return description != null ? description : "Sin descripción disponible.";
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Método helper para mostrar el rango de fechas en la tarjeta
    public String getDateRange() {
        if (startDate != null && endDate != null) {
            return startDate + " - " + endDate;
        }
        return startDate != null ? startDate : "Fecha no definida";
    }
}