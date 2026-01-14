package com.example.appgestionvoluntariado.Fragments.Admin;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgestionvoluntariado.Adapters.DashboardAdapter;
import com.example.appgestionvoluntariado.Models.Match;
import com.example.appgestionvoluntariado.Models.Organization;
import com.example.appgestionvoluntariado.Models.Project;
import com.example.appgestionvoluntariado.Models.Stat;
import com.example.appgestionvoluntariado.Models.Volunteer;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Services.MatchesAPIService;
import com.example.appgestionvoluntariado.Services.OrganizationAPIService;
import com.example.appgestionvoluntariado.Services.ProjectsAPIService;
import com.example.appgestionvoluntariado.Services.VolunteerService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDashboardFragment extends Fragment {

    private List<Stat> stats;
    private RecyclerView recyclerView;
    private DashboardAdapter dashboardAdapter;

    private View loadingLayout;

    private int completedCalls = 0;
    private int messageIndex = 0;
    private final int TOTAL_CALLS = 4;

    private final String[] LOADING_PHRASES = {
            "Obteniendo lista de voluntarios...",
            "Conectando con organizaciones...",
            "Cargando voluntariados disponibles...",
            "Calculando estadísticas...",
            "Sincronizando datos..."
    };

    private Handler animationHandler = new Handler();
    private Runnable animationRunnable;
    private TextView loadingText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);
        recyclerView = view.findViewById(R.id.rvDashboard);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        
        loadingLayout = view.findViewById(R.id.layoutLoading);
        loadingLayout.setVisibility(View.VISIBLE);
        loadingText = view.findViewById(R.id.tvLoadingText);
        stats = new ArrayList<>();

        startLoadingAnimation();
        loadDataInParallel();

        return view;
    }

    private void loadDataInParallel() {
        completedCalls = 0;

        // Load Projects
        ProjectsAPIService projectsAPIService = APIClient.getProjectsAPIService();
        Call<List<Project>> callProject = projectsAPIService.getProjects();
        callProject.enqueue(new Callback<List<Project>>() {
            @Override
            public void onResponse(Call<List<Project>> call, Response<List<Project>> response) {
                if (response.isSuccessful() && response.body() != null){
                    GlobalData.getInstance().projects = response.body();
                }
                checkIfFinished();
            }

            @Override
            public void onFailure(Call<List<Project>> call, Throwable t) {
                checkIfFinished();
            }
        });

        // Load Organizations
        OrganizationAPIService organizationAPIService = APIClient.getOrganizationAPIService();
        Call<List<Organization>> callOrg = organizationAPIService.getOrganizations();
        callOrg.enqueue(new Callback<List<Organization>>() {
            @Override
            public void onResponse(Call<List<Organization>> call, Response<List<Organization>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GlobalData.getInstance().organizations = response.body();
                }
                checkIfFinished();
            }

            @Override
            public void onFailure(Call<List<Organization>> call, Throwable t) {
                checkIfFinished();
            }
        });

        // Load Volunteers
        VolunteerService volunteerAPIService = APIClient.getVolunteerAPIService();
        Call<List<Volunteer>> callVol = volunteerAPIService.getVolunteers();
        callVol.enqueue(new Callback<List<Volunteer>>() {
            @Override
            public void onResponse(Call<List<Volunteer>> call, Response<List<Volunteer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GlobalData.getInstance().volunteers = response.body();
                }
                checkIfFinished();
            }

            @Override
            public void onFailure(Call<List<Volunteer>> call, Throwable t) {
                checkIfFinished();
            }
        });

        // Load Matches
        MatchesAPIService matchesAPIService = APIClient.getMatchesAPIService();
        Call<List<Match>> callMatch = matchesAPIService.getMatches();
        callMatch.enqueue(new Callback<List<Match>>() {
            @Override
            public void onResponse(Call<List<Match>> call, Response<List<Match>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GlobalData.getInstance().matches = response.body();
                }
                checkIfFinished();
            }

            @Override
            public void onFailure(Call<List<Match>> call, Throwable t) {
                checkIfFinished();
            }
        });
    }

    private void createVolunteerCard() {
        int accepted = 0;
        int pending = 0;
        List<Volunteer> volunteers = GlobalData.getInstance().volunteers;
        if (volunteers != null) {
            for (Volunteer vol : volunteers) {
                String status = vol.getStatus();
                if ("Activo".equalsIgnoreCase(status) || "ACEPTADO".equalsIgnoreCase(status)) {
                    accepted++;
                }
                if ("Pendiente".equalsIgnoreCase(status) || "PENDIENTE".equalsIgnoreCase(status)) {
                    pending++;
                }
            }
        }

        String pendingStr = (pending > 0) ? "+" + pending + " Pendientes" : "Ningún pendiente";
        stats.add(new Stat("VOLUNTARIOS", accepted, pendingStr, R.drawable.ic_volunteers_group));
    }

    private void createOrganizationCard() {
        int accepted = 0;
        int pending = 0;
        List<Organization> organizations = GlobalData.getInstance().organizations;
        if (organizations != null) {
            for (Organization org : organizations) {
                String status = org.getStatus();
                if ("Activo".equalsIgnoreCase(status) || "aprobado".equalsIgnoreCase(status)) {
                    accepted++;
                }
                if ("Pendiente".equalsIgnoreCase(status)) {
                    pending++;
                }
            }
        }

        String pendingStr = (pending > 0) ? "+" + pending + " Pendientes" : "Ningún pendiente";
        stats.add(new Stat("ORGANIZACIONES", accepted, pendingStr, R.drawable.ic_volunteers_group));
    }

    private void startLoadingAnimation() {
        animationRunnable = new Runnable() {
            @Override
            public void run() {
                loadingText.setText(LOADING_PHRASES[messageIndex]);
                messageIndex = (messageIndex + 1) % LOADING_PHRASES.length;
                animationHandler.postDelayed(this, 800);
            }
        };
        animationHandler.post(animationRunnable);
    }

    private void stopLoadingAnimation() {
        if (animationRunnable != null) {
            animationHandler.removeCallbacks(animationRunnable);
        }
    }

    private void checkIfFinished() {
        completedCalls++;
        if (completedCalls == TOTAL_CALLS) {
            stopLoadingAnimation();
            calculateStats();
            loadingLayout.setVisibility(View.GONE);
        }
    }

    private void showStats() {
        if (dashboardAdapter == null) {
            dashboardAdapter = new DashboardAdapter(stats);
            recyclerView.setAdapter(dashboardAdapter);
        } else {
            dashboardAdapter.notifyDataSetChanged();
        }
    }

    private void calculateStats() {
        stats.clear();
        createVolunteerCard();
        createOrganizationCard();
        showStats();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopLoadingAnimation();
    }
}
