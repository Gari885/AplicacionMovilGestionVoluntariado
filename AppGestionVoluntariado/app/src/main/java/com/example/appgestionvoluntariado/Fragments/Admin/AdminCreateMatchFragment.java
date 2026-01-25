package com.example.appgestionvoluntariado.Fragments.Admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.example.appgestionvoluntariado.Models.Project;
import com.example.appgestionvoluntariado.Models.Request.VolunteerEnrollmentRequest;
import com.example.appgestionvoluntariado.Models.UI.SpinnerProjectItem;
import com.example.appgestionvoluntariado.Models.UI.SpinnerVolunteerItem;
import com.example.appgestionvoluntariado.Models.Volunteer;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Services.AdminService;
import com.example.appgestionvoluntariado.Utils.StatusHelper;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminCreateMatchFragment extends Fragment {

    private Spinner spnVol, spnProject;
    private Button btnCreateMatch;
    private AdminService adminService;
    private List<Volunteer> volunteers = new ArrayList<>();
    private List<Project> projects = new ArrayList<>();
    private FrameLayout loadingBar;

    private MaterialToolbar topAppBar;

    private ProgressBar progressBar;
    private final int TOTAL_CALLS = 2;
    private int finishedCalls = 0;

    private String textMatchButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_create_match, container, false);

        initViews(view);
        setUpListeners(view);
        loadData();

        return view;
    }
    private void initViews(View v) {
        spnVol = v.findViewById(R.id.spinnerVolunteer);
        spnProject = v.findViewById(R.id.spinnerActivity);
        loadingBar = v.findViewById(R.id.loadingOverlay);
        progressBar = v.findViewById(R.id.pbMatchesLoading);

        btnCreateMatch = v.findViewById(R.id.btnCreateMatch);
        textMatchButton = (String) btnCreateMatch.getText();
        topAppBar = v.findViewById(R.id.topAppBar);
        btnCreateMatch.setEnabled(false);
    }
    private void setUpListeners(View v) {

        adminService = APIClient.getAdminService();

        btnCreateMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCreateMatch.setText("");
                progressBar.setVisibility(View.VISIBLE);
                saveMatch();
            }
        });
        topAppBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });
    }
    private void setUpProjectsSpinner() {
        List <SpinnerProjectItem> projectItems = new ArrayList<>();
        //Load volunteers data
        for (Project p : projects) {
            projectItems.add(new SpinnerProjectItem(p.getName(),p.getActivityId()));
        }
        //Prepare the array
        ArrayAdapter<SpinnerProjectItem> items = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item,
                projectItems
        );
        items.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Set data
        spnProject.setAdapter(items);
    }
    private void setUpVolunteersSpinner() {
        List <SpinnerVolunteerItem> volunteerItems = new ArrayList<>();
        //Load volunteers data
        for (Volunteer v : volunteers) {
            volunteerItems.add(new SpinnerVolunteerItem(v.getFullName(),v.getDni()));
        }
        //Prepare the array
        ArrayAdapter<SpinnerVolunteerItem> items = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item,
                volunteerItems
        );
        items.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Set data
        spnVol.setAdapter(items);
    }
    private boolean verifySpinners() {
        return spnProject.getSelectedItem() != null && spnVol.getSelectedItem() != null;
    }
    private void loadData() {
        loadingBar.setVisibility(View.VISIBLE);
        loadVolunteers();
        loadProjects();
    }
    private void loadProjects() {
        adminService.getProjects("").enqueue(new Callback<List<Project>>() {
            @Override
            public void onResponse(Call<List<Project>> call, Response<List<Project>> response) {
                if (response.isSuccessful() && response.body() != null){
                    projects = response.body();
                    setUpProjectsSpinner();
                    checkIfLoadingDone();
                }
            }

            @Override
            public void onFailure(Call<List<Project>> call, Throwable t) {

            }
        });
    }
    private void loadVolunteers() {
        adminService.getVolunteers("").enqueue(new Callback<List<Volunteer>>() {
            @Override
            public void onResponse(Call<List<Volunteer>> call, Response<List<Volunteer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    volunteers = response.body();
                    setUpVolunteersSpinner();
                    checkIfLoadingDone();
                }
            }

            @Override
            public void onFailure(Call<List<Volunteer>> call, Throwable t) {

            }
        });
    }
    private synchronized void checkIfLoadingDone() {
        finishedCalls++;
        if (finishedCalls == TOTAL_CALLS) {
            loadingBar.setVisibility(ViewGroup.GONE);
            btnCreateMatch.setEnabled(true);
        }
    }
    private void saveMatch() {
        if (verifySpinners()) {
            saveDataIntoAPI();
        }else{
            StatusHelper.showStatus(getContext(), "Error", "Tienes que elegir un voluntario y una actividad.", true);
        }
    }
    private void saveDataIntoAPI() {
        //Get spinners item to retrive data
        SpinnerProjectItem selectedProject = (SpinnerProjectItem) spnProject.getSelectedItem();
        SpinnerVolunteerItem selectedVolunteer = (SpinnerVolunteerItem) spnVol.getSelectedItem();

        //Get the necesary data
        int projectId = selectedProject.getId();
        VolunteerEnrollmentRequest request = new VolunteerEnrollmentRequest(selectedVolunteer.getDni());

        adminService.enrollVolunteer(projectId, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()){
                    StatusHelper.showStatus(getContext(), "Éxito", "Match creado correctamente.", false);
                    defaultButton();
                }else {
                    StatusHelper.showStatus(getContext(), "Error", "El match no se ha podido crear.", true);
                    defaultButton();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                StatusHelper.showStatus(getContext(), "Error", "Fallo del servidor.", true);
                defaultButton();
            }
        });
    }

    private void defaultButton() {
        btnCreateMatch.setText(textMatchButton);
        progressBar.setVisibility(View.INVISIBLE);
    }


}
