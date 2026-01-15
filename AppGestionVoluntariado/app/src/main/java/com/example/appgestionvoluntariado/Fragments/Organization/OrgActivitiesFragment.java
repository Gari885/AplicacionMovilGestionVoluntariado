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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgestionvoluntariado.Adapters.ProjectAdapter;
import com.example.appgestionvoluntariado.Fragments.Auth.CreateProjectFragment;
import com.example.appgestionvoluntariado.Models.Project;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Services.ProjectsService;
import com.example.appgestionvoluntariado.ViewMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrgActivitiesFragment extends Fragment {

    // Vistas
    private RecyclerView recyclerView;
    private ProjectAdapter adapter;
    private TextView tabPending, tabAccepted;
    private EditText etSearch;
    private FloatingActionButton fabAdd;

    // Datos y Lógica
    private List<Project> allProjects = new ArrayList<>();
    private List<Project> filteredProjects = new ArrayList<>();
    private ProjectsService projectsAPIService;
    private boolean showingPending = true; // Controla qué pestaña está activa

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organization_my_projects, container, false);

        initViews(view);
        setupListeners();
        fetchOrganizationProjects();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.rvProjects);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        tabPending = view.findViewById(R.id.tabStatusPending);
        tabAccepted = view.findViewById(R.id.tabStatusAccepted);
        etSearch = view.findViewById(R.id.etSearchProject);
        fabAdd = view.findViewById(R.id.fabAddProject);

        projectsAPIService = APIClient.getProjectsService();
    }

    private void setupListeners() {
        // Cambio a pestaña PENDIENTES
        tabPending.setOnClickListener(v -> {
            showingPending = true;
            updateTabUI();
            applyFilters();
        });

        // Cambio a pestaña ACEPTADOS
        tabAccepted.setOnClickListener(v -> {
            showingPending = false;
            updateTabUI();
            applyFilters();
        });

        // Buscador en tiempo real
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Botón Flotante para crear nuevo voluntariado
        fabAdd.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.organization_fragment_container, new CreateProjectFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void fetchOrganizationProjects() {
        // El APIClient inyecta el Token de Firebase. El servidor filtra por la identidad de la Org.
        projectsAPIService.getMyCreatedProjects().enqueue(new Callback<List<Project>>() {
            @Override
            public void onResponse(Call<List<Project>> call, Response<List<Project>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allProjects = response.body();
                    applyFilters();
                } else {
                    Toast.makeText(getContext(), "No se pudieron obtener tus proyectos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Project>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyFilters() {
        String query = etSearch.getText().toString().toLowerCase().trim();

        // 1. Filtramos por pestaña (Pendiente/Rechazado vs Aprobado)
        // 2. Filtramos por texto de búsqueda
        filteredProjects = allProjects.stream()
                .filter(p -> {
                    boolean matchesStatus = showingPending ?
                            !p.getStatus().equalsIgnoreCase("APPROVED") :
                            p.getStatus().equalsIgnoreCase("APPROVED");

                    boolean matchesQuery = p.getTitle().toLowerCase().contains(query);

                    return matchesStatus && matchesQuery;
                })
                .collect(Collectors.toList());

        updateRecyclerView();
    }

    private void updateRecyclerView() {
        // Usamos ViewMode.ORG_PROJECTS para mostrar los chips de estado adecuados
        adapter = new ProjectAdapter(getContext(), filteredProjects, ViewMode.ORG_PROJECTS, new ProjectAdapter.OnItemAction() {
            @Override
            public void onPrimaryAction(Project item) {
                // TODO: Abrir fragmento de edición o detalles
                Toast.makeText(getContext(), "Editar: " + item.getTitle(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSecondaryAction(Project item) {
                // TODO: Ver lista de voluntarios inscritos en este proyecto específico
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void updateTabUI() {
        // Cambia los fondos y colores de las pestañas según la selección
        if (showingPending) {
            tabPending.setBackgroundResource(R.drawable.background_tab_selected);
            tabPending.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));

            tabAccepted.setBackgroundResource(R.drawable.background_tab_unselected);
            tabAccepted.setTextColor(ContextCompat.getColor(requireContext(), R.color.cuatrovientos_blue));
        } else {
            tabPending.setBackgroundResource(R.drawable.background_tab_unselected);
            tabPending.setTextColor(ContextCompat.getColor(requireContext(), R.color.cuatrovientos_blue));

            tabAccepted.setBackgroundResource(R.drawable.background_tab_selected);
            tabAccepted.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
        }
    }
}