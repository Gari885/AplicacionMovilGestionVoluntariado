package com.example.appgestionvoluntariado.Services;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {

    private static final String BASE_URL = "http://10.0.2.2:8000/api/";
    private static Retrofit retrofit = null;

    // Método privado para configurar Retrofit UNA sola vez con LOGS
    private static Retrofit getClient() {
        if (retrofit == null) {
            // 1. Creamos el interceptor para ver los datos en el Logcat
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            // NIVEL BODY: Muestra todo (Cabeceras + JSON enviado + Respuesta servidor)
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // 2. Añadimos el interceptor al cliente HTTP
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            // 3. Construimos Retrofit con ese cliente
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client) // <--- ¡Importante! Aquí vinculamos los logs
                    .build();
        }
        return retrofit;
    }

    // --- Servicios Públicos (Ahora usan getClient() para no repetir código) ---

    public static ActivitiesAPIService getActivitiesAPIService(){
        return getClient().create(ActivitiesAPIService.class);
    }

    public static OrganizationAPIService getOrganizationAPIService(){
        return getClient().create(OrganizationAPIService.class);
    }

    public static VolunteerAPIService getVolunteerAPIService(){
        return getClient().create(VolunteerAPIService.class);
    }

    public static MatchesAPIService getMatchesAPIService(){ // Corregí el typo "MAtches"
        return getClient().create(MatchesAPIService.class);
    }

    public static FindVolunteerAPIService getfindVolunteerAPIService(){
        return getClient().create(FindVolunteerAPIService.class);
    }

    public static AuthentificationAPIService getAuthenthificationAPIService(){
        return getClient().create(AuthentificationAPIService.class);
    }
}