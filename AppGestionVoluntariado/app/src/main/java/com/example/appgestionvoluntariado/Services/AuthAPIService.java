package com.example.appgestionvoluntariado.Services;

import com.example.appgestionvoluntariado.Models.OrganizationRegisterRequest;
import com.example.appgestionvoluntariado.Models.PasswordRequest;
import com.example.appgestionvoluntariado.Models.ProfileResponse;
import com.example.appgestionvoluntariado.Models.Volunteer;
import com.example.appgestionvoluntariado.Models.VolunteerRegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

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
