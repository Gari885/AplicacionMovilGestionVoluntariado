package com.example.appgestionvoluntariado.Models;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class ProfileResponse {

    @SerializedName("tipo")
    private String type;

    @SerializedName("datos")
    private JsonObject data;

    public String getType(){
        return this.type;
    }
    public JsonObject getData(){
        return this.data;
    }
}
