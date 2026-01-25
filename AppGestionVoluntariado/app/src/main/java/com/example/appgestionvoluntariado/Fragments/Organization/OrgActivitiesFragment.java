package com.example.appgestionvoluntariado.Fragments.Organization;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgestionvoluntariado.Adapters.ProjectAdapter;
import com.example.appgestionvoluntariado.Fragments.Auth.CreateProjectFragment;
import com.example.appgestionvoluntariado.Models.Project;
import com.example.appgestionvoluntariado.Models.Request.StatusRequest;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Utils.StatusHelper;
import com.example.appgestionvoluntariado.ViewMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrgActivitiesFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProjectAdapter adapter;
    private TextView tabPending, tabAccepted;
    private EditText etSearch;
    private View loadingLayout;
    private android.widget.ImageView logoSpinner;
    private android.view.animation.Animation rotateAnimation;
    private FloatingActionButton fabAddProject;

    private List<Project> allProjects = new ArrayList<>();

    private String currentStatus = "pendiente"; // PENDIENTE o APROBADO

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getParentFragmentManager().setFragmentResultListener("project_created", this, (requestKey, result) -> {
            loadMyProjects();
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Usamos la plantilla de CoordinatorLayout que enviamos antes
        View view = inflater.inflate(R.layout.fragment_organization_my_projects, container, false);

        initViews(view);
        setupTabs();
        setupSearch();
        loadMyProjects();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.rvProjects);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        tabPending = view.findViewById(R.id.tabStatusPending);
        tabAccepted = view.findViewById(R.id.tabStatusAccepted);
        etSearch = view.findViewById(R.id.etSearchProject);
        loadingLayout = view.findViewById(R.id.layoutLoading);
        logoSpinner = view.findViewById(R.id.ivLogoSpinner);
        
        rotateAnimation = android.view.animation.AnimationUtils.loadAnimation(getContext(), R.anim.rotate_infinite);
        if (logoSpinner != null) {
            logoSpinner.startAnimation(rotateAnimation);
        }

        fabAddProject = view.findViewById(R.id.fabAddProject);

        // Botón para crear nuevo proyecto
        fabAddProject.setOnClickListener(v -> getParentFragmentManager().beginTransaction()
                .replace(R.id.organization_fragment_container, new CreateProjectFragment())
                .addToBackStack(null)
                .commit());
    }

    private void setupTabs() {
        tabPending.setOnClickListener(v -> {
            currentStatus = "pendiente";
            updateTabUI(tabPending, tabAccepted);
            loadMyProjects();
        });

        tabAccepted.setOnClickListener(v -> {
            currentStatus = "aprobado";
            updateTabUI(tabAccepted, tabPending);
            loadMyProjects();
        });
    }

    private void updateTabUI(TextView selected, TextView unselected) {
        selected.setBackgroundResource(R.drawable.background_tab_selected);
        selected.setTextColor(android.graphics.Color.WHITE);
        unselected.setBackgroundResource(R.drawable.background_tab_unselected);
        unselected.setTextColor(android.graphics.Color.parseColor("#1A3B85"));
    }

    private void loadMyProjects() {
        loadingLayout.setVisibility(View.VISIBLE);
        // Toast.makeText(getContext(), "Cargando proyectos...", Toast.LENGTH_SHORT).show(); // Removed prompt to avoid noise, user sees spinner
        // El servidor filtra por el Token de Firebase de la organización
        // Enviamos el estado como Query Param ?estado=
        APIClient.getOrganizationService().getMyProjects(currentStatus)
                .enqueue(new Callback<List<Project>>() {
                    @Override
                    public void onResponse(Call<List<Project>> call, Response<List<Project>> response) {
                        loadingLayout.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            allProjects = response.body();
                            StatusHelper.showToast(getContext(), "Cargados: " + allProjects.size(), false); // Debug
                            updateRecyclerView(allProjects);
                        } else {
                            StatusHelper.showToast(getContext(), "Error: " + response.code(), true);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Project>> call, Throwable t) {
                        loadingLayout.setVisibility(View.GONE);
                        StatusHelper.showToast(getContext(), "Error al cargar: " + t.getMessage(), true);
                    }
                });
    }

    private void updateRecyclerView(List<Project> list) {
        // Usamos el adaptador unificado con el OnProjectActionListener
        adapter = new ProjectAdapter(list, new ProjectAdapter.OnProjectActionListener() {
            @Override
            public void onAccept(Project project) {}
            @Override
            public void onReject(Project project) {}
            @Override
            public void onDelete(Project project) {cancelProject(project);}
            @Override
            public void onApply(Project project) {}
        }, ViewMode.ORGANIZATION);
        recyclerView.setAdapter(adapter);
    }

    private void cancelProject(Project project) {
        loadingLayout.setVisibility(View.VISIBLE);

        // Log para ver qué enviamos (útil si hay dudas)
        Log.d("DEBUG_CANCEL", "Cancelando Proyecto ID: " + project.getActivityId());

        // Asegúrate de que tu interfaz ahora devuelve Call<ResponseBody>
        APIClient.getProjectsService().changeState(project.getActivityId(), new StatusRequest("cancelado"))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        // Ocultamos el loading SIEMPRE al recibir respuesta, sea buena o mala
                        loadingLayout.setVisibility(View.GONE);

                        if (response.isSuccessful()) {
                            try {
                                // Leemos la respuesta del servidor para verificar
                                String serverResponse = response.body() != null ? response.body().string() : "null";
                                Log.d("DEBUG_CANCEL", "Éxito (200 OK): " + serverResponse);

                                StatusHelper.showToast(getContext(), "Proyecto cancelado correctamente", false);
                                loadMyProjects(); // Recargar lista

                            } catch (IOException e) {
                                Log.e("DEBUG_CANCEL", "Error leyendo respuesta OK", e);
                                // Aún así asumimos éxito visual
                                StatusHelper.showToast(getContext(), "Proyecto cancelado", false);
                                loadMyProjects();
                            }
                        } else {
                            // Error del servidor (404, 403, 500...)
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                                Log.e("DEBUG_CANCEL", "Fallo " + response.code() + ": " + errorBody);

                                // Mensaje más amigable según el código
                                if (response.code() == 404) {
                                    StatusHelper.showToast(getContext(), "Error: Proyecto no encontrado", true);
                                } else if (response.code() == 403) {
                                    StatusHelper.showToast(getContext(), "No tienes permiso para cancelar este proyecto", true);
                                } else {
                                    StatusHelper.showToast(getContext(), "Error al cancelar (" + response.code() + ")", true);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        loadingLayout.setVisibility(View.GONE);
                        Log.e("DEBUG_CANCEL", "Error de red/conexión", t);
                        StatusHelper.showToast(getContext(), "Error de conexión", true);
                    }
                });
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void filter(String text) {
        List<Project> filtered = new ArrayList<>();
        String query = text.toLowerCase().trim();
        for (Project p : allProjects) {
            if (p.getName().toLowerCase().contains(query)) {
                filtered.add(p);
            }
        }
        updateRecyclerView(filtered);
    }
}