package com.example.appgestionvoluntariado.Fragments.Admin;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (currentStatus.equalsIgnoreCase("aceptada")){
            view = ViewMode.ADMINISTRATOR_ACCEPTED;
            updateTabUI(tabAccepted,tabPending);
        }
    }

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
        setupSearch();

        loadProjects(); // Initial load

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
            currentStatus = "aceptada";
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
                            StatusHelper.showToast(getContext(), "Admin proyectos: " + fullList.size(), false); // Debug
                            updateAdapter(fullList);
                        } else {
                            StatusHelper.showToast(getContext(), "Error Admin: " + response.code(), true);
                        }
                    }
                    @Override
                    public void onFailure(Call<List<Project>> call, Throwable t) {
                        loadingLayout.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateAdapter(List<Project> list) {
        if (adapter == null) {
            adapter = new ProjectAdapter(list, new ProjectAdapter.OnProjectActionListener() {
                @Override
                public void onAccept(Project p) {
                    updateStatus(p.getActivityId(), "aceptada");
                }

                @Override
                public void onReject(Project p) {
                    updateStatus(p.getActivityId(), "rechazada");
                }

                @Override
                public void onDelete(Project p) {
                    updateStatus(p.getActivityId(), "cancelado");
                }

                @Override
                public void onApply(Project project) {

                }

                @Override
                public void onEdit(Project project) {
                    editProject(project);
                }
            }, view);

            rvProjects.setAdapter(adapter);
        } else {
            rvProjects.setAdapter(adapter);
            adapter.notifyAdapterAdmin(list, view);
        }
    }

    private void updateStatus(int projectId, String status) {
        loadingLayout.setVisibility(View.VISIBLE);

        // Loguea qué estás enviando
        Log.d("DEBUG_STATE", "Enviando -> ID: " + projectId + ", Status: " + status);

        APIClient.getProjectsService().changeState(projectId, new StatusRequest(status))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        loadingLayout.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null) {
                            try {
                                // Leemos el JSON crudo del backend
                                String rawJson = response.body().string();
                                Log.d("DEBUG_STATE", "Respuesta Servidor (200 OK): " + rawJson);

                                // Aquí verás algo como: {"campo_actualizado": "estado", "valor_nuevo": "CANCELADO"}
                                // Si dice "valor_nuevo": "PENDIENTE", ¡ahí está tu problema!

                                StatusHelper.showToast(getContext(), "Estado actualizado", false);
                                loadProjects();

                            } catch (IOException e) {
                                Log.e("DEBUG_STATE", "Error al leer respuesta", e);
                            }
                        } else {
                            // Error del servidor (400, 404, 500...)
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                                Log.e("DEBUG_STATE", "Error " + response.code() + ": " + errorBody);
                                StatusHelper.showToast(getContext(), "Error: " + response.code(), true);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        loadingLayout.setVisibility(View.GONE);
                        Log.e("DEBUG_STATE", "Fallo de conexión", t);
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

    private void editProject(Project project) {
        CreateProjectFragment fragment = new CreateProjectFragment();
        Bundle args = new Bundle();
        args.putBoolean("is_edit_mode", true);
        args.putInt("project_id", project.getActivityId());
        args.putString("project_name", project.getName());
        args.putString("project_description", project.getName()); // Reusing name as description for now
        args.putString("project_address", project.getAddress());
        args.putString("project_start_date", project.getStartDate());
        args.putString("project_end_date", project.getEndDate());
        args.putInt("project_participants", project.getMaxParticipants());

        ArrayList<String> odsNames = new ArrayList<>();
        if(project.getOdsList() != null) for(com.example.appgestionvoluntariado.Models.Ods o : project.getOdsList()) odsNames.add(o.getName());
        args.putStringArrayList("project_ods", odsNames);

        ArrayList<String> skillNames = new ArrayList<>();
        if(project.getSkillsList() != null) for(com.example.appgestionvoluntariado.Models.Skill s : project.getSkillsList()) skillNames.add(s.getName());
        args.putStringArrayList("project_skills", skillNames);

        fragment.setArguments(args);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.admin_fragment_container, fragment)
                .addToBackStack(null)
                .commit();
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