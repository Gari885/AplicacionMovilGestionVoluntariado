package com.example.appgestionvoluntariado.Models;

public class AdminStatsResponse {
    private int volunteersActive;
    private int volunteersPending;
    private int orgsActive;
    private int orgsPending;
    private int projectsActive;
    private int projectsPending;
    private int totalMatches;

    // Getters correspondientes...
    public int getVolunteersActive() { return volunteersActive; }
    public int getVolunteersPending() { return volunteersPending; }
    public int getOrgsActive() { return orgsActive; }
    public int getOrgsPending() { return orgsPending; }
    public int getProjectsActive() { return projectsActive; }
    public int getProjectsPending() { return projectsPending; }
    public int getTotalMatches() { return totalMatches; }
}
