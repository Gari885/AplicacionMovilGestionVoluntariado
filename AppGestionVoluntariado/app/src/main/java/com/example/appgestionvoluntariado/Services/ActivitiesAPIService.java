package com.example.appgestionvoluntariado.Services;

import com.example.appgestionvoluntariado.Models.ActividadCreacionRequest;
import com.example.appgestionvoluntariado.Models.Voluntariado;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ActivitiesAPIService {
    @GET("actividades")
    Call<List<Voluntariado>> getActivities();
    @POST("actividades/crear")
    Call<Void> crearActividad(@Body ActividadCreacionRequest request);

}
