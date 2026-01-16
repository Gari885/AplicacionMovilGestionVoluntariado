package com.example.appgestionvoluntariado.Services;

import com.example.appgestionvoluntariado.Models.Project;
import com.example.appgestionvoluntariado.Models.ProjectCreationRequest;
import com.example.appgestionvoluntariado.Models.StatusRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProjectsService {

    // ==========================================
    // --- SECCIÓN VOLUNTARIO ---
    // ==========================================

    // Obtener proyectos en los que el voluntario NO está inscrito todavía
    @GET("projects/available")
    Call<List<Project>> getAvailableProjects();

    // Obtener los proyectos donde el voluntario YA está inscrito
    @GET("projects/enrolled")
    Call<List<Project>> getEnrolledProjects();

    // Inscribirse en un proyecto (El ID del usuario sale del Token)
    @POST("projects/{id}/enroll")
    Call<Void> enroll(@Path("id") int projectId);

    // Anular inscripción (El ID del usuario sale del Token)
    @DELETE("projects/{id}/unenroll")
    Call<Void> unenroll(@Path("id") int projectId);


    // ==========================================
    // --- SECCIÓN ADMINISTRADOR ---
    // ==========================================

    // Listar todos los proyectos pendientes de aprobación
    @GET("actividades")
    Call<List<Project>> getProjects(@Query("estado")String estado);

    @PATCH("/actividades/{id}/estado")
    Call<Void> changeState(@Path("id") int id, StatusRequest status);


    // ==========================================
    // --- SECCIÓN ORGANIZADOR ---
    // ==========================================

    // Listar solo los proyectos creados por esta organización
    @GET("organizer/my-projects")
    Call<List<Project>> getMyCreatedProjects();

    // Crear una nueva oferta de voluntariado
    @POST("projects")
    Call<Void> createProject(@Body ProjectCreationRequest request);

    @PUT("projects/{id}")
    Call<Void> updateProject(@Path("id") int id, @Body Project project);

    @DELETE("projects/{id}")
    Call<Void> deleteProject(@Path("id") int id);

}