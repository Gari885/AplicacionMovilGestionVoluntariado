package com.example.appgestionvoluntariado.Services;

import com.example.appgestionvoluntariado.Models.StatusRequest;
import com.example.appgestionvoluntariado.Models.Volunteer;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface VolunteerService {
    @GET("voluntarios")
    Call<List<Volunteer>> getVolunteers();

    @PATCH("voluntarios/{dni}/estado")
    Call<Volunteer> updateStatus(
            @Path("dni") String dni,       
            @Body StatusRequest request    
    );

    @GET("volunteer/profile")
    Call<Volunteer> getProfile();

    @PUT("volunteer/profile")
    Call<Void> updateProfile(@Body Volunteer volunteer);

}
