package com.example.appgestionvoluntariado.Services;

import com.example.appgestionvoluntariado.Models.Project;
import com.example.appgestionvoluntariado.Models.Request.ProjectCreationRequest;
import com.example.appgestionvoluntariado.Models.Request.StatusRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProjectsService {

    // ==========================================
    // --- SECCIÓN VOLUNTARIO ---
    // ==========================================

    // Obtener proyectos en los que el voluntario NO está inscrito todavía
    @GET("actividades")
    Call<List<Project>> getAvailableProjects();
    ;

    // Obtener los proyectos donde el voluntario YA está inscrito
    @GET("inscripciones/me")
    Call<List<Project>> getEnrolledProjects(@Query("estado")String status);

    // Inscribirse en un proyecto (El ID del usuario sale del Token)
    @POST("actividades/{codActividad}/inscribir")
    Call<Void> enroll(@Path("codActividad") int projectId);

    // Anular inscripción (El ID del usuario sale del Token)
    // CAMBIO: Usar convención en español para coincidir con el backend [cite: 2026-01-18]
    @DELETE("actividades/{id}/desinscribir")
    Call<Void> unenroll(@Path("id") int projectId);


    // ==========================================
    // --- SECCIÓN ADMINISTRADOR ---
    // ==========================================

    // Listar todos los proyectos pendientes de aprobación


    @PATCH("actividades/{id}/estado")
    Call<Void> changeState(@Path("id") int id, @Body StatusRequest status);


    @POST("actividades/crear")
    Call<Void> createProject(@Body ProjectCreationRequest request);



}