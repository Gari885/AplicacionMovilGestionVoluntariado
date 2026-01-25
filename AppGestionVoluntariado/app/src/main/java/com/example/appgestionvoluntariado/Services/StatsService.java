package com.example.appgestionvoluntariado.Services;

import com.example.appgestionvoluntariado.Models.Response.AdminStatsResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface StatsService {
    @GET("stats/general")
    Call<AdminStatsResponse> getStats();
}
