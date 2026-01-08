package com.example.appgestionvoluntariado.Models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Volunteer {

    @SerializedName("dni")
    private String dni;

    @SerializedName("nombre")
    private String firstName;

    @SerializedName("apellido1")
    private String lastName1;

    @SerializedName("apellido2")
    private String lastName2;

    @SerializedName("correo")
    private String email;

    @SerializedName("zona")
    private String zone;

    @SerializedName("fechaNacimiento")
    private String birthDate;

    @SerializedName("experiencia")
    private String experience;

    @SerializedName("coche")
    private boolean hasCar;

    @SerializedName("habilidades")
    private String skills;

    @SerializedName("intereses")
    private String interests;

    @SerializedName("idiomas")
    private String languages;

    @SerializedName("estado_voluntario")
    private String status;

    @SerializedName("inscripciones")
    private List<Enrollment> enrollments;

    public Volunteer() {
    }

    public String getDni() { return dni; }
    public String getFirstName() { return firstName; }
    
    public String getFullName() {
        return firstName + " " + lastName1 + " " + (lastName2 != null ? lastName2 : "");
    }

    public String getLastName1() { return lastName1; }
    public String getLastName2() { return lastName2; }
    public String getEmail() { return email; }
    public String getZone() { return zone; }
    public String getBirthDate() { return birthDate; }
    public String getExperience() { return experience; }
    public boolean hasCar() { return hasCar; }

    public String getSkills() { return skills; }
    public String getInterests() { return interests; }
    public String getLanguages() { return languages; }
    public String getStatus() { return status; }
    public List<Enrollment> getEnrollments() { return enrollments; }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class Enrollment {
        @SerializedName("id_inscripcion")
        private int enrollmentId;

        @SerializedName("actividad")
        private String projectTitle; // 'actividad' in JSON usually refers to the project name here

        @SerializedName("estado")
        private String status;

        public int getEnrollmentId() { return enrollmentId; }
        public String getProjectTitle() { return projectTitle; }
        public String getStatus() { return status; }
    }
}
