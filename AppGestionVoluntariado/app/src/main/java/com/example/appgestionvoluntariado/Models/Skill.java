package com.example.appgestionvoluntariado.Models;

import com.google.gson.annotations.SerializedName;

// Common structure for Skill, Interest, and Need [cite: 2026-01-09]
public class Skill {
    @SerializedName("id") private int id;
    @SerializedName("nombre") private String name;
    public int getId() { return id; }
    public String getName() { return name; }
}
