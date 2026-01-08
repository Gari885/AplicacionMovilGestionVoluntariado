package com.example.appgestionvoluntariado.Services;

import com.example.appgestionvoluntariado.Models.StatusRequest;
import com.example.appgestionvoluntariado.Models.Organization;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

public interface OrganizationAPIService {
    @GET("organizations")
    Call<List<Organization>> getOrganizations();
    @PATCH("organizations/{cif}/state")
    Call<Organization> updateStatus(
            @Path("cif") String cif,       
            @Body StatusRequest request    
    );

}
