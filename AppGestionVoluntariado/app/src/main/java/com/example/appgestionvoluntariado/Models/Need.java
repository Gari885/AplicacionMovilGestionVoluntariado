package com.example.appgestionvoluntariado.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Need implements Serializable {
    @SerializedName("id") private int id;
    @SerializedName("nombre") private String name;
    public int getId() { return id; }
    public String getName() { return name; }
}
