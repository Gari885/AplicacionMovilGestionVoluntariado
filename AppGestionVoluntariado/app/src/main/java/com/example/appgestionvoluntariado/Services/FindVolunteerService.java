package com.example.appgestionvoluntariado.Services;

import com.example.appgestionvoluntariado.Models.Volunteer;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface FindVolunteerService {
    @GET("voluntarios/email/{email}")
    Call<Volunteer> getVolunteer(@Path("email") String email);

}
