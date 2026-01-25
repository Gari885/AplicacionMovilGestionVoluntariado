package com.example.appgestionvoluntariado.Services;

import com.example.appgestionvoluntariado.Models.Request.OrganizationRegisterRequest;
import com.example.appgestionvoluntariado.Models.Request.PasswordRequest;
import com.example.appgestionvoluntariado.Models.Response.ProfileResponse;
import com.example.appgestionvoluntariado.Models.Request.VolunteerRegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AuthAPIService {
    @POST("auth/register/voluntario")
    Call<Void> registerVolunteer(@Body VolunteerRegisterRequest request);

    @POST("auth/register/organizacion")
    Call<Void> registerOrganization(@Body OrganizationRegisterRequest request);

    @GET("auth/profile")
    Call<ProfileResponse> getProfile();


    @POST("auth/change-password")
    Call<Void> changePassword(@Body PasswordRequest request);
}
