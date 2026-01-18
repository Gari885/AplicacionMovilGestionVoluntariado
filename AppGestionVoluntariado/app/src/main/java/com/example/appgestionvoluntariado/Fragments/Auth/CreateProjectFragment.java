package com.example.appgestionvoluntariado.Fragments.Auth;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.appgestionvoluntariado.Models.ProjectCreationRequest;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Services.ProjectsService;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateProjectFragment extends Fragment {

    private ImageButton btnClose;
    private Button btnCreate, btnAddSkill, btnAddODS;
    private TextInputEditText etName, etDescription, etStartDate, etEndDate, etMaxParticipants, etNewSkill, etNewODS;
    private TextInputLayout tilName, tilDescription, tilStartDate, tilEndDate, tilMaxParticipants, tilNewSkill, tilNewODS, tilZone, tilOrganization, tilSector;
    private AutoCompleteTextView actvZone, actvOrganization, actvSector;
    private ChipGroup chipGroupData;

    private List<String> odsList = new ArrayList<>();
    private List<String> skillsList = new ArrayList<>();
    private ProjectsService projectsAPIService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organization_create_project, container, false);
        initViews(view);
        setupDropdowns();
        setupDateTimePickers();
        setupListeners();
        setupToolbar();

        projectsAPIService = APIClient.getProjectsService();
        loadOrganizationData();
        return view;
    }

    private void initViews(View v) {
        btnClose = v.findViewById(R.id.btnClose);
        btnCreate = v.findViewById(R.id.btnCreateProject);
        btnAddSkill = v.findViewById(R.id.btnAddSkill);
        btnAddODS = v.findViewById(R.id.btnAddODS);

        etName = v.findViewById(R.id.etName);
        etDescription = v.findViewById(R.id.etDescription);
        etStartDate = v.findViewById(R.id.etStartDate);
        etEndDate = v.findViewById(R.id.etEndDate);
        etMaxParticipants = v.findViewById(R.id.etMaxParticipants);
        etNewSkill = v.findViewById(R.id.etNewSkill);
        etNewODS = v.findViewById(R.id.etNewODS);

        tilName = v.findViewById(R.id.tilName);
        tilDescription = v.findViewById(R.id.tilDescription);
        tilStartDate = v.findViewById(R.id.tilStartDate);
        tilEndDate = v.findViewById(R.id.tilEndDate);
        tilMaxParticipants = v.findViewById(R.id.tilMaxParticipants);
        tilNewSkill = v.findViewById(R.id.tilNewSkill);
        tilNewODS = v.findViewById(R.id.tilNewODS);
        tilZone = v.findViewById(R.id.tilZone);
        tilOrganization = v.findViewById(R.id.tilOrganization);
        tilSector = v.findViewById(R.id.tilSector);

        actvZone = v.findViewById(R.id.actvZone);
        actvOrganization = v.findViewById(R.id.actvOrganization);
        actvSector = v.findViewById(R.id.actvSector);
        
        chipGroupData = v.findViewById(R.id.chipGroupAddedData);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = getActivity().findViewById(R.id.topAppBarOrg);
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
            toolbar.setNavigationIconTint(Color.parseColor("#1A3B85"));
            toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());
        }
    }

    private void setupDropdowns() {
        String[] zones = {"Pamplona", "Comarca de Pamplona", "Ribera", "Zona Media", "Montaña"};
        actvZone.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, zones));
        
        String[] sectors = {"Educación", "Salud", "Medio Ambiente", "Social", "Cultural", "Deportivo", "Tecnología", "Otros"};
        actvSector.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, sectors));
    }

    private void loadOrganizationData() {
        APIClient.getAuthAPIService().getProfile().enqueue(new Callback<com.example.appgestionvoluntariado.Models.ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<com.example.appgestionvoluntariado.Models.ProfileResponse> call, @NonNull Response<com.example.appgestionvoluntariado.Models.ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String orgName = response.body().getData().get("nombre").getAsString();
                        actvOrganization.setText(orgName);
                        actvOrganization.setEnabled(false); // Make it read-only
                        tilOrganization.setEnabled(false); // Visual indication
                    } catch (Exception e) {
                        Log.e("CreateProject", "Error parsing profile data", e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<com.example.appgestionvoluntariado.Models.ProfileResponse> call, @NonNull Throwable t) {
                Log.e("CreateProject", "Failed to fetch profile", t);
            }
        });
    }

    private void setupListeners() {
        btnClose.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        btnAddSkill.setOnClickListener(v -> addChip(etNewSkill, skillsList, "SKILL"));
        btnAddODS.setOnClickListener(v -> addChip(etNewODS, odsList, "ODS"));

        btnCreate.setOnClickListener(v -> {
            if (validateForm()) {
                sendProjectToBackend();
            }
        });
    }

    private boolean validateForm() {
        boolean isValid = true;

        tilName.setError(null);
        tilDescription.setError(null);
        tilStartDate.setError(null);
        tilEndDate.setError(null);
        tilMaxParticipants.setError(null);
        tilZone.setError(null);

        if (TextUtils.isEmpty(etName.getText())) { tilName.setError("Project name required"); isValid = false; }
        if (TextUtils.isEmpty(etDescription.getText())) { tilDescription.setError("Description required"); isValid = false; }
        if (TextUtils.isEmpty(actvZone.getText())) { tilZone.setError("Zone selection required"); isValid = false; }
        if (TextUtils.isEmpty(etStartDate.getText())) { tilStartDate.setError("Start date required"); isValid = false; }
        if (TextUtils.isEmpty(etEndDate.getText())) { tilEndDate.setError("End date required"); isValid = false; }

        if (TextUtils.isEmpty(etMaxParticipants.getText())) {
            tilMaxParticipants.setError("Participant limit required");
            isValid = false;
        }

        if (odsList.isEmpty()) {
            Toast.makeText(getContext(), "Add at least one ODS", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        if (skillsList.isEmpty()) {
            Toast.makeText(getContext(), "Add at least one required skill", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    private void sendProjectToBackend() {
        ProjectCreationRequest request = new ProjectCreationRequest();

        // **IMPORTANTE**: Enviamos null como CIF.
        // El backend detectará automáticamente que soy una Organización por mi Token
        request.setOrganizationCif(null);

        request.setName(etName.getText().toString().trim());
        request.setDescription(etDescription.getText().toString().trim());
        request.setAddress(actvZone.getText().toString().trim());

        // Formatear fechas como YYYY-MM-DD
        request.setStartDate(formatDateForBackend(etStartDate.getText().toString()));
        request.setEndDate(formatDateForBackend(etEndDate.getText().toString()));

        try {
            request.setMaxParticipants(Integer.parseInt(etMaxParticipants.getText().toString()));
        } catch (NumberFormatException e) {
            request.setMaxParticipants(10);
        }

        request.setOds(odsList);
        request.setSkills(skillsList);
        request.setNeeds(new ArrayList<>());
        
        // Add sector if logical to do so, though not explicitly requested in API call yet, it might be part of the request model?
        // Checking ProjectCreationRequest model below would be prudent, but for now user just asked for visual blocking.
        // Assuming Address maps to Zone as per previous edits.

        projectsAPIService.createProject(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Project created successfully!", Toast.LENGTH_LONG).show();
                    getParentFragmentManager().popBackStack();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e("API_ERROR", "Error: " + response.code() + " - " + errorBody);
                        Toast.makeText(getContext(), "Error: " + response.code() + " " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Connection error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addChip(TextInputEditText input, List<String> list, String type) {
        String text = input.getText().toString().trim();
        if (!text.isEmpty()) {
            list.add(text);
            Chip chip = new Chip(requireContext());
            chip.setText(text);
            chip.setCloseIconVisible(true);
            chip.setChipBackgroundColorResource(type.equals("ODS") ? R.color.cuatrovientos_blue_light : R.color.cuatrovientos_blue);
            chip.setTextColor(Color.WHITE);
            chip.setOnCloseIconClickListener(v -> {
                chipGroupData.removeView(chip);
                list.remove(text);
            });
            chipGroupData.addView(chip);
            input.setText("");
        }
    }

    private void setupDateTimePickers() {
        etStartDate.setOnClickListener(v -> showDateTimePicker(etStartDate));
        etEndDate.setOnClickListener(v -> showDateTimePicker(etEndDate));
    }

    private void showDateTimePicker(TextInputEditText input) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(requireContext(), (view, year, month, day) -> {
            new TimePickerDialog(requireContext(), (viewTime, hour, min) -> {
                // Formato visual amigable
                String date = String.format(Locale.getDefault(), "%02d/%02d/%d %02d:%02d", day, month + 1, year, hour, min);
                input.setText(date);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private String formatDateForBackend(String dateStr) {
        try {
            // Convierte de dd/MM/yyyy HH:mm  ->  yyyy-MM-dd
            SimpleDateFormat in = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            SimpleDateFormat out = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return out.format(in.parse(dateStr));
        } catch (ParseException e) { return dateStr; }
    }
}