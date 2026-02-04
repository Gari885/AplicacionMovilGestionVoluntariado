package com.example.appgestionvoluntariado.Fragments.Volunteer;

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

public class VolunteerMyProjectsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProjectAdapter projectAdapter;
    private com.google.android.material.button.MaterialButton btnTabAccepted, btnTabPending;
    private String currentStatusFilter = "pendiente"; // Default filter

    // Listas para el filtrado
    private List<Project> allEnrolledProjects = new ArrayList<>();
    private List<Project> displayedProjects = new ArrayList<>();

    private View loadingLayout;
    private TextView loadingText;
    private android.widget.ImageView logoSpinner;
    private android.view.animation.Animation rotateAnimation;
    
    private EditText etSearch;

    private ProjectsService projectsService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Mantenemos la estética limpia del diseño Cuatrovientos
        View view = inflater.inflate(R.layout.fragment_volunteer_my_projects, container, false);

        initViews(view);
        setupSearch();
        setupFilterTabs();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadEnrolledProjects(currentStatusFilter);
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.rvProjects);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadingLayout = view.findViewById(R.id.layoutLoading);
        loadingText = view.findViewById(R.id.tvLoadingText);
        logoSpinner = view.findViewById(R.id.ivLogoSpinner);
        
        rotateAnimation = android.view.animation.AnimationUtils.loadAnimation(getContext(), R.anim.rotate_infinite);
        if (logoSpinner != null) {
            logoSpinner.startAnimation(rotateAnimation);
        }

        etSearch = view.findViewById(R.id.etSearchProject);
        btnTabAccepted = view.findViewById(R.id.btnTabAccepted);
        btnTabPending = view.findViewById(R.id.btnTabPending);

        // APIClient ya debe incluir el AuthInterceptor para el Token
        projectsService = APIClient.getProjectsService();
    }

    private void setupFilterTabs() {
        btnTabAccepted.setOnClickListener(v -> switchTab("aceptado"));
        btnTabPending.setOnClickListener(v -> switchTab("pendiente"));
    }

    private void switchTab(String status) {
        if (currentStatusFilter.equals(status)) return;
        currentStatusFilter = status;
        updateTabStyles();
        loadEnrolledProjects(currentStatusFilter);
    }

    private void updateTabStyles() {
        boolean isAccepted = currentStatusFilter.equals("aceptado");
        int blue = android.graphics.Color.parseColor("#1A3B85");
        int white = android.graphics.Color.WHITE;

        // Configure "Accepted" Button
        if (isAccepted) {
            btnTabAccepted.setBackgroundTintList(android.content.res.ColorStateList.valueOf(blue));
            btnTabAccepted.setTextColor(white);
            btnTabAccepted.setStrokeWidth(0);
        } else {
            btnTabAccepted.setBackgroundTintList(android.content.res.ColorStateList.valueOf(white));
            btnTabAccepted.setTextColor(blue);
            btnTabAccepted.setStrokeColor(android.content.res.ColorStateList.valueOf(blue));
            btnTabAccepted.setStrokeWidth(4); // 2dp approx
        }

        // Configure "Pending" Button
        if (!isAccepted) {
            btnTabPending.setBackgroundTintList(android.content.res.ColorStateList.valueOf(blue));
            btnTabPending.setTextColor(white);
            btnTabPending.setStrokeWidth(0);
        } else {
            btnTabPending.setBackgroundTintList(android.content.res.ColorStateList.valueOf(white));
            btnTabPending.setTextColor(blue);
            btnTabPending.setStrokeColor(android.content.res.ColorStateList.valueOf(blue));
            btnTabPending.setStrokeWidth(4); // 2dp approx
        }
    }

    private void loadEnrolledProjects(String newStatus) {
        startLoadingAnimation("Cargando tus voluntariados");

        // IMPORTANTE: Ya no pasamos el DNI. El backend lo obtiene del Token Firebase
        projectsService.getEnrolledProjects(newStatus).enqueue(new Callback<List<Project>>() {
            @Override
            public void onResponse(Call<List<Project>> call, Response<List<Project>> response) {
                stopLoadingAnimation();
                if (response.isSuccessful() && response.body() != null) {
                    allEnrolledProjects = response.body();
                    displayedProjects.clear();
                    displayedProjects.addAll(allEnrolledProjects);
                    updateUi();
                }
            }

            @Override
            public void onFailure(Call<List<Project>> call, Throwable t) {
                stopLoadingAnimation();
                StatusHelper.showToast(getContext(), "Error de conexión", true);
            }
        });
    }

    private void updateUi() {
        // Usamos el modo VOLUNTEER_MY_PROJECTS para mostrar el botón de anular en rojo
        projectAdapter = new ProjectAdapter(displayedProjects, new ProjectAdapter.OnProjectActionListener() {
            @Override
            public void onAccept(Project project) {

            }

            @Override
            public void onReject(Project project) {

            }

            @Override
            public void onDelete(Project project) {
                performUnenrollment(project);
            }

            @Override
            public void onApply(Project project) { }
                
            @Override
            public void onEdit(Project project) { }
            },ViewMode.VOLUNTEER_MY_PROJECTS);

        // Mostrar etiqueta de estado SOLO si estamos en la pestaña TAB_ACCEPTED
        projectAdapter.setShowStatusLabel(currentStatusFilter.equals("aceptado"));

        recyclerView.setAdapter(projectAdapter);
    }

    private void performUnenrollment(Project item) {
        startLoadingAnimation("Anulando inscripción");

        // Tampoco pasamos el DNI aquí; la seguridad la da el Token
        // FIXED: Usamos getActivityId() porque el endpoint ahora es actividades/{id}/desinscribir [cite: 2026-01-18]
        projectsService.unenroll(item.getActivityId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                stopLoadingAnimation();
                if (response.isSuccessful()) {
                    StatusHelper.showToast(getContext(), "Inscripción anulada", false);

                    // Actualizamos ambas listas
                    allEnrolledProjects.remove(item);
                    displayedProjects.remove(item);
                    projectAdapter.notifyAdapter(displayedProjects);
                } else {
                    StatusHelper.showToast(getContext(), "No se pudo anular", true);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                stopLoadingAnimation();
                StatusHelper.showToast(getContext(), "Error al comunicar con el servidor", true);
            }
        });
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filter(String text) {
        List<Project> filtered = new ArrayList<>();
        String query = text.toLowerCase().trim();

        if (query.isEmpty()) {
            filtered.addAll(allEnrolledProjects);
        } else {
            for (Project p : allEnrolledProjects) {
                boolean matchName = p.getName() != null && p.getName().toLowerCase().contains(query);
                boolean matchDesc = p.getDescription() != null && p.getDescription().toLowerCase().contains(query);
                if (matchName || matchDesc) {
                    filtered.add(p);
                }
            }
        }
        
        if (projectAdapter != null) {
            projectAdapter.updateList(filtered);
        }
    }

    private void startLoadingAnimation(String message) {
        loadingLayout.setVisibility(View.VISIBLE);
        loadingText.setText(message);
    }

    private void stopLoadingAnimation() {
        loadingLayout.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}