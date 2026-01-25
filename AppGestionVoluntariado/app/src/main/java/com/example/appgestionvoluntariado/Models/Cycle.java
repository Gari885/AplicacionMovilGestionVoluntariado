package com.example.appgestionvoluntariado.Models;

import com.google.gson.annotations.SerializedName;

public class Cycle {

    @SerializedName("nombre")
    private String name;

    @SerializedName("curso")
    private String course;

    public Cycle(String name, String course){
        this.name = name;
        this.course = course;
    }

    public String getFullCycle(){
        return name + " " + course;
    }

    @Override
    public String toString() {
        return name;
    }
}
