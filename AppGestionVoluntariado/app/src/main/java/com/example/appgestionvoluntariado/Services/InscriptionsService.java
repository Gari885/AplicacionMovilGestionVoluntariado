package com.example.appgestionvoluntariado.Services;

import com.example.appgestionvoluntariado.Models.InscripcionNueva;
import com.example.appgestionvoluntariado.Models.StatusRequest;
import com.example.appgestionvoluntariado.Models.Volunteer; // For enrollment details inside if reused, or generic objects
import com.example.appgestionvoluntariado.Models.Enrollment; // Using generic if standard list returns this

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface InscriptionsService {
    @GET("inscripciones")
    Call<List<Enrollment>> getPending(); // Or specialized model

    @POST("inscripciones")
    Call<Void> create(@Body InscripcionNueva request);

    @DELETE("inscripciones/{id}")
    Call<Void> cancel(@Path("id") int id);

    @PATCH("inscripciones/{id}/estado")
    Call<Void> updateState(@Path("id") int id, @Body StatusRequest request);

    @GET("inscripciones/me")
    Call<List<Enrollment>> getMyInscriptions(@Query("estado") String estado);

    @GET("inscripciones/voluntario/{dni}/actividades-aceptadas")
    Call<List<Object>> getAcceptedActivities(@Path("dni") String dni, @Query("estado") String estado); // Returns ActividadInscritaDetalle

    @GET("inscripciones/organizacion/{cif}")
    Call<List<Object>> getByOrganization(@Path("cif") String cif, @Query("estado") String estado); // Returns InscripcionOrganizacionDetalle
}
