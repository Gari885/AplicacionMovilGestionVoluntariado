package com.example.appgestionvoluntariado.Models;

import com.google.gson.annotations.SerializedName;

public class Ods {
    @SerializedName("id")
    private int id;
    @SerializedName("nombre")
    private String name;
    @SerializedName("descripcion")
    private String description;
    @SerializedName("color")
    private String colorHex;

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getColorHex() { return colorHex; }
}