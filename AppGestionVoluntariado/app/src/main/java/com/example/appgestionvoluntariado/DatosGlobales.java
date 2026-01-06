package com.example.appgestionvoluntariado;

import com.example.appgestionvoluntariado.Models.Match;
import com.example.appgestionvoluntariado.Models.Organizacion;
import com.example.appgestionvoluntariado.Models.Voluntariado;
import com.example.appgestionvoluntariado.Models.Voluntario;

import java.util.ArrayList;
import java.util.List;

public class DatosGlobales {
    private static DatosGlobales instancia;

    private DatosGlobales() {
    }

    public List<Voluntario> voluntarios = new ArrayList<>();

    public List<Voluntariado> voluntariados = new ArrayList<>();
    public List<Organizacion> organizaciones = new ArrayList<>();

    public List<Match> matches = new ArrayList<>();



    public final String[] INTERESES = {
            "Medio Ambiente",
            "Educación",
            "Salud",
            "Animales",
            "Cultura",
            "Deporte",
            "Tecnología",
            "Derechos Humanos",
            "Mayores",
            "Infancia",

    };
    public final String[] HABILIDADES = {
            "Programación",
            "Diseño Gráfico",
            "Redes Sociales",
            "Gestión de Eventos",
            "Docencia",
            "Primeros Auxilios",
            "Cocina",
            "Conducción",
            "Idiomas",
            "Música"
    };

    public final String[] DISPONIBILIDAD = {
            "Lunes Mañana",
            "Lunes Tarde",
            "Martes Mañana",
            "Martes Tarde",
            "Miércoles Mañana",
            "Miércoles Tarde",
            "Jueves Mañana",
            "Jueves Tarde",
            "Viernes Mañana",
            "Viernes Tarde",
            "Fines de Semana"
    };

    public final String[] LISTA_SECTORES = {
            "Salud",
            "Educación",
            "Medio Ambiente",
            "Social",
            "Animales",
            "Cultura"
    };
    public final String[] LISTA_ZONAS = {
            "Pamplona",
            "Tudela",
            "Burlada",
            "Estella",
            "Tafalla"
    };




    public static synchronized DatosGlobales getInstance() {
        if (instancia == null) {
            instancia = new DatosGlobales();
        }
        return instancia;
    }
}
