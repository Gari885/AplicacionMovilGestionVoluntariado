package com.example.appgestionvoluntariado.Models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class Project implements Serializable {

    @SerializedName("codActividad")
    private int activityId;

    @SerializedName("nombre")
    private String name;

    @SerializedName("estado")
    private String status;

    @SerializedName("sector")
    private String sector;

    @SerializedName("estadoAprobacion")
    private String approvalStatus;

    @SerializedName("direccion")
    private String address;

    @SerializedName("fechaInicio")
    private String startDate;

    @SerializedName("fechaFin")
    private String endDate;

    @SerializedName("maxParticipantes")
    private int maxParticipants;

    @SerializedName("cif_organizacion")
    private String organizationVat;

    @SerializedName("nombre_organizacion")
    private String organizationName;

    @SerializedName("ods")
    private List<Ods> odsList;

    public String getSector() {return sector;}

    public void setSector(String sector) {this.sector = sector;}

    @SerializedName("habilidades")
    private List<Skill> skillsList;

    @SerializedName("necesidades")
    private List<Need> needsList;

    public Project() {
    }

    // Getters and Setters (English) [cite: 2026-01-09]
    public int getActivityId() { return activityId; }
    public void setActivityId(int activityId) { this.activityId = activityId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public int getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(int maxParticipants) { this.maxParticipants = maxParticipants; }

    public String getOrganizationVat() { return organizationVat; }
    public void setOrganizationVat(String organizationVat) { this.organizationVat = organizationVat; }

    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }

    public List<Ods> getOdsList() { return odsList; }
    public void setOdsList(List<Ods> odsList) { this.odsList = odsList; }

    public List<Skill> getSkillsList() { return skillsList; }
    public void setSkillsList(List<Skill> skillsList) { this.skillsList = skillsList; }

    public List<Need> getNeedsList() { return needsList; }
    public void setNeedsList(List<Need> needsList) { this.needsList = needsList; }
}