package com.example.appgestionvoluntariado.Utils;

/**
 * Singleton class to manage static lists and global application data.
 * Variable names are in English, but values are in Spanish for the UI [cite: 2026-01-16].
 */
public class GlobalData {

    private static GlobalData instance;

    // Static lists for dropdowns and chips - Values in Spanish for the App
    public final String[] EXPERIENCE_LIST = {
            "Ninguna",
            "Menos de 1 año",
            "Entre 1 y 3 años",
            "Más de 3 años"
    };

    public final String[] CAR_LIST = {"Sí", "No"};

    public final String[] CYCLE_LIST = {
            "SMR", "ASIR", "DAM", "DAW", "Marketing", "Administración", "Otros"
    };

    public final String[] LANGUAGE_LIST = {
            "Castellano", "Inglés", "Francés", "Euskera", "Alemán", "Otros"
    };

    public final String[] DAYS_LIST = {
            "Lunes", "Martes", "Miércoles", "Jueves", "Viernes",
            "Sábado", "Domingo", "Lunes a Viernes", "Fines de semana"
    };

    public final String[] TIME_SLOTS_LIST = {
            "Mañana", "Tarde", "Noche"
    };


    public final String[] ZONES_LIST = {
            "Casco Viejo",
            "Ensanche",
            "Iturrama",
            "San Juan / Donibane",
            "Mendebaldea / Ermitagaña",
            "Milagrosa / Arrosadia",
            "Azpilagaña",
            "Chantrea / Txantrea",
            "Rochapea / Arrotxapea",
            "San Jorge / Sanduzelai",
            "Buztintxuri",
            "Mendillorri",
            "Lezkairu",
            "Erripagaña",
            "Ansoáin / Antsoain",
            "Barañáin",
            "Burlada / Burlata",
            "Villava / Atarrabia",
            "Zizur Mayor / Zizur Nagusia",
            "Mutilva / Mutiloa",
            "Sarriguren"
    };

    // Private constructor to prevent direct instantiation
    private GlobalData() { }

    /**
     * Thread-safe Singleton instance retriever.
     * @return The single instance of GlobalData.
     */
    public static synchronized GlobalData getInstance() {
        if (instance == null) {
            instance = new GlobalData();
        }
        return instance;
    }
}