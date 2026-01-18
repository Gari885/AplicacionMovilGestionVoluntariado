package com.example.appgestionvoluntariado.Models;
import com.example.appgestionvoluntariado.Models.Ods;
import com.example.appgestionvoluntariado.Models.Skill;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ActividadInscritaDetalle {
    @SerializedName("id_inscripcion") private int idInscripcion;
    @SerializedName("codActividad") private int codActividad;
    @SerializedName("nombre") private String nombre;
    @SerializedName("direccion") private String direccion;
    @SerializedName("fechaInicio") private String fechaInicio;
    @SerializedName("fechaFin") private String fechaFin;
    @SerializedName("organizacion") private String organizacion;
    @SerializedName("estado_actividad") private String estadoActividad;
    @SerializedName("estado_inscripcion") private String estadoInscripcion;
    @SerializedName("ods") private List<Ods> ods;
    @SerializedName("habilidades") private List<Skill> habilidades;

    public int getIdInscripcion() { return idInscripcion; }
    public int getCodActividad() { return codActividad; }
    public String getNombre() { return nombre; }
    public String getDireccion() { return direccion; }
    public String getFechaInicio() { return fechaInicio; }
    public String getFechaFin() { return fechaFin; }
    public String getOrganizacion() { return organizacion; }
    public String getEstadoActividad() { return estadoActividad; }
    public String getEstadoInscripcion() { return estadoInscripcion; }
}
