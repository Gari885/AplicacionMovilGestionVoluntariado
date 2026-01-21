package com.example.appgestionvoluntariado.Services;

import com.example.appgestionvoluntariado.Models.Request.StatusRequest;
import com.example.appgestionvoluntariado.Models.Volunteer;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface VolunteerService {

    @PATCH("voluntarios/{dni}/estado")
    Call<Void> updateStatus(
            @Path ("dni") String dni,
            @Body StatusRequest request
    );


    @PUT("auth/profile")
    Call<Void> updateProfile(@Body Map<String, Object> data);

}
