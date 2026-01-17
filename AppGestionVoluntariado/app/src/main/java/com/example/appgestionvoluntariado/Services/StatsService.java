package com.example.appgestionvoluntariado.Services;

import com.example.appgestionvoluntariado.Models.AdminStatsResponse;
import com.example.appgestionvoluntariado.Models.Stat;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface StatsService {
    @GET("stats/general")
    Call<AdminStatsResponse> getStats();
}
