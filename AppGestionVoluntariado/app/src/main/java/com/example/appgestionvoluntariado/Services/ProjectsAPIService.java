package com.example.appgestionvoluntariado.Services;

import com.example.appgestionvoluntariado.Models.ProjectCreationRequest;
import com.example.appgestionvoluntariado.Models.Project;
import com.example.appgestionvoluntariado.Models.VolunteerEnrollRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ProjectsAPIService {
    @GET("actividades")
    Call<List<Project>> getProjects();
    @POST("actividades/crear")
    Call<Void> createProject(@Body ProjectCreationRequest request);

    @POST("actividades/{codActividad}/inscribir")
    Call<Void> enrollVolunteerInProject(
            @Path("codActividad") int projectId,
            @Body VolunteerEnrollRequest request
    );

}
