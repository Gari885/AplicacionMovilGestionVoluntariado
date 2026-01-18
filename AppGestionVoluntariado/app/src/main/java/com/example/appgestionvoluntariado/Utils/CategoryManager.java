package com.example.appgestionvoluntariado.Utils;


import com.example.appgestionvoluntariado.Models.*;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Services.CategoryService;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryManager {
    private final CategoryService service;

    public CategoryManager() {
        this.service = APIClient.getClient().create(CategoryService.class);
    }

    public interface CategoryCallback<T> {
        void onSuccess(List<T> data);
        void onError(String error);
    }

    public void fetchAllCategories(CategoryCallback<Ods> odsCb,
                                   CategoryCallback<Skill> skillCb,
                                   CategoryCallback<Interest> interestCb,
                                   CategoryCallback<Need> needCb) {

        // Fetch ODS [cite: 2026-01-09]
        service.getOds().enqueue(new Callback<List<Ods>>() {
            @Override
            public void onResponse(Call<List<Ods>> call, Response<List<Ods>> response) {
                if (response.isSuccessful()) odsCb.onSuccess(response.body());
                else odsCb.onError("Error loading ODS");
            }
            @Override
            public void onFailure(Call<List<Ods>> call, Throwable t) { odsCb.onError(t.getMessage()); }
        });

        // Fetch Skills [cite: 2026-01-09]
        service.getSkills().enqueue(new Callback<List<Skill>>() {
            @Override
            public void onResponse(Call<List<Skill>> call, Response<List<Skill>> response) {
                if (response.isSuccessful()) skillCb.onSuccess(response.body());
                else skillCb.onError("Error loading Skills");
            }
            @Override
            public void onFailure(Call<List<Skill>> call, Throwable t) { skillCb.onError(t.getMessage()); }
        });

        // Fetch Interests [cite: 2026-01-09]
        service.getInterests().enqueue(new Callback<List<Interest>>() {
            @Override
            public void onResponse(Call<List<Interest>> call, Response<List<Interest>> response) {
                if (response.isSuccessful()) interestCb.onSuccess(response.body());
                else interestCb.onError("Error loading Interests");
            }
            @Override
            public void onFailure(Call<List<Interest>> call, Throwable t) { interestCb.onError(t.getMessage()); }
        });

        // Fetch Needs [cite: 2026-01-09]
        service.getNeeds().enqueue(new Callback<List<Need>>() {
            @Override
            public void onResponse(Call<List<Need>> call, Response<List<Need>> response) {
                if (response.isSuccessful()) needCb.onSuccess(response.body());
                else needCb.onError("Error loading Needs");
            }
            @Override
            public void onFailure(Call<List<Need>> call, Throwable t) { needCb.onError(t.getMessage()); }
        });

        service.getNeeds().enqueue(new Callback<List<Need>>() {
            @Override
            public void onResponse(Call<List<Need>> call, Response<List<Need>> response) {
                if (response.isSuccessful()) needCb.onSuccess(response.body());
                else needCb.onError("Error loading Needs");
            }
            @Override
            public void onFailure(Call<List<Need>> call, Throwable t) { needCb.onError(t.getMessage()); }
        });
    }
}
