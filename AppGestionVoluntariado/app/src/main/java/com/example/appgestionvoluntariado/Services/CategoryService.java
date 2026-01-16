package com.example.appgestionvoluntariado.Services;

import com.example.appgestionvoluntariado.Models.Interest;
import com.example.appgestionvoluntariado.Models.Need;
import com.example.appgestionvoluntariado.Models.Ods;
import com.example.appgestionvoluntariado.Models.Skill;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CategoryService {
    @GET("categorias/ods")
    Call<List<Ods>> getOds();

    @GET("categorias/habilidades")
    Call<List<Skill>> getSkills();

    @GET("categorias/intereses")
    Call<List<Interest>> getInterests();

    @GET("categorias/necesidades")
    Call<List<Need>> getNeeds();
}
