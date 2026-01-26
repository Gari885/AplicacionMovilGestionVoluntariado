package com.example.appgestionvoluntariado.Models.Request;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ProjectCreationRequest {

    // CAMBIO IMPORTANTE: El backend espera 'cifOrganizacion' (sin guion bajo)
    @SerializedName("cifOrganizacion")
    private String organizationCif;

    @SerializedName("nombre")
    private String name;

    @SerializedName("descripcion")
    private String description;


    @SerializedName("sector")
    private String sector;

    @SerializedName("direccion")
    private String address;

    @SerializedName("fechaInicio")
    private String startDate;

    @SerializedName("fechaFin")
    private String endDate;

    @SerializedName("maxParticipantes")
    private int maxParticipants;

    // Asegúrate de enviar IDs numéricos en estas listas (ej: ["1", "3"])
    @SerializedName("ods")
    private List<String> ods;

    @SerializedName("habilidades")
    private List<String> skills;

    @SerializedName("necesidades")
    private List<String> needs;

    public ProjectCreationRequest() {
    }

    public ProjectCreationRequest(String organizationCif, String name, String description,
                                  String address,String sector, String startDate, String endDate,
                                  int maxParticipants, List<String> ods,
                                  List<String> skills, List<String> needs) {
        this.organizationCif = organizationCif;
        this.name = name;
        this.description = description;
        this.address = address;
        this.startDate = startDate;
        this.endDate = endDate;
        this.maxParticipants = maxParticipants;
        this.ods = ods;
        this.skills = skills;
        this.needs = needs;
        this.sector = sector;
    }

    // Getters and Setters
    public String getOrganizationCif() { return organizationCif; }
    public void setOrganizationCif(String organizationCif) { this.organizationCif = organizationCif; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public int getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(int maxParticipants) { this.maxParticipants = maxParticipants; }

    public List<String> getOds() { return ods; }
    public void setOds(List<String> ods) { this.ods = ods; }

    public String getSector() {return sector;}

    public void setSector(String sector) {this.sector = sector;}

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }

    public List<String> getNeeds() { return needs; }
    public void setNeeds(List<String> needs) { this.needs = needs; }
}