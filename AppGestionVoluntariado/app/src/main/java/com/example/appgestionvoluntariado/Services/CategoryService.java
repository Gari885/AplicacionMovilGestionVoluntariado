package com.example.appgestionvoluntariado.Services;

import com.example.appgestionvoluntariado.Models.Cycle;
import com.example.appgestionvoluntariado.Models.Interest;
import com.example.appgestionvoluntariado.Models.Need;
import com.example.appgestionvoluntariado.Models.Ods;
import com.example.appgestionvoluntariado.Models.Skill;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CategoryService {
    @GET("categories/ods")
    Call<List<Ods>> getOds();

    @GET("categories/habilidades")
    Call<List<Skill>> getSkills();

    @GET("categories/intereses")
    Call<List<Interest>> getInterests();

    @GET("categories/necesidades")
    Call<List<Need>> getNeeds();

    @GET("ciclos")
    Call<List<Cycle>> getCycles();
}
