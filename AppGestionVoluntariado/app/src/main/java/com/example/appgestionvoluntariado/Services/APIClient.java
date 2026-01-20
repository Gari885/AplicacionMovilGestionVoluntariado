package com.example.appgestionvoluntariado.Services;

import androidx.credentials.CredentialManager;

import com.example.appgestionvoluntariado.Utils.AuthInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {

    private static final String BASE_URL = "http://10.0.2.2:8000/api/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor())
                    // INCREASE TIMEOUTS to 30 or 60 seconds
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
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

    public static ProjectsService getProjectsService(){
        return getClient().create(ProjectsService.class);
    }

    public static InscriptionsService getInscriptionService(){
        return getClient().create(InscriptionsService.class);
    }

    public static OrganizationService getOrganizationService(){
        return getClient().create(OrganizationService.class);
    }

    public static VolunteerService getVolunteerService(){
        return getClient().create(VolunteerService.class);
    }

    public static CategoryService getCategoriesService(){
        return getClient().create(CategoryService.class);
    }



    public static AuthAPIService getAuthAPIService(){
        return getClient().create(AuthAPIService.class);
    }

    public static AdminService getAdminService(){
        return getClient().create(AdminService.class);
    }

    public static StatsService getStatsService(){
        return getClient().create(StatsService.class);
    }


}