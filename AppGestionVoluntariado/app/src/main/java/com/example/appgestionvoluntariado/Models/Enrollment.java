package com.example.appgestionvoluntariado.Models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Enrollment {
    @SerializedName("id_inscripcion")
    private int enrollmentId;

    @SerializedName("actividad")
    private String projectTitle;

    @SerializedName("estado")
    private String status;
    
    // Additional fields from YAML components/schemas/InscripcionDetalle if needed globally
    // But specific "Inscripcion" schema has more fields.
    // Let's check "InscripcionDetalle" in YAML:
    // id_inscripcion, dni_voluntario, nombre_voluntario..., codActividad, nombre_actividad...
    
    // This simple version seems to match "Inscripciones" list inside VoluntarioDetalle
    // which has: id_inscripcion, actividad, estado.
    
    public int getEnrollmentId() { return enrollmentId; }
    public String getProjectTitle() { return projectTitle; }
    public String getStatus() { return status; }
}
