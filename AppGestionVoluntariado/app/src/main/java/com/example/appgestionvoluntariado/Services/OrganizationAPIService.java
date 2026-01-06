package com.example.appgestionvoluntariado.Services;

import com.example.appgestionvoluntariado.Models.EstadoRequest;
import com.example.appgestionvoluntariado.Models.Organizacion;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

public interface OrganizationAPIService {
    @GET("organizations")
    Call<List<Organizacion>> getOrganizations();
    @PATCH("organizations/{cif}/state")
    Call<Organizacion> actualizarEstado(
            @Path("cif") String cif,       // Rellena la parte {cif} de la URL
            @Body EstadoRequest request    // Envía el JSON { "estado": "..." }
    );

}
