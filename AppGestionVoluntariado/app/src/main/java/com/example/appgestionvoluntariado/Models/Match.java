package com.example.appgestionvoluntariado.Models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Model for activity registration details (Match).
 * Source code is in English, API mapping in Spanish [cite: 2026-01-09, 2026-01-16].
 */
public class Match {

    @SerializedName("id_inscripcion")
    private int registrationId;

    @SerializedName("dni_voluntario")
    private String volunteerDni;

    @SerializedName("nombre_voluntario")
    private String volunteerName;

    @SerializedName("email_voluntario")
    private String volunteerEmail;

    @SerializedName("habilidades_voluntario")
    private List<Skill> volunteerSkills;

    @SerializedName("disponibilidad_voluntario")
    private List<String> volunteerAvailability;

    @SerializedName("intereses_voluntario")
    private List<Interest> volunteerInterests;

    @SerializedName("codActividad")
    private int activityId;

    @SerializedName("nombre_actividad")
    private String activityName;

    @SerializedName("email_organizacion")
    private String organizationEmail;

    @SerializedName("habilidades_actividad")
    private List<Skill> activitySkills;

    @SerializedName("estado")
    private String status;

    public Match() {
    }

    // Getters and Setters
    public int getRegistrationId() { return registrationId; }
    public void setRegistrationId(int registrationId) { this.registrationId = registrationId; }

    public String getVolunteerDni() { return volunteerDni; }
    public void setVolunteerDni(String volunteerDni) { this.volunteerDni = volunteerDni; }

    public String getVolunteerName() { return volunteerName; }
    public void setVolunteerName(String volunteerName) { this.volunteerName = volunteerName; }

    public String getVolunteerEmail() { return volunteerEmail; }
    public void setVolunteerEmail(String volunteerEmail) { this.volunteerEmail = volunteerEmail; }

    public List<Skill> getVolunteerSkills() { return volunteerSkills; }
    public void setVolunteerSkills(List<Skill> volunteerSkills) { this.volunteerSkills = volunteerSkills; }

    public List<String> getVolunteerAvailability() { return volunteerAvailability; }
    public void setVolunteerAvailability(List<String> volunteerAvailability) { this.volunteerAvailability = volunteerAvailability; }

    public List<Interest> getVolunteerInterests() { return volunteerInterests; }
    public void setVolunteerInterests(List<Interest> volunteerInterests) { this.volunteerInterests = volunteerInterests; }

    public int getActivityId() { return activityId; }
    public void setActivityId(int activityId) { this.activityId = activityId; }

    public String getActivityName() { return activityName; }
    public void setActivityName(String activityName) { this.activityName = activityName; }

    public String getOrganizationEmail() { return organizationEmail; }
    public void setOrganizationEmail(String organizationEmail) { this.organizationEmail = organizationEmail; }

    public List<Skill> getActivitySkills() { return activitySkills; }
    public void setActivitySkills(List<Skill> activitySkills) { this.activitySkills = activitySkills; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}