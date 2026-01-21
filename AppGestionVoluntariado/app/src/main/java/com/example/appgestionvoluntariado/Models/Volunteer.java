package com.example.appgestionvoluntariado.Models;

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
    @SerializedName("ciclo")
    private String cycle;

    // --- CORRECCIÓN AQUÍ ---
    // Cambiamos List<String> por List<CategoryItem>
    @SerializedName("habilidades")
    private List<CategoryItem> skills;

    @SerializedName("intereses")
    private List<CategoryItem> interests;

    @SerializedName("disponibilidad")
    private List<String> disponibility;
    // -----------------------

    // 'idiomas' se mantiene como String si el backend manda array de textos
    @SerializedName("idiomas")
    private List<String> languages;

    @SerializedName("estado_voluntario")
    private String status;

    @SerializedName("inscripciones")
    private List<Enrollment> enrollments;

    public Volunteer() {
    }

    // --- GETTERS MODIFICADOS ---
    public List<CategoryItem> getSkills() { return skills != null ? skills : new ArrayList<>(); }
    public List<CategoryItem> getInterests() { return interests != null ? interests : new ArrayList<>(); }

    // Método helper si necesitas los nombres como lista de Strings para la UI
    public List<String> getSkillsNames() {
        List<String> names = new ArrayList<>();
        if (skills != null) {
            for (CategoryItem item : skills) {
                names.add(item.nombre);
            }
        }
        return names;
    }

    public List<String> getLanguages() { return languages != null ? languages : new ArrayList<>(); }

    // ... [Resto de getters y setters] ...
    public String getDni() { return dni; }
    public String getFirstName() { return firstName; }

    public String getEmail() { return this.email;}

    public String getStatus(){ return status; }

    public String getZone() { return zone;}

    public String getBirthDate() { return birthDate;}

    public String getCycle() { return cycle; }

    public String getExperience(){ return experience;}

    public Boolean getHasCar(){ return hasCar; }
    public String getFirstLastName(){ return lastName1;}

    public String getSecondLastName(){ return lastName2;}

    public List<String> getDisponibility() { return disponibility; }


    public String getFullName(){
        return firstName + " " + lastName1 + " " + lastName2;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public void setSkills(List<CategoryItem> skills) {
        this.skills = skills;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName1(String lastName1) {
        this.lastName1 = lastName1;
    }

    public void setLastName2(String lastName2) {
        this.lastName2 = lastName2;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public void setHasCar(boolean hasCar) {
        this.hasCar = hasCar;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }

    public void setInterests(List<CategoryItem> interests) {
        this.interests = interests;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setEnrollments(List<Enrollment> enrollments) {
        this.enrollments = enrollments;
    }

    // ...

    // --- NUEVA CLASE PARA HABILIDADES / INTERESES ---
    // Mapea el objeto { "id": 1, "nombre": "..." }
    public static class CategoryItem {
        @SerializedName("id")
        public int id;

        @SerializedName("nombre")
        public String nombre; // Debe coincidir con el campo JSON del backend

        @Override
        public String toString() {
            return nombre; // Para que se vea bonito en Spinners
        }
    }

    // --- CLASE ENROLLMENT (Ya estaba bien) ---
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