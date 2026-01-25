package com.example.appgestionvoluntariado.Services;

import com.example.appgestionvoluntariado.Models.Project;
import com.example.appgestionvoluntariado.Models.Request.InterestRequest;
import com.example.appgestionvoluntariado.Models.Request.SkillRequest;
import com.example.appgestionvoluntariado.Models.Request.VolunteerEnrollmentRequest;
import com.example.appgestionvoluntariado.Models.Volunteer;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AdminService {
    @POST("categories/habilidades")
    Call<Void> addSkill(@Body SkillRequest request);
    @DELETE("categories/habilidades/{id}")
    Call<Void> deleteSkill(@Path("id")int id);

    @POST("categories/intereses")
    Call<Void> addInterest(@Body InterestRequest request);

    @DELETE("categories/intereses/{id}")
    Call<Void> deleteInterest(@Path("id")int id);

    @POST("actividades/{codActividad}/inscribir")
    Call<Void> enrollVolunteer(@Path("codActividad")int id,
                               @Body VolunteerEnrollmentRequest request);

    @GET("voluntarios")
    Call<List<Volunteer>> getVolunteers(@Query("estado") String estado);

    @GET("actividades")
    Call<List<Project>> getProjects(@Query("estadoAprobacion")String estado);


}
