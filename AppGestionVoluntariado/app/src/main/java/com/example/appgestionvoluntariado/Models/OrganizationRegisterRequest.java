package com.example.appgestionvoluntariado.Models;

import com.google.gson.annotations.SerializedName;

public class OrganizationRegisterRequest {
    @SerializedName("cif")
    private String cif;
    @SerializedName("nombre")
    private String name;
    @SerializedName("email")
    private String email;
    @SerializedName("password")
    private String password;
    @SerializedName("contacto")
    private String phone;
    @SerializedName("direccion")
    private String address;
    @SerializedName("localidad")
    private String locality;
    @SerializedName("cp")
    private String postalCode;
    @SerializedName("descripcion")
    private String description;

    @SerializedName("sector")
    private String sector;

    public OrganizationRegisterRequest(String cif, String name, String email, String password, String phone, String address, String locality, String postalCode, String description, String sector) {
        this.cif = cif;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.locality = locality;
        this.postalCode = postalCode;
        this.description = description;
        this.sector = sector;
    }

    // Getters and Setters
    public String getCif() { return cif; }
    public void setCif(String cif) { this.cif = cif; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getLocality() { return locality; }
    public void setLocality(String locality) { this.locality = locality; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
