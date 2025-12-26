package com.example.appgestionvoluntariado.Services;

import com.example.appgestionvoluntariado.Models.Voluntariado;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ActivitiesAPIService {
    @GET("actividades")
    Call<List<Voluntariado>> getActivities();
}
