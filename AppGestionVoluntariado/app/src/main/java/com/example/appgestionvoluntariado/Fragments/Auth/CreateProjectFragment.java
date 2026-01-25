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

    private List<String> odsList = new ArrayList<>();
    private List<String> skillsList = new ArrayList<>();
    private ProjectsService projectsAPIService;

    // NEW: Store selected organization for Admins
    private Organization selectedOrganization = null;
    private List<Organization> loadedOrganizations = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organization_create_project, container, false);
        initViews(view);
        setupNavigation(); // Arreglamos el botón atrás [cite: 2026-01-20]
        setupDateTimePickers();
        setupListeners();

        projectsAPIService = APIClient.getProjectsService();

        loadDynamicCategories(); // Carga ODS y Skills desde la API
        loadOrganizationData();
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
        APIClient.getAuthAPIService().getProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getType().equalsIgnoreCase("admin")) {
                        // ADMIN: Load organizations to select
                        actvOrganization.setEnabled(true);
                        tilOrganization.setEnabled(true);
                        loadOrganizations();
                    }else if (response.body().getType().equalsIgnoreCase("organizacion")) {
                        try {
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
                Log.e("CreateProject", "Failed to fetch profile", t);
                checkAllLoaded();
            }
        });
    }

    private void loadOrganizations() {
        APIClient.getOrganizationService().getOrganizations("aprobado").enqueue(new Callback<List<Organization>>() {
            @Override
            public void onResponse(Call<List<Organization>> call, Response<List<Organization>> response) {
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
        if (TextUtils.isEmpty(etStartDate.getText())) { tilStartDate.setError("Campo requerido"); isValid = false; }
        if (TextUtils.isEmpty(etEndDate.getText())) { tilEndDate.setError("Campo requerido"); isValid = false; }

        // Validate Organization for Admin
        if (actvOrganization.isEnabled() && selectedOrganization == null) {
            tilOrganization.setError("Selecciona una organización");
            isValid = false;
        }

        if (TextUtils.isEmpty(etMaxParticipants.getText())) {
            tilMaxParticipants.setError("Campo requerido");
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

        // NEW: Add Organization CIF if Admin selected one
        if (selectedOrganization != null) {
            request.setOrganizationCif(selectedOrganization.getCif());
        }

        projectsAPIService.createProject(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                toggleLoading(false);
                if (response.isSuccessful()) {
                    loadingText.setText("Creando proyecto...");
                    StatusHelper.showToast(getContext(), "Actividad creada con éxito", false);
                    
                    // Notify listeners to refresh
                    Bundle result = new Bundle();
                    result.putBoolean("refresh", true);
                    getParentFragmentManager().setFragmentResult("project_created", result);

                    getParentFragmentManager().popBackStack();
                }else {
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
                toggleLoading(false);
            }
        });
    }

    private void loadDynamicCategories() {
        toggleLoading(true);
        CategoryManager cm = new CategoryManager();

        cm.fetchAllCategories(
                new CategoryManager.CategoryCallback<Ods>() {
                    @Override
                    public void onSuccess(List<Ods> data) {
                        masterODSList.addAll(data);
                        // IMPORTANTE: El objeto Ods debe tener un toString() que devuelva el nombre
                        ArrayAdapter<Ods> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, masterODSList);
                        actvODS.setAdapter(adapter);
                        checkAllLoaded();
                    }
                    @Override public void onError(String e) { checkAllLoaded(); }
                },
                new CategoryManager.CategoryCallback<Skill>() {
                    @Override
                    public void onSuccess(List<Skill> data) {
                        masterSkillsList.addAll(data);
                        ArrayAdapter<Skill> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, masterSkillsList);
                        actvSkill.setAdapter(adapter);
                        checkAllLoaded();
                    }
                    @Override public void onError(String e) { checkAllLoaded(); }
                },
                // ... (resto de callbacks vacíos o necesarios)
                null, null, null
        );
    }


    private void checkAllLoaded() {
        loadedCount++;
        if (loadedCount >= 4) toggleLoading(false);
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

    private void addChipFromAutoComplete(AutoCompleteTextView actv, List<String> list, String type) {
        // 1. Obtenemos el texto del AutoComplete [cite: 2026-01-20]
        String text = actv.getText().toString().trim();

        // 2. Validamos que no esté vacío y que no se haya añadido ya [cite: 2026-01-20]
        if (!text.isEmpty() && !list.contains(text)) {

            // Añadimos a la lista que se enviará a la API [cite: 2026-01-20]
            list.add(text);

            // 3. Creamos el Chip visualmente [cite: 2026-01-20]
            Chip chip = new Chip(requireContext());
            chip.setText(text);
            chip.setCloseIconVisible(true);
            chip.setCheckable(false);
            chip.setClickable(false);

            // Estilo basado en el tipo (Azul oscuro para Skills, Azul claro para ODS) [cite: 2026-01-16]
            if (type.equals("ODS")) {
                chip.setChipBackgroundColorResource(R.color.cuatrovientos_blue_light);
                chip.setTextColor(Color.BLACK);
            } else {
                chip.setChipBackgroundColorResource(R.color.cuatrovientos_blue);
                chip.setTextColor(Color.WHITE);
                chip.setCloseIconTintResource(android.R.color.white);
            }

            // 4. Lógica para eliminar el Chip y el dato de la lista [cite: 2026-01-20]
            chip.setOnCloseIconClickListener(v -> {
                chipGroupData.removeView(chip);
                list.remove(text);
            });

            // 5. Lo añadimos al contenedor y limpiamos el selector [cite: 2026-01-20]
            chipGroupData.addView(chip);
            actv.setText("");
        } else if (list.contains(text)) {
            StatusHelper.showToast(getContext(), "Este elemento ya ha sido añadido", true);
            actv.setText("");
        }
    }
}
