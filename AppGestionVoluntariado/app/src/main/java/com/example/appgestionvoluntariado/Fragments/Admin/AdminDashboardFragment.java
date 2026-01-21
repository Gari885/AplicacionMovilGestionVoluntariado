package com.example.appgestionvoluntariado.Fragments.Admin;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgestionvoluntariado.Adapters.DashboardAdapter;
import com.example.appgestionvoluntariado.Models.AdminStatsResponse;
import com.example.appgestionvoluntariado.Models.Stat;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Services.StatsService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDashboardFragment extends Fragment {

    private RecyclerView recyclerView;
    private DashboardAdapter dashboardAdapter;
    private ImageView logoSpinner;
    private View loadingLayout;
    private TextView loadingText;
    private Animation rotateAnimation;
    private List<Stat> statsList = new ArrayList<>();

    private Handler animationHandler = new Handler();
    private Runnable animationRunnable;
    private int messageIndex = 0;
    private final String[] LOADING_PHRASES = {
            "Sincronizando con el servidor...",
            "Calculando métricas globales...",
            "Obteniendo estado de la plataforma..."
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);

        initViews(view);
        startLoadingAnimation();
        fetchDashboardStats();

        return view;
    }

    private void initViews(View v) {
        recyclerView = v.findViewById(R.id.rvDashboard);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        logoSpinner = v.findViewById(R.id.ivLogoSpinner);
        rotateAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_infinite);
        logoSpinner.startAnimation(rotateAnimation);

        loadingLayout = v.findViewById(R.id.layoutLoading);
        loadingText = v.findViewById(R.id.tvLoadingText);
    }

    private void fetchDashboardStats() {
        StatsService statsService = APIClient.getStatsService();

        statsService.getStats().enqueue(new Callback<AdminStatsResponse>() {
            @Override
            public void onResponse(Call<AdminStatsResponse> call, Response<AdminStatsResponse> response) {
                stopLoadingAnimation();
                loadingLayout.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    processStats(response.body());
                } else {
                    Toast.makeText(getContext(), "Error al obtener estadísticas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminStatsResponse> call, Throwable t) {
                stopLoadingAnimation();
                loadingLayout.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processStats(AdminStatsResponse data) {
        statsList.clear();

        // Mapeamos los datos de la API a los objetos Stat para el RecyclerView
        statsList.add(new Stat("VOLUNTARIOS",
                data.getVolunteersActive(),
                "+" + data.getVolunteersPending() + " Pendientes",
                R.drawable.ic_volunteers));

        statsList.add(new Stat("ORGANIZACIONES",
                data.getOrgsActive(),
                "+" + data.getOrgsPending() + " Pendientes",
                R.drawable.ic_organizations));

        statsList.add(new Stat("PROYECTOS",
                data.getProjectsActive(),
                "+" + data.getProjectsPending() + " por Validar",
                R.drawable.ic_projects));

        statsList.add(new Stat("MATCHES",
                data.getTotalMatches(),
                "Conexiones exitosas",
                R.drawable.ic_matches));

        updateUI();
    }

    private void updateUI() {
        if (dashboardAdapter == null) {
            dashboardAdapter = new DashboardAdapter(statsList);
            recyclerView.setAdapter(dashboardAdapter);
        } else {
            dashboardAdapter.notifyDataSetChanged();
        }
    }

    // --- Lógica de Animación ---
    private void startLoadingAnimation() {
        animationRunnable = new Runnable() {
            @Override
            public void run() {
                loadingText.setText(LOADING_PHRASES[messageIndex]);
                messageIndex = (messageIndex + 1) % LOADING_PHRASES.length;
                animationHandler.postDelayed(this, 1000);
            }
        };
        animationHandler.post(animationRunnable);
    }

    private void stopLoadingAnimation() {
        if (animationRunnable != null) animationHandler.removeCallbacks(animationRunnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopLoadingAnimation();
    }
}