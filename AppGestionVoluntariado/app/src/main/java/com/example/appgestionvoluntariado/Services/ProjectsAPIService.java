package com.example.appgestionvoluntariado.Services;

import com.example.appgestionvoluntariado.Models.Project;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ProjectsAPIService {

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
    @GET("admin/projects/pending")
    Call<List<Project>> getPendingProjects();

    @PATCH("projects/{id}/approve")
    Call<Void> approveProject(@Path("id") int id);

    @PATCH("projects/{id}/reject")
    Call<Void> rejectProject(@Path("id") int id);


    // ==========================================
    // --- SECCIÓN ORGANIZADOR ---
    // ==========================================

    // Listar solo los proyectos creados por esta organización
    @GET("organizer/my-projects")
    Call<List<Project>> getMyCreatedProjects();

    // Crear una nueva oferta de voluntariado
    @POST("projects")
    Call<Void> createProject(@Body Project project);

    @PUT("projects/{id}")
    Call<Void> updateProject(@Path("id") int id, @Body Project project);

    @DELETE("projects/{id}")
    Call<Void> deleteProject(@Path("id") int id);
}