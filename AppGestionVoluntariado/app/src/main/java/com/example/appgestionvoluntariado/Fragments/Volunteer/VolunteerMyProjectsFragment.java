package com.example.appgestionvoluntariado.Fragments.Volunteer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

public class VolunteerMyProjectsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProjectAdapter projectAdapter;

    // Listas para el filtrado
    private List<Project> allEnrolledProjects = new ArrayList<>();
    private List<Project> displayedProjects = new ArrayList<>();

    private LinearLayout loadingLayout;
    private TextView loadingText;
    private EditText etSearch;

    private ProjectsAPIService projectsAPIService;

    private Handler animationHandler;
    private Runnable animationRunnable;
    private int dotCount = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Mantenemos la estética limpia del diseño Cuatrovientos
        View view = inflater.inflate(R.layout.fragment_volunteer_my_projects, container, false);

        initViews(view);
        setupSearch();
        loadEnrolledProjects();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.rvProjects);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadingLayout = view.findViewById(R.id.layoutLoading);
        loadingText = view.findViewById(R.id.tvLoadingText);
        etSearch = view.findViewById(R.id.etSearchProject);

        // APIClient ya debe incluir el AuthInterceptor para el Token
        projectsAPIService = APIClient.getProjectsAPIService();
        animationHandler = new Handler(Looper.getMainLooper());
    }

    private void loadEnrolledProjects() {
        startLoadingAnimation("Cargando tus voluntariados");

        // IMPORTANTE: Ya no pasamos el DNI. El backend lo obtiene del Token Firebase
        projectsAPIService.getEnrolledProjects().enqueue(new Callback<List<Project>>() {
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
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUi() {
        // Usamos el modo VOLUNTEER_MY_PROJECTS para mostrar el botón de anular en rojo
        projectAdapter = new ProjectAdapter(getContext(), displayedProjects, ViewMode.VOLUNTEER_MY_PROJECTS, new ProjectAdapter.OnItemAction() {
            @Override
            public void onPrimaryAction(Project item) {
                performUnenrollment(item);
            }

            @Override
            public void onSecondaryAction(Project item) {
                // Info extra gestionada por el adapter
            }
        });
        recyclerView.setAdapter(projectAdapter);
    }

    private void performUnenrollment(Project item) {
        startLoadingAnimation("Anulando inscripción");

        // Tampoco pasamos el DNI aquí; la seguridad la da el Token
        projectsAPIService.unenroll(item.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                stopLoadingAnimation();
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Inscripción anulada", Toast.LENGTH_SHORT).show();

                    // Actualizamos ambas listas
                    allEnrolledProjects.remove(item);
                    displayedProjects.remove(item);
                    projectAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "No se pudo anular", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                stopLoadingAnimation();
                Toast.makeText(getContext(), "Error al comunicar con el servidor", Toast.LENGTH_SHORT).show();
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
        displayedProjects.clear();
        if (text.isEmpty()) {
            displayedProjects.addAll(allEnrolledProjects);
        } else {
            String query = text.toLowerCase().trim();
            for (Project p : allEnrolledProjects) {
                if (p.getTitle().toLowerCase().contains(query) ||
                        p.getAddress().toLowerCase().contains(query)) {
                    displayedProjects.add(p);
                }
            }
        }
        if (projectAdapter != null) {
            projectAdapter.notifyDataSetChanged();
        }
    }

    private void startLoadingAnimation(String message) {
        loadingLayout.setVisibility(View.VISIBLE);
        animationRunnable = new Runnable() {
            @Override
            public void run() {
                StringBuilder dots = new StringBuilder();
                for (int i = 0; i < dotCount; i++) dots.append(".");
                loadingText.setText(message + dots.toString());
                dotCount = (dotCount + 1) % 4;
                animationHandler.postDelayed(this, 500);
            }
        };
        animationHandler.post(animationRunnable);
    }

    private void stopLoadingAnimation() {
        loadingLayout.setVisibility(View.GONE);
        if (animationHandler != null && animationRunnable != null) {
            animationHandler.removeCallbacks(animationRunnable);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopLoadingAnimation();
    }
}