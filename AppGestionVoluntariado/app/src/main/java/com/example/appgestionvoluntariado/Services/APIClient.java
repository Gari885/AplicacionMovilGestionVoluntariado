package com.example.appgestionvoluntariado.Services;

import com.example.appgestionvoluntariado.Utils.AuthInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {

    private static final String BASE_URL = "http://10.0.2.2:8000/api/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            // Configuramos OkHttpClient con el nuevo Interceptor
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor())
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client) // Vinculamos el cliente seguro
                    .build();
        }
        return retrofit;
    }

    // --- Public Services ---

    public static ProjectsAPIService getProjectsAPIService(){
        return getClient().create(ProjectsAPIService.class);
    }

    public static OrganizationAPIService getOrganizationAPIService(){
        return getClient().create(OrganizationAPIService.class);
    }

    public static VolunteerService getVolunteerAPIService(){
        return getClient().create(VolunteerService.class);
    }

    public static MatchesAPIService getMatchesAPIService(){
        return getClient().create(MatchesAPIService.class);
    }

    public static FindVolunteerAPIService getFindVolunteerAPIService(){
        return getClient().create(FindVolunteerAPIService.class);
    }

    public static AuthAPIService getAuthAPIService(){
        return getClient().create(AuthAPIService.class);
    }
}