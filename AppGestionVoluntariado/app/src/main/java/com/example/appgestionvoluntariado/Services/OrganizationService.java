package com.example.appgestionvoluntariado.Services;

import com.example.appgestionvoluntariado.Models.StatusRequest;
import com.example.appgestionvoluntariado.Models.Organization;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OrganizationService {
    @GET("organizations")
    Call<List<Organization>> getOrganizations(@Query("estado")String estado);

    //Se va a usar el authinterceptor para esto
    @PATCH("organizations/{cif}/state")
    Call<Void> updateStatus(
            @Path("cif") String cif,
            @Body StatusRequest request
    );

    @PUT("organiations")
    Call<Void> updateProfile(
            @Body Organization org
    );

}
