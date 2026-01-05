package com.example.appgestionvoluntariado.Services;

import com.example.appgestionvoluntariado.Models.Voluntariado;
import com.example.appgestionvoluntariado.Models.Voluntario;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface FindVolunteerAPIService {
    @GET("voluntarios/email/{email}")
    Call<Voluntario> getVoluntario(@Path("email") String email);

}
