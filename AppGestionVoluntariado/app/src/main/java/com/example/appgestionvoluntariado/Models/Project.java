package com.example.appgestionvoluntariado.Models;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class Project {

    @SerializedName("codActividad")
    private int id;

    @SerializedName("nombre")
    private String title;

    @SerializedName("estado")
    private String status;

    @SerializedName("estadoAprobacion")
    private String approvalStatus; // "pendiente", "aprobado", "rechazado"

    @SerializedName("direccion")
    private String address;

    @SerializedName("fechaInicio")
    private String startDate;

    @SerializedName("fechaFin")
    private String endDate;

    @SerializedName("maxParticipantes")
    private int maxParticipants;

    @SerializedName("cif_organizacion")
    private String organizationCif;

    @SerializedName("nombre_organizacion")
    private String organizationName;

    // Cambiados a listas de objetos según tu esquema
    @SerializedName("ods")
    private List<TagItem> ods;

    @SerializedName("habilidades")
    private List<TagItem> skills;

    @SerializedName("necesidades")
    private List<TagItem> needs;

    // Campo local para descripción (aunque no venga en el esquema, lo mantenemos para el popup)
    @SerializedName("descripcion")
    private String description;

    private boolean isEnrolled = false;

    // --- CLASE INTERNA PARA MAPEAR OBJETOS DEL ARRAY ---
    public static class TagItem {
        @SerializedName("nombre")
        private String name;
        public String getName() { return name; }
    }

    public Project() {
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getStatus() { return status; }
    public String getApprovalStatus() { return approvalStatus; }
    public String getAddress() { return address; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public int getMaxParticipants() { return maxParticipants; }
    public String getOrganizationCif() { return organizationCif; }
    public String getOrganizationName() { return organizationName; }
    public String getDescription() { return description != null ? description : "Sin descripción disponible."; }
    public boolean isEnrolled() { return isEnrolled; }

    // Helpers para obtener solo los Strings de los nombres
    public List<String> getOdsNames() {
        List<String> names = new ArrayList<>();
        if (ods != null) for (TagItem item : ods) names.add(item.getName());
        return names;
    }

    public List<String> getSkillNames() {
        List<String> names = new ArrayList<>();
        if (skills != null) for (TagItem item : skills) names.add(item.getName());
        return names;
    }

    public List<String> getNeedNames() {
        List<String> names = new ArrayList<>();
        if (needs != null) for (TagItem item : needs) names.add(item.getName());
        return names;
    }

    // Setters
    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }
    public void setEnrolled(boolean enrolled) { isEnrolled = enrolled; }

}