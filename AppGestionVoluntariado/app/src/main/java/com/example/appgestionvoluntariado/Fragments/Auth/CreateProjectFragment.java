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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.appgestionvoluntariado.Models.Ods;
import com.example.appgestionvoluntariado.Models.Organization;
import com.example.appgestionvoluntariado.Models.Project;
import com.example.appgestionvoluntariado.Models.Request.ProjectCreationRequest;
import com.example.appgestionvoluntariado.Models.Response.ProfileResponse;
import com.example.appgestionvoluntariado.Models.Skill;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Services.ProjectsService;
import com.example.appgestionvoluntariado.Utils.CategoryManager;
import com.example.appgestionvoluntariado.Utils.FormData;
import com.example.appgestionvoluntariado.Utils.StatusHelper;
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

    private Button btnCreate, btnAddSkill, btnAddODS;
    private TextInputEditText etName, etDescription, etStartDate, etEndDate, etMaxParticipants, etNewSkill, etNewODS;
    private TextInputLayout tilName, tilDescription, tilStartDate, tilEndDate, tilMaxParticipants, tilNewSkill, tilNewODS, tilZone, tilOrganization, tilSector;
    private AutoCompleteTextView actvZone, actvOrganization, actvSector, actvSkill, actvODS;
    private ChipGroup chipGroupData;

    // Call management
    private List<Call<?>> activeCalls = new ArrayList<>();

    private TextView loadingText;

    private ProgressBar pbLoading;
    private final List<Skill> masterSkillsList = new ArrayList<>();
    private final List<Ods> masterODSList = new ArrayList<>();

    private int loadedCount = 0;

    private String bottonText;


    // Views de carga
    private View loadingOverlay;
    private ImageView ivLogoSpinner;
    private Animation rotateAnim;

    private MaterialToolbar topAppBar;
    private TextView tvFormTitle;

    private int totalCalls = 0;

    private List<String> odsList = new ArrayList<>();
    private List<String> skillsList = new ArrayList<>();
    private ProjectsService projectsAPIService;

    // NEW: Store selected organization for Admins
    private Organization selectedOrganization = null;
    private List<Organization> loadedOrganizations = new ArrayList<>();

    private Project projectToEdit;
    private boolean isEditMode = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organization_create_project, container, false);
        initViews(view);
        setupNavigation(); // Arreglamos el botón atrás [cite: 2026-01-20]
        setupDateTimePickers();
        setupListeners();

        projectsAPIService = APIClient.getProjectsService();
        loadOrganizationData();
        loadDynamicCategories(); // Carga ODS y Skills desde la API

        // Check for edit mode
        if (getArguments() != null) {
            projectToEdit = (Project) getArguments().getSerializable("project");
            if (projectToEdit != null) {
                isEditMode = true;
                setupEditMode();
            }
        }
        return view;
    }

    private void initViews(View v) {
        btnCreate = v.findViewById(R.id.btnCreateProject);
        btnAddSkill = v.findViewById(R.id.btnAddSkill);
        btnAddODS = v.findViewById(R.id.btnAddODS);
        loadingText = v.findViewById(R.id.tvLoadingText);

        etName = v.findViewById(R.id.etName);
        etDescription = v.findViewById(R.id.etDescription);
        etStartDate = v.findViewById(R.id.etStartDate);
        etEndDate = v.findViewById(R.id.etEndDate);
        etMaxParticipants = v.findViewById(R.id.etMaxParticipants);

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

        actvODS = v.findViewById(R.id.actvODS);
        actvSkill = v.findViewById(R.id.actvSkill);
        actvZone = v.findViewById(R.id.actvZone);
        actvOrganization = v.findViewById(R.id.actvOrganization);
        actvSector = v.findViewById(R.id.actvSector);
        topAppBar = v.findViewById(R.id.topAppBar);
        tvFormTitle = v.findViewById(R.id.tvFormTitle);
        chipGroupData = v.findViewById(R.id.chipGroupAddedData);
        pbLoading = v.findViewById(R.id.pbLoginLoading);

        // Loading y Animación [cite: 2026-01-20]
        loadingOverlay = v.findViewById(R.id.layoutLoading);
        ivLogoSpinner = v.findViewById(R.id.ivLogoSpinner);
        rotateAnim = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_infinite);

        actvZone.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, FormData.ZONES_LIST));
        actvSector.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, FormData.SECTORS_LIST));

    }

    private void setupNavigation() {
        // Arreglo del botón atrás para que funcione en cualquier contexto [cite: 2026-01-20]
        topAppBar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Cancel all pending calls
        for (Call<?> call : activeCalls) {
            if (!call.isCanceled()) {
                call.cancel();
            }
        }
        activeCalls.clear();
    }

    private void toggleLoading(boolean show) {
        if (show) {
            loadingOverlay.setVisibility(View.VISIBLE);
            ivLogoSpinner.startAnimation(rotateAnim);
        } else {
            ivLogoSpinner.clearAnimation();
            loadingOverlay.setVisibility(View.GONE);
        }
    }


    private void loadOrganizationData() {
        Call<ProfileResponse> call = APIClient.getAuthAPIService().getProfile();
        activeCalls.add(call);
        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getType().equalsIgnoreCase("admin")) {
                        // ADMIN: Load organizations to select
                        actvOrganization.setEnabled(true);
                        tilOrganization.setEnabled(true);
                        totalCalls = 4;
                        loadOrganizations();
                    }else if (response.body().getType().equalsIgnoreCase("organizacion")) {
                        try {
                            totalCalls = 3;
                            // ORG: Pre-fill and disable
                            String orgName = response.body().getData().get("nombre").getAsString();
                            actvOrganization.setText(orgName);
                            actvOrganization.setEnabled(false); // Make it read-only
                            tilOrganization.setEnabled(false); // Visual indication

                        } catch (Exception e) {
                            Log.e("CreateProject", "Error parsing profile data", e);
                        }
                    }
                    checkAllLoaded();

                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                Log.e("CreateProject", "Failed to fetch profile", t);
                checkAllLoaded();
            }
        });
    }

    private void loadOrganizations() {
        Call<List<Organization>> call = APIClient.getOrganizationService().getOrganizations("aprobado");
        activeCalls.add(call);
        call.enqueue(new Callback<List<Organization>>() {
            @Override
            public void onResponse(Call<List<Organization>> call, Response<List<Organization>> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    loadedOrganizations = response.body();
                    // NEW: Use custom layout or toString() override in Organization model
                    ArrayAdapter<Organization> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, loadedOrganizations);
                    actvOrganization.setAdapter(adapter);

                    // Handle selection event
                    actvOrganization.setOnItemClickListener((parent, view, position, id) -> {
                        selectedOrganization = (Organization) parent.getItemAtPosition(position);
                    });

                    checkAllLoaded();
                }
            }

            @Override
            public void onFailure(Call<List<Organization>> call, Throwable t) {
                if (!isAdded()) return;
                checkAllLoaded();
            }
        });
    }

    private void setupListeners() {

        topAppBar.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        btnAddSkill.setOnClickListener(v -> addChipFromAutoComplete(actvSkill, skillsList, "SKILL"));
        btnAddODS.setOnClickListener(v -> addChipFromAutoComplete(actvODS, odsList, "ODS"));

        btnCreate.setOnClickListener(v -> {
            if (validateForm()) {
                toggleLoading(true);
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
        tilOrganization.setError(null);

        if (TextUtils.isEmpty(etName.getText())) { tilName.setError("Campo requerido"); isValid = false; }
        if (TextUtils.isEmpty(etDescription.getText())) { tilDescription.setError("Campo requerido"); isValid = false; }
        if (TextUtils.isEmpty(actvZone.getText())) { tilZone.setError("Campo requerido"); isValid = false; }

        // Start Date
        if (TextUtils.isEmpty(etStartDate.getText())) { 
            tilStartDate.setError("Campo requerido"); 
            isValid = false; 
        }

        // End Date & Date Logic
        if (TextUtils.isEmpty(etEndDate.getText())) { 
            tilEndDate.setError("Campo requerido"); 
            isValid = false; 
        } else if (!TextUtils.isEmpty(etStartDate.getText())) {
             // Compare dates if both are present
             try {
                 SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                 Calendar start = Calendar.getInstance();
                 start.setTime(sdf.parse(etStartDate.getText().toString()));
                 Calendar end = Calendar.getInstance();
                 end.setTime(sdf.parse(etEndDate.getText().toString()));
                 
                 if (end.before(start)) {
                     tilEndDate.setError("La fecha fin no puede ser anterior a inicio");
                     isValid = false;
                 }
             } catch (ParseException e) {
                 e.printStackTrace();
             }
        }

        // Participants > 0
        String participantsStr = etMaxParticipants.getText().toString();
        if (TextUtils.isEmpty(participantsStr)) {
            tilMaxParticipants.setError("Campo requerido");
            isValid = false;
        } else {
            try {
                int participants = Integer.parseInt(participantsStr);
                if (participants <= 0) {
                    tilMaxParticipants.setError("Debe ser mayor que 0");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                tilMaxParticipants.setError("Número inválido");
                isValid = false;
            }
        }

        // Organization check
        if (tilOrganization.isEnabled() && TextUtils.isEmpty(actvOrganization.getText())) {
            tilOrganization.setError("Campo requerido");
            isValid = false;
        }

        if (odsList.isEmpty()) {
            StatusHelper.showToast(getContext(), "Añade almenos un ODS", true);
            isValid = false;
        }
        if (skillsList.isEmpty()) {
            StatusHelper.showToast(getContext(), "Añade almenos una habilidad", true);
            isValid = false;
        }

        if (!isValid) StatusHelper.showToast(getContext(), "Por favor corrige los errores", true);

        return isValid;
    }

    private void sendProjectToBackend() {
        toggleLoading(true);
        ProjectCreationRequest request = new ProjectCreationRequest();

        request.setName(etName.getText().toString().trim());
        request.setDescription(etDescription.getText().toString().trim());
        request.setAddress(actvZone.getText().toString().trim());
        request.setStartDate(formatDateForBackend(etStartDate.getText().toString()));
        request.setEndDate(formatDateForBackend(etEndDate.getText().toString()));
        request.setOds(odsList);
        request.setMaxParticipants(Integer.parseInt(etMaxParticipants.getText().toString()));
        request.setSkills(skillsList);
        request.setSector(actvSector.getText().toString());

        // NEW: Add Organization CIF if Admin selected one
        if (selectedOrganization != null) {
            request.setOrganizationCif(selectedOrganization.getCif());
        }

        if (isEditMode) {
             Call<Void> call = projectsAPIService.updateProject(projectToEdit.getActivityId(), request);
             activeCalls.add(call);
             call.enqueue(new Callback<Void>() {
                 @Override
                 public void onResponse(Call<Void> call, Response<Void> response) {
                     if (!isAdded()) return;
                     toggleLoading(false);
                     if (response.isSuccessful()) {
                         StatusHelper.showToast(getContext(), "Proyecto actualizado con éxito", false);
                          getParentFragmentManager().setFragmentResult("project_created", new Bundle()); // Refresh list
                         getParentFragmentManager().popBackStack();
                     } else {
                         StatusHelper.showToast(getContext(), "Error al actualizar", true);
                     }
                 }

                 @Override
                 public void onFailure(Call<Void> call, Throwable t) {
                     if (!isAdded()) return;
                     toggleLoading(false);
                     StatusHelper.showToast(getContext(), "Fallo de red", true);
                 }
             });
        } else {
            Call<Void> call = projectsAPIService.createProject(request);
            activeCalls.add(call);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (!isAdded()) return;
                    toggleLoading(false);
                    if (response.isSuccessful()) {
                        loadingText.setText("Creando proyecto...");
                        StatusHelper.showToast(getContext(), "Actividad creada con éxito", false);

                        // Notify listeners to refresh
                        Bundle result = new Bundle();
                        result.putBoolean("refresh", true);
                        getParentFragmentManager().setFragmentResult("project_created", result);

                        getParentFragmentManager().popBackStack();
                    } else {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e("CreateProject", "Error: " + errorBody);
                            StatusHelper.showToast(getContext(), "Error: " + response.message(), true);
                        } catch (IOException e) {
                            StatusHelper.showToast(getContext(), "No se ha podido crear la actividad", true);
                        }
                    }


                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    if (!isAdded()) return;
                    toggleLoading(false);
                }
            });
        }
    }

    private void setupEditMode() {


        if (projectToEdit == null) return;

        topAppBar.setTitle("Editar Proyecto");
        if(tvFormTitle != null) tvFormTitle.setText("Editar Proyecto");
        btnCreate.setText("GUARDAR CAMBIOS");

        String sector = projectToEdit.getSector();
        actvSector.setText(sector, false);


        etName.setText(projectToEdit.getName());

        etDescription.setText(projectToEdit.getName()); // Provisional reuse since I don't see description field in Project.java

        actvZone.setText(projectToEdit.getAddress(), false); // false to disable filtering
        
        // Format dates for UI: yyyy-MM-dd -> dd/MM/yyyy HH:mm (or just dd/MM/yyyy)
        etStartDate.setText(formatDateForUI(projectToEdit.getStartDate()));
        etEndDate.setText(formatDateForUI(projectToEdit.getEndDate()));
        
        etMaxParticipants.setText(String.valueOf(projectToEdit.getMaxParticipants()));

        // Lists
        if (projectToEdit.getOdsList() != null) {
            for (Ods o : projectToEdit.getOdsList()) {
                 odsList.add(o.getName());
                 addChipToGroup(chipGroupData, o.getName(), "ODS", true);
            }
        }
        if (projectToEdit.getSkillsList() != null) {
             for (com.example.appgestionvoluntariado.Models.Skill s : projectToEdit.getSkillsList()) {
                 skillsList.add(s.getName());
                 addChipToGroup(chipGroupData, s.getName(), "SKILL", true);
             }
        }
    }

    private void loadDynamicCategories() {
        toggleLoading(true);
        CategoryManager cm = new CategoryManager();

        cm.fetchAllCategories(
                new CategoryManager.CategoryCallback<Ods>() {
                    @Override
                    public void onSuccess(List<Ods> data) {
                        if (!isAdded() || getContext() == null) return;
                        masterODSList.addAll(data);
                        // IMPORTANTE: El objeto Ods debe tener un toString() que devuelva el nombre
                        ArrayAdapter<Ods> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, masterODSList);
                        actvODS.setAdapter(adapter);
                        checkAllLoaded();
                    }
                    @Override public void onError(String e) {
                        if (!isAdded()) return;
                        checkAllLoaded();
                    }
                },
                new CategoryManager.CategoryCallback<Skill>() {
                    @Override
                    public void onSuccess(List<Skill> data) {
                        if (!isAdded() || getContext() == null) return;
                        masterSkillsList.addAll(data);
                        ArrayAdapter<Skill> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, masterSkillsList);
                        actvSkill.setAdapter(adapter);
                        checkAllLoaded();
                    }
                    @Override public void onError(String e) {
                        if (!isAdded()) return;
                        checkAllLoaded();
                    }
                },
                // ... (resto de callbacks vacíos o necesarios)
                null, null, null
        );
    }


    private void checkAllLoaded() {
        loadedCount++;
        if (loadedCount >= totalCalls) toggleLoading(false);
    }



    private void setupDateTimePickers() {
        etStartDate.setOnClickListener(v -> showDateTimePicker(etStartDate));
        etEndDate.setOnClickListener(v -> showDateTimePicker(etEndDate));
    }

    private void showDateTimePicker(TextInputEditText input)  {
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

    // Reuse chip creation logic but separated for programmatic adding
    private void addChipToGroup(ChipGroup group, String text, String type, boolean isInteractive) {
        Chip chip = new Chip(requireContext());
        chip.setText(text);
        chip.setCloseIconVisible(true);
        chip.setCheckable(false);
        chip.setClickable(false);

        if (type.equals("ODS")) {
            chip.setChipBackgroundColorResource(R.color.cuatrovientos_blue_light);
            chip.setTextColor(Color.BLACK);
        } else {
            chip.setChipBackgroundColorResource(R.color.cuatrovientos_blue);
            chip.setTextColor(Color.WHITE);
            chip.setCloseIconTintResource(android.R.color.white);
        }

        chip.setOnCloseIconClickListener(v -> {
            group.removeView(chip);
            if(type.equals("ODS")) odsList.remove(text);
            else skillsList.remove(text);
        });

        group.addView(chip);
    }

    private void addChipFromAutoComplete(AutoCompleteTextView actv, List<String> list, String type) {
        // 1. Obtenemos el texto del AutoComplete [cite: 2026-01-20]
        String text = actv.getText().toString().trim();

        // 2. Validamos que no esté vacío y que no se haya añadido ya [cite: 2026-01-20]
        if (!text.isEmpty() && !list.contains(text)) {

            // Añadimos a la lista que se enviará a la API [cite: 2026-01-20]
            list.add(text);

            // 3. Creamos el Chip visualmente [cite: 2026-01-20]
            addChipToGroup(chipGroupData, text, type, true);

            // 5. Lo añadimos al contenedor y limpiamos el selector [cite: 2026-01-20]
            actv.setText("");
        } else if (list.contains(text)) {
            StatusHelper.showToast(getContext(), "Este elemento ya ha sido añadido", true);
            actv.setText("");
        }
    }

    private String formatDateForUI(String dateStr) {
        if (dateStr == null) return "";
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy 00:00", Locale.getDefault());
            return outputFormat.format(inputFormat.parse(dateStr));
        } catch (ParseException e) {
            return dateStr; 
        }
    }
}
