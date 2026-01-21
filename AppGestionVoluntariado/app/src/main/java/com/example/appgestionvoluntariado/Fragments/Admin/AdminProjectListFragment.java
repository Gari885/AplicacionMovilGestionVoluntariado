package com.example.appgestionvoluntariado.Fragments.Admin;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgestionvoluntariado.Adapters.ProjectAdapter;
import com.example.appgestionvoluntariado.Fragments.Auth.CreateProjectFragment;
import com.example.appgestionvoluntariado.Models.Project;
import com.example.appgestionvoluntariado.Models.Request.StatusRequest;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.ViewMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminProjectListFragment extends Fragment {

    private RecyclerView rvProjects;
    private ProjectAdapter adapter;
    private View loadingLayout;
    private TextView tabPending, tabAccepted;
    private EditText etSearch;
    private ImageView logoSpinner;
    private Animation rotateAnimation;
    private ViewMode view;

    private List<Project> fullList = new ArrayList<>();
    private String currentStatus = "pendiente";

    private FloatingActionButton fabAddProject;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_admin_project_list, container, false);

        rvProjects = v.findViewById(R.id.rvProjects);
        rvProjects.setLayoutManager(new LinearLayoutManager(getContext()));

        loadingLayout = v.findViewById(R.id.layoutLoading);
        tabPending = v.findViewById(R.id.tabStatusPending);
        tabAccepted = v.findViewById(R.id.tabStatusAccepted);
        etSearch = v.findViewById(R.id.etSearchProject);
        fabAddProject = v.findViewById(R.id.fabAddProject);
        fabAddProject.setVisibility(View.INVISIBLE);
        view = ViewMode.ADMINISTRATOR_PENDING;
        logoSpinner = v.findViewById(R.id.ivLogoSpinner);
        rotateAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_infinite);
        logoSpinner.startAnimation(rotateAnimation);

        setupTabs();
        setupSearch();
        loadProjects();

        return v;
    }

    private void setupTabs() {
        tabPending.setOnClickListener(v -> {
            currentStatus = "pendiente";
            view = ViewMode.ADMINISTRATOR_PENDING;
            updateTabUI(tabPending, tabAccepted);
            loadProjects();
        });

        tabAccepted.setOnClickListener(v -> {
            currentStatus = "aprobado";
            view = ViewMode.ADMINISTRATOR_ACCEPTED;
            updateTabUI(tabAccepted, tabPending);
            loadProjects();
        });


        fabAddProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.admin_fragment_container, new CreateProjectFragment())
                        .addToBackStack(null).commit();
            }
        });
    }

    private void updateTabUI(TextView selected, TextView unselected) {
        selected.setBackgroundResource(R.drawable.background_tab_selected);
        selected.setTextColor(Color.WHITE);
        unselected.setBackgroundResource(R.drawable.background_tab_unselected);
        unselected.setTextColor(Color.parseColor("#1A3B85"));
    }

    private void loadProjects() {
        loadingLayout.setVisibility(View.VISIBLE);
        APIClient.getAdminService().getProjects(currentStatus)
                .enqueue(new Callback<List<Project>>() {
                    @Override
                    public void onResponse(Call<List<Project>> call, Response<List<Project>> response) {
                        loadingLayout.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            fabAddProject.setVisibility(View.VISIBLE);
                            fullList = response.body();
                            updateAdapter(fullList);
                        }
                    }
                    @Override
                    public void onFailure(Call<List<Project>> call, Throwable t) {
                        loadingLayout.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateAdapter(List<Project> list) {
        if (adapter == null) {
            adapter = new ProjectAdapter(list, new ProjectAdapter.OnProjectActionListener() {
                @Override
                public void onAccept(Project p) {
                    updateStatus(p.getActivityId(), "aprobado");
                }

                @Override
                public void onReject(Project p) {
                    updateStatus(p.getActivityId(), "rechazado");
                }

                @Override
                public void onDelete(Project p) {
                    updateStatus(p.getActivityId(), "rechazado");
                }

                @Override
                public void onApply(Project project) {

                }
            }, view);

            rvProjects.setAdapter(adapter);
        } else {
            adapter.notifyAdapterAdmin(list,view);
        }
    }

    private void updateStatus(int projectId, String status) {
        loadingLayout.setVisibility(View.VISIBLE);
        APIClient.getProjectsService().changeState(projectId, new StatusRequest(status))
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) loadProjects();
                        else loadingLayout.setVisibility(View.GONE);
                    }
                    @Override public void onFailure(Call<Void> call, Throwable t) {
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
        for (Project p : fullList) {
            if (p.getName().toLowerCase().contains(query)) {
                filtered.add(p);
            }
        }
        updateAdapter(filtered);
    }
}