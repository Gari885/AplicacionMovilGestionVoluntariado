package com.example.appgestionvoluntariado.Fragments.Volunteer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgestionvoluntariado.Adapters.ProjectAdapter;
import com.example.appgestionvoluntariado.GlobalData;
import com.example.appgestionvoluntariado.GlobalSession;
import com.example.appgestionvoluntariado.Models.Match;
import com.example.appgestionvoluntariado.Models.Project;
import com.example.appgestionvoluntariado.Models.VolunteerEnrollRequest;
import com.example.appgestionvoluntariado.ViewMode;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Services.ProjectsAPIService;
import com.example.appgestionvoluntariado.Services.MatchesAPIService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VolunteerExploreFragment extends Fragment {

    private List<Project> filteredProjects = new ArrayList<>();
    private ProjectsAPIService projectsAPIService;

    private RecyclerView recyclerView;
    private ProjectAdapter projectAdapter;
    private LinearLayout loadingLayout;
    private TextView loadingText;

    private Handler animationHandler;
    private Runnable animationRunnable;
    private final String LOADING_TEXT = "Cargando ofertas";
    private int dotCount = 0;
    private int callCount = 0;
    private final int TOTAL_CALLS = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_volunteer_explore_projects, container, false);

        initViews(view);

        callCount = 0;
        startAnimation();
        callApi();

        return view;
    }

    private void initViews(View view) {
        loadingLayout = view.findViewById(R.id.layoutLoading);
        loadingText = view.findViewById(R.id.tvLoadingText);

        recyclerView = view.findViewById(R.id.rvProjects);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        projectsAPIService = APIClient.getProjectsAPIService();
        animationHandler = new Handler(Looper.getMainLooper());
    }

    private void callApi() {
        projectsAPIService.getProjects().enqueue(new Callback<List<Project>>() {
            @Override
            public void onResponse(Call<List<Project>> call, Response<List<Project>> response) {
                if (response.isSuccessful() && response.body() != null){
                    GlobalData.getInstance().projects = response.body();
                }
                checkIfCallsFinished();
            }

            @Override
            public void onFailure(Call<List<Project>> call, Throwable t) {
                checkIfCallsFinished();
            }
        });

        MatchesAPIService matchesAPIService = APIClient.getMatchesAPIService();
        matchesAPIService.getMatches().enqueue(new Callback<List<Match>>() {
            @Override
            public void onResponse(Call<List<Match>> call, Response<List<Match>> response) {
                if (response.isSuccessful() && response.body() != null){
                    GlobalData.getInstance().matches = response.body();
                }
                checkIfCallsFinished();
            }

            @Override
            public void onFailure(Call<List<Match>> call, Throwable t) {
                checkIfCallsFinished();
            }
        });
    }

    private void checkIfCallsFinished() {
        callCount++;
        if (callCount >= TOTAL_CALLS) {
            stopAnimation();
            loadingLayout.setVisibility(View.GONE);
            prepareProjectList();
        }
    }

    private void prepareProjectList() {
        List<Project> allProjects = GlobalData.getInstance().projects;
        List<Match> matches = GlobalData.getInstance().matches;

        String myDni = "";
        if (GlobalSession.getVolunteer() != null) {
            myDni = GlobalSession.getVolunteer().getDni();
        }

        if (filteredProjects == null) filteredProjects = new ArrayList<>();
        else filteredProjects.clear();

        if (allProjects != null && matches != null){
            HashSet<Integer> enrolledProjectIds = new HashSet<>();
            for (Match m : matches) {
                if (myDni.equals(m.getVolunteerDni())) {
                    enrolledProjectIds.add(m.getActivityCode());
                }
            }

            for (Project p : allProjects) {
                if (!enrolledProjectIds.contains(p.getId())) {
                    filteredProjects.add(p);
                }
            }
        }

        showProjects(ViewMode.VOLUNTEER_AVAILABLE);
    }

    private void showProjects(ViewMode viewMode) {
        if (projectAdapter == null) {
            projectAdapter = new ProjectAdapter(getContext(), filteredProjects, viewMode, new ProjectAdapter.OnItemAction() {
                @Override
                public void onPrimaryAction(Project item) {
                    performEnrollment(item);
                }

                @Override
                public void onSecondaryAction(Project item) {
                    // Logic for +Info if needed
                }
            });
            recyclerView.setAdapter(projectAdapter);
        } else {
            projectAdapter.notifyDataSetChanged();
        }
    }

    private void performEnrollment(Project item) {
        loadingLayout.setVisibility(View.VISIBLE);
        loadingText.setText("Apuntando a oferta...");

        String myDni = GlobalSession.getVolunteer().getDni();
        VolunteerEnrollRequest request = new VolunteerEnrollRequest(myDni);

        projectsAPIService.enrollVolunteerInProject(item.getId(), request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                loadingLayout.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "¡Inscripción realizada!", Toast.LENGTH_SHORT).show();
                    filteredProjects.remove(item);
                    projectAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Error al inscribirse (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                loadingLayout.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startAnimation() {
        loadingLayout.setVisibility(View.VISIBLE);
        dotCount = 0;

        animationRunnable = new Runnable() {
            @Override
            public void run() {
                StringBuilder dots = new StringBuilder();
                for (int i = 0; i < dotCount; i++) dots.append(".");
                loadingText.setText(LOADING_TEXT + dots.toString());

                dotCount++;
                if (dotCount > 3) dotCount = 0;

                animationHandler.postDelayed(this, 500);
            }
        };
        animationHandler.post(animationRunnable);
    }

    private void stopAnimation() {
        if (animationRunnable != null && animationHandler != null) {
            animationHandler.removeCallbacks(animationRunnable);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopAnimation();
    }
}
