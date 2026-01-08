package com.example.appgestionvoluntariado.Services;

import com.example.appgestionvoluntariado.Models.Match;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface MatchesAPIService {

    @GET("inscripciones")
    Call<List<Match>> getMatches();
}
