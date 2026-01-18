package com.example.appgestionvoluntariado.Fragments.Volunteer;

import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.appgestionvoluntariado.Models.*;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Services.AuthAPIService;
import com.example.appgestionvoluntariado.Services.VolunteerService;
import com.example.appgestionvoluntariado.Utils.CategoryManager;
import com.example.appgestionvoluntariado.Utils.StatusHelper;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VolunteerProfileEditDataFragment extends Fragment {

    // UI Components from XML [cite: 2026-01-18]
    private TextInputEditText etFullName, etDni, etZone, etBirthday;
    private AutoCompleteTextView acLanguages, acExperience, acCycle, acCar;
    private MaterialButton btnSave, btnBack, btnSelectSkills, btnSelectInterests;
    private ChipGroup cgOds, cgSkills, cgInterests;
    private View loadingOverlay;

    // Data & Services
    private AuthAPIService authAPIService;
    private VolunteerService volunteerService;
    private Volunteer currentVolunteer;

    private final List<Skill> masterSkillsList = new ArrayList<>();
    private final List<Interest> masterInterestsList = new ArrayList<>();

    private final List<String> selectedSkills = new ArrayList<>();
    private final List<String> selectedInterests = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_volunteer_edit_data, container, false);
        initViews(view);
        setupSpinners();
        setupListeners();
        fetchData();
        return view;
    }

    private void initViews(View v) {
        // Mapping all IDs from your XML [cite: 2026-01-18]
        etFullName = v.findViewById(R.id.etEditFullName);
        etDni = v.findViewById(R.id.etEditDni);
        etZone = v.findViewById(R.id.etEditZone);
        etBirthday = v.findViewById(R.id.etEditBirthday);
        acLanguages = v.findViewById(R.id.etEditLanguages);
        acExperience = v.findViewById(R.id.etEditExperience);
        acCycle = v.findViewById(R.id.etEditCycle);
        acCar = v.findViewById(R.id.etEditCar);

        btnBack = v.findViewById(R.id.btnBack);
        btnSave = v.findViewById(R.id.btnSaveFullData);
        btnSelectSkills = v.findViewById(R.id.btnEditSelectSkills);
        btnSelectInterests = v.findViewById(R.id.btnEditSelectInterests);

        cgSkills = v.findViewById(R.id.cgEditSelectedSkills);
        cgInterests = v.findViewById(R.id.cgEditSelectedInterests);
        loadingOverlay = v.findViewById(R.id.loadingOverlay);

        authAPIService = APIClient.getAuthAPIService();
        volunteerService = APIClient.getVolunteerService();
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        etBirthday.setOnClickListener(v -> showDatePicker());
        btnSave.setOnClickListener(v -> { if (validateForm()) saveData(); });

        // Multi-select listeners [cite: 2026-01-18]
        btnSelectSkills.setOnClickListener(v -> openMultiSelectSheet("Editar Habilidades", masterSkillsList, selectedSkills, cgSkills));
        btnSelectInterests.setOnClickListener(v -> openMultiSelectSheet("Editar Intereses", masterInterestsList, selectedInterests, cgInterests));
    }

    private void setupSpinners() {
        String[] cycles = {"ASIR", "DAM", "DAW", "SMR", "Marketing", "Administración"};
        String[] exp = {"Sin experiencia", "Menos de 1 año", "1-3 años", "Más de 3 años"};
        String[] langs = {"Castellano", "Euskera", "Inglés", "Francés", "Otros"};
        String[] car = {"Sí", "No"};

        acCycle.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, cycles));
        acExperience.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, exp));
        acLanguages.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, langs));
        acCar.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, car));
    }

    private void fetchData() {
        if (loadingOverlay != null) loadingOverlay.setVisibility(View.VISIBLE);

        // Load Categories first [cite: 2026-01-18, 2026-01-17]
        new CategoryManager().fetchAllCategories(
                new CategoryManager.CategoryCallback<Ods>() { @Override public void onSuccess(List<Ods> d) {} @Override public void onError(String e) {} },
                new CategoryManager.CategoryCallback<Skill>() { @Override public void onSuccess(List<Skill> d) { masterSkillsList.clear(); masterSkillsList.addAll(d); } @Override public void onError(String e) {} },
                new CategoryManager.CategoryCallback<Interest>() { @Override public void onSuccess(List<Interest> d) { masterInterestsList.clear(); masterInterestsList.addAll(d); } @Override public void onError(String e) {} },
                new CategoryManager.CategoryCallback<Need>() { @Override public void onSuccess(List<Need> d) {} @Override public void onError(String e) {} }
        );

        authAPIService.getProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (loadingOverlay != null) loadingOverlay.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    currentVolunteer = new Gson().fromJson(response.body().getData(), Volunteer.class);
                    fillFields(currentVolunteer);
                }
            }
            @Override public void onFailure(Call<ProfileResponse> call, Throwable t) { if (loadingOverlay != null) loadingOverlay.setVisibility(View.GONE); }
        });
    }

    private void fillFields(Volunteer v) {
        etFullName.setText(v.getFirstName());
        etDni.setText(v.getDni());
        etZone.setText(v.getZone());
        etBirthday.setText(v.getBirthDate());
        acCycle.setText(v.getCycle(), false);
        acExperience.setText(v.getExperience(), false);
        acCar.setText(v.getHasCar() ? "Sí" : "No", false);

        if (v.getLanguages() != null) acLanguages.setText(String.join(", ", v.getLanguages()), false);

        // Sync and draw chips [cite: 2026-01-18]
        selectedSkills.clear(); if(v.getSkills() != null) selectedSkills.addAll(v.getSkills());
        selectedInterests.clear(); if(v.getInterests() != null) selectedInterests.addAll(v.getInterests());

        refreshChips(cgSkills, selectedSkills);
        refreshChips(cgInterests, selectedInterests);
    }

    private void saveData() {
        if (currentVolunteer == null) return;
        if (loadingOverlay != null) loadingOverlay.setVisibility(View.VISIBLE);

        currentVolunteer.setFirstName(etFullName.getText().toString());
        currentVolunteer.setZone(etZone.getText().toString());
        currentVolunteer.setBirthDate(etBirthday.getText().toString());
        currentVolunteer.setCycle(acCycle.getText().toString());
        currentVolunteer.setExperience(acExperience.getText().toString());
        currentVolunteer.setHasCar(acCar.getText().toString().equals("Sí"));
        currentVolunteer.setSkills(selectedSkills);
        currentVolunteer.setInterests(selectedInterests);

        volunteerService.editProfile(currentVolunteer).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (loadingOverlay != null) loadingOverlay.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                }else {
                    Toast.makeText(getContext(), "No se ha podido actualizar el perfil", Toast.LENGTH_SHORT).show();
                }

                if (!response.isSuccessful()) {
                    try {
                        Log.e("API_ERROR", "Error body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override public void onFailure(Call<Void> call, Throwable t) { if (loadingOverlay != null) loadingOverlay.setVisibility(View.GONE); }
        });
    }

    private boolean validateForm() {
        if (etFullName.getText().toString().isEmpty() || etZone.getText().toString().isEmpty() ||
                etBirthday.getText().toString().isEmpty() || acCycle.getText().toString().isEmpty()) {
            StatusHelper.showStatus(getContext(), "Campos incompletos", "Nombre, zona, nacimiento y ciclo son obligatorios", true);
            return false;
        }
        return true;
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(requireContext(), (view, y, m, d) -> {
            String date = String.format(Locale.getDefault(), "%d-%02d-%02d", y, (m + 1), d);
            etBirthday.setText(date);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private <T> void openMultiSelectSheet(String title, List<T> data, List<String> selection, ChipGroup group) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View sheet = getLayoutInflater().inflate(R.layout.layout_selector_sheet, null);

        TextView tvTitle = sheet.findViewById(R.id.tvSheetTitle);
        EditText etSearch = sheet.findViewById(R.id.etSearchSheet);
        ChipGroup cgItems = sheet.findViewById(R.id.cgAllItems);
        Button btnConfirm = sheet.findViewById(R.id.btnConfirmSelection);

        tvTitle.setText(title);
        for (T item : data) {
            String name = (item instanceof Ods) ? ((Ods) item).getName() :
                    (item instanceof Skill) ? ((Skill) item).getName() : ((Interest) item).getName();
            Chip chip = new Chip(requireContext());
            chip.setText(name);
            chip.setCheckable(true);
            chip.setChecked(selection.contains(name));
            cgItems.addView(chip);
        }

        btnConfirm.setOnClickListener(v -> {
            selection.clear();
            for (int i = 0; i < cgItems.getChildCount(); i++) {
                Chip c = (Chip) cgItems.getChildAt(i);
                if (c.isChecked()) selection.add(c.getText().toString());
            }
            refreshChips(group, selection);
            dialog.dismiss();
        });
        dialog.setContentView(sheet);
        dialog.show();
    }

    private void refreshChips(ChipGroup group, List<String> list) {
        group.removeAllViews();
        for (String s : list) {
            Chip chip = new Chip(requireContext());
            chip.setText(s);
            chip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#F0F4FF")));
            chip.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#1A3B85")));
            chip.setChipStrokeWidth(2f);
            chip.setTextColor(Color.parseColor("#1A3B85"));
            group.addView(chip);
        }
    }
}