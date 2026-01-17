package com.example.appgestionvoluntariado.Services;

import com.example.appgestionvoluntariado.Models.ActividadCreacionRequest;
import com.example.appgestionvoluntariado.Models.Project;
import com.example.appgestionvoluntariado.Models.VoluntarioInscribirseRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ActivitiesAPIService {
    @GET("actividades")
    Call<List<Project>> getActivities();
    @POST("actividades/crear")
    Call<Void> crearActividad(@Body ActividadCreacionRequest request);

    @POST("actividades/{codActividad}/inscribir")
    Call<Void> inscribirVoluntarioActividad(
            @Path("codActividad") int codigoActv,
            @Body VoluntarioInscribirseRequest request
    );

}
