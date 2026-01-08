package com.example.appgestionvoluntariado.Services;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {

    private static final String BASE_URL = "http://10.0.2.2:8000/api/";
    private static Retrofit retrofit = null;

    private static Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client) 
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

    public static VolunteerAPIService getVolunteerAPIService(){
        return getClient().create(VolunteerAPIService.class);
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