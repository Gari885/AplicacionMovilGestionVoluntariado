package com.example.appgestionvoluntariado.Fragments.Organization;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.appgestionvoluntariado.Models.StatusRequest;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.ViewMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrgActivitiesFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProjectAdapter adapter;
    private TextView tabPending, tabAccepted;
    private EditText etSearch;
    private View loadingLayout;
    private FloatingActionButton fabAdd;

    private List<Project> allProjects = new ArrayList<>();
    private String currentStatus = "pendiente"; // PENDIENTE o APROBADO

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
        fabAdd = view.findViewById(R.id.fabAddProject);

        // Botón para crear nuevo proyecto
        fabAdd.setOnClickListener(v -> getParentFragmentManager().beginTransaction()
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
        // El servidor filtra por el Token de Firebase de la organización
        // Enviamos el estado como Query Param ?estado=
        APIClient.getProjectsService().getProjects(currentStatus)
                .enqueue(new Callback<List<Project>>() {
                    @Override
                    public void onResponse(Call<List<Project>> call, Response<List<Project>> response) {
                        loadingLayout.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            allProjects = response.body();
                            updateRecyclerView(allProjects);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Project>> call, Throwable t) {
                        loadingLayout.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Error al cargar tus proyectos", Toast.LENGTH_SHORT).show();
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
        // No enviamos CIF, el token identifica que es tu proyecto
        APIClient.getProjectsService().changeState(project.getActivityId(), new StatusRequest("rechazado"))
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            loadMyProjects();
                        } else {
                            loadingLayout.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Error al cancelar proyecto", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        loadingLayout.setVisibility(View.GONE);
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