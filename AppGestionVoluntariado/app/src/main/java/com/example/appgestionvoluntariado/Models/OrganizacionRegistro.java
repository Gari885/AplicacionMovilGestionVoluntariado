package com.example.appgestionvoluntariado.Models;

import com.google.gson.annotations.SerializedName;

public class OrganizacionRegistro {
    @SerializedName("cif") private String cif;
    @SerializedName("nombre") private String nombre;
    @SerializedName("email") private String email;
    @SerializedName("password") private String password;
    @SerializedName("sector") private String sector;
    @SerializedName("direccion") private String direccion;
    @SerializedName("localidad") private String localidad;
    @SerializedName("cp") private String cp;
    @SerializedName("descripcion") private String descripcion;
    @SerializedName("contacto") private String contacto;

    public OrganizacionRegistro(String cif, String nombre, String email, String password, String sector,
                                String direccion, String localidad, String cp, String descripcion, String contacto) {
        this.cif = cif;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.sector = sector;
        this.direccion = direccion;
        this.localidad = localidad;
        this.cp = cp;
        this.descripcion = descripcion;
        this.contacto = contacto;
    }
}
