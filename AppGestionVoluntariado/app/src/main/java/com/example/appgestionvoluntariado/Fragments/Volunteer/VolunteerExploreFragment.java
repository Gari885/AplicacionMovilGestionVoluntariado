package com.example.appgestionvoluntariado.Fragments.Volunteer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.example.appgestionvoluntariado.Services.ProjectsService;
import com.example.appgestionvoluntariado.Utils.StatusHelper;
import com.example.appgestionvoluntariado.ViewMode;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VolunteerExploreFragment extends Fragment {

    private List<Project> availableProjects = new ArrayList<>();
    private ProjectsService projectsService;
    private RecyclerView recyclerView;
    private ProjectAdapter projectAdapter;
    private View loadingLayout;
    private TextView loadingText;
    private android.widget.ImageView logoSpinner;
    private android.view.animation.Animation rotateAnimation;

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
        logoSpinner = view.findViewById(R.id.ivLogoSpinner);
        
        rotateAnimation = android.view.animation.AnimationUtils.loadAnimation(getContext(), R.anim.rotate_infinite);
        if (logoSpinner != null) {
            logoSpinner.startAnimation(rotateAnimation);
        }

        recyclerView = view.findViewById(R.id.rvProjects);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // APIClient ya inyecta automáticamente el Token de Firebase en la cabecera
        projectsService = APIClient.getProjectsService();
    }

    private void loadAvailableProjects() {
        showLoading("Buscando nuevas ofertas...");

        // Ya no enviamos el DNI; el backend lo extrae del token de autorización
        projectsService.getAvailableProjects().enqueue(new Callback<List<Project>>() {
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
        projectAdapter = new ProjectAdapter(availableProjects, new ProjectAdapter.OnProjectActionListener() {
            @Override
            public void onAccept(Project project) {}
            @Override
            public void onReject(Project project) {}
            @Override
            public void onDelete(Project project) {}
            @Override
            public void onApply(Project project) {enrollInProject(project);}

            @Override
            public void onEdit(Project project) {
                // No edit for volunteers
            }
        }, ViewMode.VOLUNTEER_AVAILABLE);
        recyclerView.setAdapter(projectAdapter);
    }

    private void enrollInProject(Project item) {
        showLoading("Tramitando tu inscripción...");

        // Llamada simplificada: solo pasamos el ID del proyecto
        projectsService.enroll(item.getActivityId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                hideLoading();
                if (response.isSuccessful()) {
                    StatusHelper.showToast(getContext(), "¡Inscrito correctamente!", false);

                    // Quitamos el proyecto de la lista de disponibles para que no aparezca dos veces
                    availableProjects.remove(item);
                    projectAdapter.notifyAdapter(availableProjects);
                } else {
                    try {
                        String errorJson = response.errorBody().string();
                        org.json.JSONObject jObj = new org.json.JSONObject(errorJson);
                        String msg = jObj.optString("error", "No se pudo inscribir");
                        StatusHelper.showToast(getContext(), msg, true);
                    } catch (Exception e) {
                        StatusHelper.showToast(getContext(), "Error al procesar la solicitud", true);
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                hideLoading();
                StatusHelper.showToast(getContext(), "Error de conexión", true);
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