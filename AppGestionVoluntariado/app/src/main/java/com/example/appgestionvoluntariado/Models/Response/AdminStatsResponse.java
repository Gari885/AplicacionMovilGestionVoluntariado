package com.example.appgestionvoluntariado.Models.Response;

import com.google.gson.annotations.SerializedName;

public class AdminStatsResponse {

    // --- Mapeo directo del JSON ---

    @SerializedName("voluntarios")
    private StatsDetail voluntarios;

    @SerializedName("organizaciones")
    private StatsDetail organizaciones;

    @SerializedName("actividades")
    private StatsDetail actividades; // Nuevo campo que acabamos de añadir al backend

    @SerializedName("matches")
    private StatsSimple matches;

    @SerializedName("pendientes_global")
    private int totalPendientes;

    // --- Getters de Acceso Rápido (Flattening) ---

    // Voluntarios
    public int getVolunteersActive() { return voluntarios != null ? voluntarios.total : 0; }
    public int getVolunteersPending() { return voluntarios != null ? voluntarios.pendientes : 0; }

    // Organizaciones
    public int getOrgsActive() { return organizaciones != null ? organizaciones.total : 0; }
    public int getOrgsPending() { return organizaciones != null ? organizaciones.pendientes : 0; }

    // Proyectos (Actividades)
    public int getProjectsActive() { return actividades != null ? actividades.total : 0; }
    public int getProjectsPending() { return actividades != null ? actividades.pendientes : 0; }

    // Matches
    public int getTotalMatches() { return matches != null ? matches.total : 0; }

    // Pendientes Globales
    public int getTotalPendientesGlobal() { return totalPendientes; }

    // --- Clases Internas ---

    public static class StatsDetail {
        @SerializedName("total")
        public int total;

        @SerializedName("pendientes")
        public int pendientes;
    }

    public static class StatsSimple {
        @SerializedName("total")
        public int total;
    }
}