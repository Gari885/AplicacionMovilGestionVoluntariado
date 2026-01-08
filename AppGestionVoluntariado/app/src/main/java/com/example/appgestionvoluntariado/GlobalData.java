package com.example.appgestionvoluntariado;

import com.example.appgestionvoluntariado.Models.Match;
import com.example.appgestionvoluntariado.Models.Organization;
import com.example.appgestionvoluntariado.Models.Project;
import com.example.appgestionvoluntariado.Models.Volunteer;

import java.util.ArrayList;
import java.util.List;

public class GlobalData {
    private static GlobalData instance;

    private GlobalData() {
    }

    public List<Volunteer> volunteers = new ArrayList<>();
    public List<Project> projects = new ArrayList<>();
    public List<Organization> organizations = new ArrayList<>();
    public List<Match> matches = new ArrayList<>();


    public final String[] INTERESTS = {
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
    public final String[] SKILLS = {
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

    public final String[] AVAILABILITY = {
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

    public final String[] SECTOR_LIST = {
            "Salud",
            "Educación",
            "Medio Ambiente",
            "Social",
            "Animales",
            "Cultura"
    };
    public final String[] ZONE_LIST = {
            "Pamplona",
            "Tudela",
            "Burlada",
            "Estella",
            "Tafalla"
    };

    public final String[] LANGUAGE_LIST = {
            "Castellano",
            "Euskera",
            "Inglés",
            "Francés",
            "Alemán"
    };
    public final String[] EXPERIENCE_LIST = {
            "Sin experiencia",
            "Menos de 1 año",
            "1-3 años",
            "Más de 3 años"
    };
    public final String[] CAR_LIST = {
            "Sí",
            "No"
    };
    public final String[] CYCLE_LIST = {
            "DAM",
            "ASIR",
            "TL",
            "GVEC",
            "CID",
            "AF"
    };

    // Data for Chips
    public final String[] SKILL_CHIPS = {
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
    public final String[] INTEREST_CHIPS = {
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

    public static synchronized GlobalData getInstance() {
        if (instance == null) {
            instance = new GlobalData();
        }
        return instance;
    }
}
