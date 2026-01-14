package com.example.appgestionvoluntariado.Fragments.Volunteer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgestionvoluntariado.Adapters.ProjectAdapter;
import com.example.appgestionvoluntariado.Models.Project;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Services.ProjectsAPIService;
import com.example.appgestionvoluntariado.ViewMode;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VolunteerExploreFragment extends Fragment {

    private List<Project> availableProjects = new ArrayList<>();
    private ProjectsAPIService projectsAPIService;
    private RecyclerView recyclerView;
    private ProjectAdapter projectAdapter;
    private LinearLayout loadingLayout;
    private TextView loadingText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Mantenemos la estética blanca y limpia del diseño de 2026
        View view = inflater.inflate(R.layout.fragment_volunteer_explore_projects, container, false);

        initViews(view);
        loadAvailableProjects();

        return view;
    }

    private void initViews(View view) {
        loadingLayout = view.findViewById(R.id.layoutLoading);
        loadingText = view.findViewById(R.id.tvLoadingText);
        recyclerView = view.findViewById(R.id.rvProjects);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // APIClient ya inyecta automáticamente el Token de Firebase en la cabecera
        projectsAPIService = APIClient.getProjectsAPIService();
    }

    private void loadAvailableProjects() {
        showLoading("Buscando nuevas ofertas...");

        // Ya no enviamos el DNI; el backend lo extrae del token de autorización
        projectsAPIService.getAvailableProjects().enqueue(new Callback<List<Project>>() {
            @Override
            public void onResponse(Call<List<Project>> call, Response<List<Project>> response) {
                hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    availableProjects = response.body();
                    updateUi();
                } else {
                    Toast.makeText(getContext(), "No se pudieron cargar las ofertas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Project>> call, Throwable t) {
                hideLoading();
                Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUi() {
        // Usamos VOLUNTEER_AVAILABLE para mostrar el botón azul de "Apuntarse"
        projectAdapter = new ProjectAdapter(getContext(), availableProjects, ViewMode.VOLUNTEER_AVAILABLE, new ProjectAdapter.OnItemAction() {
            @Override
            public void onPrimaryAction(Project item) {
                enrollInProject(item);
            }

            @Override
            public void onSecondaryAction(Project item) {
                // El adapter ya abre el popup de información detallada
            }
        });
        recyclerView.setAdapter(projectAdapter);
    }

    private void enrollInProject(Project item) {
        showLoading("Tramitando tu inscripción...");

        // Llamada simplificada: solo pasamos el ID del proyecto
        projectsAPIService.enroll(item.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                hideLoading();
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "¡Inscrito correctamente!", Toast.LENGTH_SHORT).show();

                    // Quitamos el proyecto de la lista de disponibles para que no aparezca dos veces
                    availableProjects.remove(item);
                    projectAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Error: El servidor rechazó la inscripción", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                hideLoading();
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Métodos auxiliares para la UI
    private void showLoading(String message) {
        if (loadingLayout != null) {
            loadingLayout.setVisibility(View.VISIBLE);
            loadingText.setText(message);
        }
    }

    private void hideLoading() {
        if (loadingLayout != null) {
            loadingLayout.setVisibility(View.GONE);
        }
    }
}