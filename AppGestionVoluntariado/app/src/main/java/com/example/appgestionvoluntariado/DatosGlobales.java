package com.example.appgestionvoluntariado;

import com.example.appgestionvoluntariado.Models.Organizacion;
import com.example.appgestionvoluntariado.Models.Voluntariado;
import com.example.appgestionvoluntariado.Models.Voluntario;

import java.util.ArrayList;
import java.util.List;

public class DatosGlobales {
    private static DatosGlobales instancia;

    private DatosGlobales() {
    }

    public List<Voluntario> voluntarios = voluntarios = new ArrayList<>();

    public List<Voluntariado> voluntariados = voluntariados = new ArrayList<>();
    public List<Organizacion> organizaciones = organizaciones = new ArrayList<>();

    public static synchronized DatosGlobales getInstance() {
        if (instancia == null) {
            instancia = new DatosGlobales();
        }
        return instancia;
    }
}
