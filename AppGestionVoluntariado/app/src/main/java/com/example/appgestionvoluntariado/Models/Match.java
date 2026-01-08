package com.example.appgestionvoluntariado.Models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Match implements Serializable {

    @SerializedName("id_inscripcion")
    private int enrollmentId;

    @SerializedName("dni_voluntario")
    private String volunteerDni;

    @SerializedName("nombre_voluntario")
    private String volunteerName;

    @SerializedName("codActividad")
    private int activityCode;

    @SerializedName("nombre_actividad")
    private String activityTitle;

    @SerializedName("estado")
    private String status;

    public Match() {
    }

    public Match(int enrollmentId, String volunteerDni, String volunteerName, int activityCode, String activityTitle, String status) {
        this.enrollmentId = enrollmentId;
        this.volunteerDni = volunteerDni;
        this.volunteerName = volunteerName;
        this.activityCode = activityCode;
        this.activityTitle = activityTitle;
        this.status = status;
    }

    public int getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(int enrollmentId) { this.enrollmentId = enrollmentId; }

    public String getVolunteerDni() { return volunteerDni; }
    public void setVolunteerDni(String volunteerDni) { this.volunteerDni = volunteerDni; }

    public String getVolunteerName() { return volunteerName; }
    public void setVolunteerName(String volunteerName) { this.volunteerName = volunteerName; }

    public int getActivityCode() { return activityCode; }
    public void setActivityCode(int activityCode) { this.activityCode = activityCode; }

    public String getActivityTitle() { return activityTitle; }
    public void setActivityTitle(String activityTitle) { this.activityTitle = activityTitle; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
