package com.example.appgestionvoluntariado.Services;

import com.example.appgestionvoluntariado.Models.VoluntarioRegistroRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthentificationAPIService {
    @POST("auth/register/voluntario")
    Call<Void> registrarVoluntario(@Body VoluntarioRegistroRequest request);


}
