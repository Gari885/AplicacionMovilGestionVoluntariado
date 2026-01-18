package com.example.appgestionvoluntariado.Models;

import com.google.gson.annotations.SerializedName;

public class Cycle {
    @SerializedName("nombre")
    private String name;

    @SerializedName("curso")
    private String grade; // e.g., "1º", "2º"

    public String getName() { return name; }
    public String getGrade() { return grade; }

    @Override
    public String toString() {
        return name + " (" + grade + ")";
    }
}
