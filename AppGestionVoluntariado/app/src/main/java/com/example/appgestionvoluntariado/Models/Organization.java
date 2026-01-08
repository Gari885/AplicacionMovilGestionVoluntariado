package com.example.appgestionvoluntariado.Models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

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
    private String city;

    @SerializedName("cp")
    private String zipCode;

    @SerializedName("descripcion")
    private String description;

    @SerializedName("contacto")
    private String contactPerson;

    @SerializedName("estado")
    private String status;

    @SerializedName("actividades")
    private List<Project> projects;

    public Organization() {
    }

    public String getCif() { return cif; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getSector() { return sector; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getZipCode() { return zipCode; }
    public String getDescription() { return description; }
    public String getContactPerson() { return contactPerson; }
    public String getStatus() { return status; }
    public List<Project> getProjects() { return projects; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setStatus(String status) { this.status = status; }
}
