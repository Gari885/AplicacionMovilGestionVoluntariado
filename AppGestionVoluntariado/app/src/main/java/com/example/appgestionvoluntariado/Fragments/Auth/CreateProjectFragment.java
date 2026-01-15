package com.example.appgestionvoluntariado.Fragments.Auth;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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
    private TextInputLayout tilName, tilDescription, tilStartDate, tilEndDate, tilMaxParticipants, tilNewSkill, tilNewODS;
    private AutoCompleteTextView actvZone;
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

        projectsAPIService = APIClient.getProjectsService();

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

        actvZone = v.findViewById(R.id.actvZone);
        chipGroupData = v.findViewById(R.id.chipGroupAddedData);
    }

    private void setupDropdowns() {
        String[] zones = {"Pamplona", "Comarca de Pamplona", "Ribera", "Zona Media", "Montaña"};
        actvZone.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, zones));
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

    private void sendProjectToBackend() {
        // Creamos el objeto Project con los datos del formulario
        ProjectCreationRequest request = new ProjectCreationRequest();
        request.setTitle(etName.getText().toString());
        request.setDescription(etDescription.getText().toString());
        request.setAddress(actvZone.getText().toString());
        request.setStartDate(formatDateForBackend(etStartDate.getText().toString()));
        request.setEndDate(formatDateForBackend(etEndDate.getText().toString()));
        request.setMaxParticipants(Integer.parseInt(etMaxParticipants.getText().toString()));
        // Suponiendo que tu modelo Project acepta List<String> para ODS y Habilidades
        // newProject.setOds(odsList);
        // newProject.setRequiredSkills(skillsList);

        projectsAPIService.createProject(newProject).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "¡Proyecto enviado para revisión!", Toast.LENGTH_LONG).show();
                    getParentFragmentManager().popBackStack();
                } else {
                    Toast.makeText(getContext(), "Error al crear el proyecto", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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

            // Colores Cuatrovientos
            if (type.equals("ODS")) {
                chip.setChipBackgroundColorResource(android.R.color.holo_blue_dark);
            } else {
                chip.setChipBackgroundColorResource(android.R.color.darker_gray);
            }
            chip.setTextColor(getResources().getColor(android.R.color.white));

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
                String date = String.format(Locale.getDefault(), "%02d/%02d/%d %02d:%02d", day, month + 1, year, hour, min);
                input.setText(date);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private boolean validateForm() {
        boolean valid = true;
        if (TextUtils.isEmpty(etName.getText())) { tilName.setError("Obligatorio"); valid = false; }
        if (TextUtils.isEmpty(etStartDate.getText())) { tilStartDate.setError("Obligatorio"); valid = false; }
        if (TextUtils.isEmpty(etEndDate.getText())) { tilEndDate.setError("Obligatorio"); valid = false; }
        if (odsList.isEmpty()) { Toast.makeText(getContext(), "Añade al menos un ODS", Toast.LENGTH_SHORT).show(); valid = false; }
        return valid;
    }

    private String formatDateForBackend(String dateStr) {
        try {
            SimpleDateFormat in = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            SimpleDateFormat out = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            return out.format(in.parse(dateStr));
        } catch (ParseException e) { return dateStr; }
    }
}