package com.example.appgestionvoluntariado.Models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Project {

    @SerializedName("codActividad")
    private int id;

    @SerializedName("nombre")
    private String title;

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

    public String getDescription() {
        return "Organizado por: " + organizationName;
    }
}
