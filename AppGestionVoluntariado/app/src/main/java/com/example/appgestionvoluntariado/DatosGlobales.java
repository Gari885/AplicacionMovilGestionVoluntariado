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

    //Por ahora no igual luego interesa meterlo
    //private final String[] LISTA_ZONAS = {"Pamplona", "Tudela", "Estella", "Burlada", "Tafalla"};
    public final String[] LISTA_IDIOMAS = {
            "Castellano",
            "Euskera",
            "Inglés",
            "Francés",
            "Alemán"
    };
    public final String[] LISTA_EXPERIENCIA = {
            "Sin experiencia",
            "Menos de 1 año",
            "1-3 años",
            "Más de 3 años"
    };
    public final String[] LISTA_COCHE = {
            "Sí",
            "No"
    };
    public final String[] LISTA_CICLOS = {
            "DAM",
            "ASIR",
            "TL",
            "GVEC",
            "CID",
            "AF"
    };

    // Datos para Chips
    public final String[] CHIPS_HABILIDADES = {
            "Programación",
            "Diseño Gráfico",
            "Redes Sociales",
            "Gestión de Eventos",
            "Docencia",
            "Primeros Auxilios",
            "Cocina",
            "Conducción",
            "Idiomas",
            "Música",
    };
    public final String[] CHIPS_INTERESES = {
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



    public static synchronized DatosGlobales getInstance() {
        if (instancia == null) {
            instancia = new DatosGlobales();
        }
        return instancia;
    }
}
