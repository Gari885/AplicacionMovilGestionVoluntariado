package com.example.appgestionvoluntariado.Services;

import com.example.appgestionvoluntariado.Models.ProfileResponse;
import com.example.appgestionvoluntariado.Models.VolunteerRegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AuthAPIService {
    @POST("auth/register/voluntario")
    Call<Void> registerVolunteer(@Body VolunteerRegisterRequest request);

    @GET("auth/profile")
    Call<ProfileResponse> getProfile();
}
