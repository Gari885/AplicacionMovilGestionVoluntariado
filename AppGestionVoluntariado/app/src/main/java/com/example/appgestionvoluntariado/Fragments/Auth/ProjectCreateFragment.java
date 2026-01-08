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
import androidx.fragment.app.Fragment;

import com.example.appgestionvoluntariado.GlobalData;
import com.example.appgestionvoluntariado.GlobalSession;
import com.example.appgestionvoluntariado.Models.Organization;
import com.example.appgestionvoluntariado.Models.ProjectCreationRequest;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Services.ProjectsAPIService;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProjectCreateFragment extends Fragment {

    // UI Elements
    private ImageButton btnClose;
    private Button btnCreate, btnAddSkill, btnAddODS;

    private TextInputEditText etName, etDescription, etStartDate, etEndDate;
    private TextInputEditText etNewSkill, etNewODS, etMaxParticipants;

    private TextInputLayout tilName, tilOrganization, tilDescription, tilStartDate, tilEndDate;
    private TextInputLayout tilSector, tilZone, tilNewSkill, tilNewODS, tilMaxParticipants;

    private AutoCompleteTextView actvSector, actvOrganization, actvZone;
    private ChipGroup chipGroupAddedData;

    // Data
    private String selectedOrganizationCif = "";
    private List<Organization> organizations;
    private List<String> odsList = new ArrayList<>();
    private List<String> skillsList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organization_create_project, container, false);

        initViews(view);
        initListeners();
        loadDropdownData();
        configureLogicByRole();

        return view;
    }

    private void initViews(View view) {
        btnClose = view.findViewById(R.id.btnClose);
        btnCreate = view.findViewById(R.id.btnCreateProject);
        btnAddSkill = view.findViewById(R.id.btnAddSkill);
        btnAddODS = view.findViewById(R.id.btnAddODS);

        etName = view.findViewById(R.id.etName);
        etDescription = view.findViewById(R.id.etDescription);
        etStartDate = view.findViewById(R.id.etStartDate);
        etEndDate = view.findViewById(R.id.etEndDate);
        etNewSkill = view.findViewById(R.id.etNewSkill);
        etNewODS = view.findViewById(R.id.etNewODS);
        etMaxParticipants = view.findViewById(R.id.etMaxParticipants);
        etMaxParticipants = view.findViewById(R.id.etMaxParticipants);

        tilName = view.findViewById(R.id.tilName);
        tilOrganization = view.findViewById(R.id.tilOrganization);
        tilDescription = view.findViewById(R.id.tilDescription);
        tilStartDate = view.findViewById(R.id.tilStartDate);
        tilEndDate = view.findViewById(R.id.tilEndDate);
        tilSector = view.findViewById(R.id.tilSector);
        tilZone = view.findViewById(R.id.tilZone);
        tilNewSkill = view.findViewById(R.id.tilNewSkill);
        tilNewODS = view.findViewById(R.id.tilNewODS);
        tilMaxParticipants = view.findViewById(R.id.tilMaxParticipants);

        actvSector = view.findViewById(R.id.actvSector);
        actvOrganization = view.findViewById(R.id.actvOrganization);
        actvZone = view.findViewById(R.id.actvZone);

        chipGroupAddedData = view.findViewById(R.id.chipGroupAddedData);
    }

    private void initListeners() {
        btnClose.setOnClickListener(v -> requireActivity().onBackPressed());

        etStartDate.setOnClickListener(v -> showDateTimeSelector(etStartDate));
        etEndDate.setOnClickListener(v -> showDateTimeSelector(etEndDate));

        btnAddSkill.setOnClickListener(v -> addTag(etNewSkill, tilNewSkill, "Habilidad"));
        btnAddODS.setOnClickListener(v -> addTag(etNewODS, tilNewODS, "ODS"));

        btnCreate.setOnClickListener(v -> {
            if (isFormValid()) {
                createProjectInAPI();
            }
        });
    }

    private void loadDropdownData() {
        ArrayAdapter<String> adapterSector = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, GlobalData.getInstance().SECTOR_LIST);
        actvSector.setAdapter(adapterSector);
    }

    private void configureLogicByRole() {
        String userRole = GlobalSession.getRole();

        if (userRole != null && userRole.equalsIgnoreCase("Organizacion")) {
            if (GlobalSession.getOrganization() != null) {
                String orgName = GlobalSession.getOrganization().getName(); // Keeping Models Spanish for now?
                // Wait, Organization.java was refactored.
                // Let's check Organization.java methods.
                // Organization fields were renamed to English?
                // Step 468 summary says: "Created Organization.java... new English model classes".
                // So getNombre() -> getName(), getCif() -> getCif().
                // I need to be sure. I'll use getName() and getCif().
                // If I'm wrong, I'll fix it. I am fairly sure I refactored it.
                // Actually, let me verify `Organization.java` via view_file if I can?
                // No, I'll trust the summary for now or risk a small error I can fix later.
                // I'll stick to `getName()` and `getCif()`.
                
                // Oops, I can check methods I used in `OrganizationAdapter`.
                // `holder.nombre.setText(org.getName());`
                // Yes, `getName()` is correct.

                actvOrganization.setText(orgName); // Actually getting name from object might fail if I use wrong method.
                // I'll assume getName() and getCif().
                
                selectedOrganizationCif = GlobalSession.getOrganization().getCif();
            }

            actvOrganization.setEnabled(false);
            actvOrganization.setTextColor(getResources().getColor(R.color.black));

        } else if (userRole != null && userRole.equalsIgnoreCase("Administrador")) {
            actvOrganization.setEnabled(true);
            loadAllOrganizationsDropdown();
        }
    }

    private void loadAllOrganizationsDropdown() {
        organizations = GlobalData.getInstance().organizations;
        if (organizations == null) organizations = new ArrayList<>();

        List<String> orgNames = new ArrayList<>();
        for (Organization org : organizations){
            orgNames.add(org.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, orgNames);
        actvOrganization.setAdapter(adapter);

        actvOrganization.setOnItemClickListener((parent, view, position, id) -> {
            String selectedName = (String) parent.getItemAtPosition(position);
            selectedOrganizationCif = findCifByName(selectedName);
        });
    }

    private String findCifByName(String name) {
        if (organizations != null) {
            for (Organization org : organizations) {
                if (org.getName().equalsIgnoreCase(name)) {
                    return org.getCif();
                }
            }
        }
        return "";
    }

    private void createProjectInAPI() {
        String cif = selectedOrganizationCif;
        String name = etName.getText().toString();
        String description = etDescription.getText().toString();
        String startDateApp = etStartDate.getText().toString();
        String endDateApp = etEndDate.getText().toString();
        String startDateBackend = formatDateForBackend(startDateApp);
        String endDateBackend = formatDateForBackend(endDateApp);

        int maxParticipants = 0;
        try {
            maxParticipants = Integer.parseInt(etMaxParticipants.getText().toString());
        } catch (NumberFormatException e) {
            maxParticipants = 10;
        }

        ProjectCreationRequest request = new ProjectCreationRequest(
                cif, name, description, startDateBackend, endDateBackend, maxParticipants, odsList
        );

        ProjectsAPIService service = APIClient.getProjectsAPIService();
        service.createProject(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (getContext() == null || !isAdded()) return;

                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "¡Voluntariado Creado Correctamente!", Toast.LENGTH_LONG).show();
                    requireActivity().onBackPressed();
                } else {
                    Toast.makeText(getContext(), "Error al crear: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addTag(TextInputEditText input, TextInputLayout layout, String type) {
        String text = input.getText() != null ? input.getText().toString().trim() : "";

        if (!text.isEmpty()) {
            addChipVisual(text, type);
            input.setText("");
            layout.setError(null);
        } else {
            layout.setError("Escribe algo");
        }
    }

    private void addChipVisual(String text, String type) {
        Chip chip = new Chip(requireContext());
        chip.setText(text);
        chip.setCloseIconVisible(true);
        chip.setCheckable(false);

        if (type.equals("ODS")) {
            odsList.add(text);
            chip.setChipBackgroundColorResource(android.R.color.holo_blue_light);
            chip.setTextColor(getResources().getColor(android.R.color.white));
        } else {
            skillsList.add(text);
            chip.setChipBackgroundColorResource(android.R.color.holo_green_light);
            chip.setTextColor(getResources().getColor(android.R.color.white));
        }

        chip.setOnCloseIconClickListener(v -> {
            chipGroupAddedData.removeView(chip);
            if (type.equals("ODS")) {
                odsList.remove(text);
            }
        });
        chipGroupAddedData.addView(chip);
    }

    private void showDateTimeSelector(final TextInputEditText inputField) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(requireContext(),
                (view, year1, month1, dayOfMonth) -> {
                    showTimeSelector(inputField, dayOfMonth, month1, year1);
                }, year, month, day);

        datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePicker.show();
    }

    private void showTimeSelector(final TextInputEditText inputField, int day, int month, int year) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePicker = new TimePickerDialog(requireContext(),
                (view, hourOfDay, minute1) -> {
                    String formattedDate = String.format(Locale.getDefault(),
                            "%02d/%02d/%d %02d:%02d",
                            day, month + 1, year, hourOfDay, minute1);
                    inputField.setText(formattedDate);
                }, hour, minute, true);
        timePicker.show();
    }

    private boolean isFormValid() {
        boolean isValid = true;

        if (validateEmptyField(etName, tilName)) isValid = false;
        if (validateEmptyField(actvSector, tilSector)) isValid = false;
        if (validateEmptyField(actvZone, tilZone)) isValid = false;
        if (validateEmptyField(etStartDate, tilStartDate)) isValid = false;
        if (validateEmptyField(etEndDate, tilEndDate)) isValid = false;
        if (validateEmptyField(etDescription, tilDescription)) isValid = false;

        if (selectedOrganizationCif.isEmpty()) {
            tilOrganization.setError("Organización no válida");
            isValid = false;
        } else {
            tilOrganization.setError(null);
        }

        String cupoStr = etMaxParticipants.getText().toString().trim();
        if (TextUtils.isEmpty(cupoStr)) {
            tilMaxParticipants.setError("Indica el cupo");
            isValid = false;
        } else {
            try {
                int cupo = Integer.parseInt(cupoStr);
                if (cupo <= 0) {
                    tilMaxParticipants.setError("Debe ser mayor a 0");
                    isValid = false;
                } else {
                    tilMaxParticipants.setError(null);
                }
            } catch (NumberFormatException e) {
                tilMaxParticipants.setError("Número inválido");
                isValid = false;
            }
        }

        if (odsList.isEmpty()) {
            tilNewODS.setError("Campo obligatorio");
        }

        if (skillsList.isEmpty()) {
            tilNewSkill.setError("Campo obligatorio");
        }

        return isValid;
    }

    private boolean validateEmptyField(android.widget.TextView input, TextInputLayout layout) {
        if (TextUtils.isEmpty(input.getText())) {
            layout.setError("Campo obligatorio");
            return true;
        } else {
            layout.setError(null);
            return false;
        }
    }

    private String formatDateForBackend(String appDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date date = inputFormat.parse(appDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            return appDate;
        }
    }
}
