package com.example.appgestionvoluntariado.Models;

import com.google.gson.annotations.SerializedName;

public class DashboardStats {
    @SerializedName("voluntarios")
    private StatCount volunteers;

    @SerializedName("organizaciones")
    private StatCount organizations;

    @SerializedName("matches")
    private StatCount matches;

    @SerializedName("pendientes_global")
    private int globalPending;

    // Getters
    public StatCount getVolunteers() { return volunteers; }
    public StatCount getOrganizations() { return organizations; }
    public StatCount getMatches() { return matches; }
    public int getGlobalPending() { return globalPending; }

    public static class StatCount {
        @SerializedName("total") private int total;
        @SerializedName("pendientes") private int pending;

        public int getTotal() { return total; }
        public int getPending() { return pending; }
    }
}
