package com.example.appgestionvoluntariado.Models;

import com.google.gson.annotations.SerializedName;

public class Interest {
    @SerializedName("id") private int id;
    @SerializedName("nombre") private String name;
    public int getId() { return id; }
    public String getName() { return name; }
}
