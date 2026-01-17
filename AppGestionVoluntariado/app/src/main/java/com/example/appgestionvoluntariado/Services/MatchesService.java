package com.example.appgestionvoluntariado.Services;

import com.example.appgestionvoluntariado.Models.Match;
import com.example.appgestionvoluntariado.Models.StatusRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MatchesService {

    @GET("inscripciones")
    Call<List<Match>> getMatches(@Query("estado")String estado);
    @PATCH("inscripciones/{id}/estado")
    Call<Void> updateStatus(@Path("id") int id, @Body StatusRequest request);
}
