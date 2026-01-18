package com.example.appgestionvoluntariado.Models;

import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
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

    @SerializedName("ciclo") // Añadido para coincidir con el registro
    private String cycle;

    @SerializedName("habilidades")
    private List<String> skills;

    @SerializedName("intereses")
    private List<String> interests;

    @SerializedName("idiomas")
    private List<String> languages;

    @SerializedName("estado_voluntario")
    private String status;

    @SerializedName("inscripciones")
    private List<Enrollment> enrollments;

    public Volunteer() {
    }

    // --- GETTERS ---
    public String getDni() { return dni; }
    public String getFirstName() { return firstName; }
    public String getLastName1() { return lastName1; }
    public String getLastName2() { return lastName2; }
    public String getEmail() { return email; }
    public String getZone() { return zone; }
    public String getBirthDate() { return birthDate; }
    public String getExperience() { return experience; }
    public boolean getHasCar() { return hasCar; }
    public String getCycle() { return cycle; }
    public List<String> getSkills() { return skills != null ? skills : new ArrayList<>(); }
    public List<String> getInterests() { return interests != null ? interests : new ArrayList<>(); }
    public List<String> getLanguages() { return languages != null ? languages : new ArrayList<>(); }
    public String getStatus() { return status; }
    public List<Enrollment> getEnrollments() { return enrollments; }

    // Método helper para la UI
    public String getFullName() {
        return firstName + " " + (lastName1 != null ? lastName1 : "") + " " + (lastName2 != null ? lastName2 : "");
    }

    // --- SETTERS (Necesarios para la edición) ---
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName1(String lastName1) { this.lastName1 = lastName1; }
    public void setLastName2(String lastName2) { this.lastName2 = lastName2; }
    public void setZone(String zone) { this.zone = zone; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }
    public void setExperience(String experience) { this.experience = experience; }
    public void setHasCar(boolean hasCar) { this.hasCar = hasCar; }
    public void setCycle(String cycle) { this.cycle = cycle; }
    public void setLanguages(List<String> languages) { this.languages = languages; }
    public void setStatus(String status) { this.status = status; }

    public void setSkills(List<String> selectedSkills) {
        this.skills = selectedSkills;
    }

    public void setInterests(List<String> selectedInterests) {
        this.interests = selectedInterests;
    }

    // --- CLASE INTERNA ENROLLMENT ---
    public static class Enrollment {
        @SerializedName("id_inscripcion")
        private int enrollmentId;

        @SerializedName("actividad")
        private String projectTitle;

        @SerializedName("estado")
        private String status;

        public int getEnrollmentId() { return enrollmentId; }
        public String getProjectTitle() { return projectTitle; }
        public String getStatus() { return status; }
    }
}