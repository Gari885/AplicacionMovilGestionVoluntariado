package com.example.appgestionvoluntariado.Models.Request;

import com.google.gson.annotations.SerializedName;

public class SkillRequest {
    @SerializedName("nombre")
    private String name;

    public SkillRequest(String name){
        this.name = name;
    }
}
