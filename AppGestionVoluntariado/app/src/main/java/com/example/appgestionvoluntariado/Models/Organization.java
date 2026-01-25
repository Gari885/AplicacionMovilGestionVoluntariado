package com.example.appgestionvoluntariado.Models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Model representing an Organization.
 * Variables in English, API mapping in Spanish.
 */
public class Organization {

    @SerializedName("cif")
    private String cif;

    @SerializedName("nombre")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("sector")
    private String sector;

    @SerializedName("direccion")
    private String address;

    @SerializedName("localidad")
    private String locality;

    @SerializedName("cp")
    private String postalCode;

    @SerializedName("descripcion")
    private String description;

    @SerializedName("contacto")
    private String contactPhone;

    @SerializedName("estado")
    private String status;

    @SerializedName("actividades")
    private List<Project> projects;

    // Default constructor for GSON
    public Organization() {
    }

    /**
     * Parameterized constructor for profile updates.
     */
    public Organization(String name, String sector, String address, String locality,
                        String postalCode, String description, String contactPhone) {
        this.name = name;
        this.sector = sector;
        this.address = address;
        this.locality = locality;
        this.postalCode = postalCode;
        this.description = description;
        this.contactPhone = contactPhone;
    }

    // Getters and Setters (Matching Fragment logic)
    public String getCif() { return cif; }
    public void setCif(String cif) { this.cif = cif; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getLocality() { return locality; }
    public void setLocality(String locality) { this.locality = locality; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<Project> getProjects() { return projects; }

    public void setProjects(List<Project> projects) { this.projects = projects; }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}