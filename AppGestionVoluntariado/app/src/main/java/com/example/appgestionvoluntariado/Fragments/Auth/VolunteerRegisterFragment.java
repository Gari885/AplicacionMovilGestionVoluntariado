package com.example.appgestionvoluntariado.Fragments.Auth;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.appgestionvoluntariado.Models.*;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Utils.CategoryManager;
import com.example.appgestionvoluntariado.Utils.GlobalData;
import com.example.appgestionvoluntariado.Utils.StatusHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VolunteerRegisterFragment extends Fragment {

    // UI Components
    private Button btnRegister, btnAddAvailability, btnAddLanguage;
    private TextInputEditText etName, etEmail, etDni, etPassword, etBirthDate;
    private TextInputLayout tilName, tilEmail, tilDni, tilPassword, tilFechaNac;
    private AutoCompleteTextView actvLanguages, actvExperience, actvCar, actvCycle, actvZona, actvDays, actvTimeSlots;
    private ChipGroup chipGroupSkills, chipGroupInterests, chipGroupOds, chipGroupSummary;

    // Firebase & Data Lists
    private FirebaseAuth mAuth;
    private final List<String> selectedSkills = new ArrayList<>();
    private final List<String> selectedInterests = new ArrayList<>();
    private final List<String> selectedAvailability = new ArrayList<>();
    private final List<String> selectedOds = new ArrayList<>();
    private final List<String> selectedLanguages = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_volunteer_register, container, false);
        mAuth = FirebaseAuth.getInstance();

        initViews(view);
        setupToolbar();
        loadDynamicCategories();
        loadStaticListData();
        setupListeners();

        return view;
    }

    private void initViews(View view) {
        // Buttons
        btnRegister = view.findViewById(R.id.btnRegistrar);
        btnAddAvailability = view.findViewById(R.id.btnAddAvailability);
        btnAddLanguage = view.findViewById(R.id.btnAddLanguage);

        // EditTexts
        etName = view.findViewById(R.id.etNombre);
        etEmail = view.findViewById(R.id.etCorreo);
        etDni = view.findViewById(R.id.etDni);
        etPassword = view.findViewById(R.id.etPassword);
        etBirthDate = view.findViewById(R.id.etFechaNac);

        // Layouts for Errors
        tilName = view.findViewById(R.id.tilNombre);
        tilEmail = view.findViewById(R.id.tilCorreo);
        tilDni = view.findViewById(R.id.tilDni);
        tilPassword = view.findViewById(R.id.tilPassword);
        tilFechaNac = view.findViewById(R.id.tilFechaNac);

        // Dropdowns
        actvLanguages = view.findViewById(R.id.actvIdiomas);
        actvExperience = view.findViewById(R.id.actvExperiencia);
        actvCar = view.findViewById(R.id.actvCoche);
        actvCycle = view.findViewById(R.id.actvCiclo);
        actvZona = view.findViewById(R.id.actvZona);
        actvDays = view.findViewById(R.id.actvDays);
        actvTimeSlots = view.findViewById(R.id.actvTimeSlots);

        // ChipGroups
        chipGroupSkills = view.findViewById(R.id.chipGroupHabilidades);
        chipGroupInterests = view.findViewById(R.id.chipGroupIntereses);
        chipGroupOds = view.findViewById(R.id.chipGroupOds);
        chipGroupSummary = view.findViewById(R.id.chipGroupResumen);
    }

    private void setupListeners() {
        // Add Language Logic
        btnAddLanguage.setOnClickListener(v -> {
            String language = actvLanguages.getText().toString();
            if (!language.isEmpty() && !selectedLanguages.contains(language)) {
                selectedLanguages.add(language);
                addSummaryChip(language, "LANGUAGE", selectedLanguages, null);
                actvLanguages.setText("");
            }
        });

        // Add Availability Logic
        btnAddAvailability.setOnClickListener(v -> {
            String day = actvDays.getText().toString();
            String slot = actvTimeSlots.getText().toString();

            if (!day.isEmpty() && !slot.isEmpty()) {
                String combined = day + " (" + slot + ")";
                if (!selectedAvailability.contains(combined)) {
                    selectedAvailability.add(combined);
                    addSummaryChip(combined, "AVAILABILITY", selectedAvailability, null);
                    actvDays.setText("");
                    actvTimeSlots.setText("");
                } else {
                    StatusHelper.showStatus(getContext(), "Atención", "Esta disponibilidad ya ha sido añadida", true);
                }
            } else {
                StatusHelper.showStatus(getContext(), "Faltan datos", "Selecciona día y franja horaria", true);
            }
        });

        btnRegister.setOnClickListener(v -> {
            if (validateForm()) {
                performFirebaseRegistration();
            }
        });
    }

    private void loadDynamicCategories() {
        CategoryManager manager = new CategoryManager();
        manager.fetchAllCategories(
                new CategoryManager.CategoryCallback<Ods>() {
                    @Override public void onSuccess(List<Ods> data) { fillDynamicChips(chipGroupOds, data, selectedOds, "ODS"); }
                    @Override public void onError(String error) { StatusHelper.showStatus(getContext(), "Error", "Fallo al cargar ODS", true); }
                },
                new CategoryManager.CategoryCallback<Skill>() {
                    @Override public void onSuccess(List<Skill> data) { fillDynamicChips(chipGroupSkills, data, selectedSkills, "SKILL"); }
                    @Override public void onError(String error) { }
                },
                new CategoryManager.CategoryCallback<Interest>() {
                    @Override public void onSuccess(List<Interest> data) { fillDynamicChips(chipGroupInterests, data, selectedInterests, "INTEREST"); }
                    @Override public void onError(String error) { }
                },
                new CategoryManager.CategoryCallback<Need>() {
                    @Override public void onSuccess(List<Need> data) { }
                    @Override public void onError(String error) { }
                }
        );
    }

    private <T> void fillDynamicChips(ChipGroup group, List<T> dataList, List<String> selectionList, String type) {
        if (dataList == null) return;
        for (T item : dataList) {
            String name = "";
            if (item instanceof Ods) name = ((Ods) item).getName();
            else if (item instanceof Skill) name = ((Skill) item).getName();
            else if (item instanceof Interest) name = ((Interest) item).getName();

            final String finalName = name;
            Chip chip = new Chip(requireContext());
            chip.setText(finalName);
            chip.setCheckable(true);
            chip.setOnCheckedChangeListener((v, isChecked) -> {
                if (isChecked) {
                    selectionList.add(finalName);
                    group.removeView(chip);
                    addSummaryChip(finalName, type, selectionList, group);
                }
            });
            group.addView(chip);
        }
    }

    private void addSummaryChip(String text, String type, List<String> list, @Nullable ChipGroup originGroup) {
        Chip chip = new Chip(requireContext());
        chip.setText(text);
        chip.setCloseIconVisible(true);
        chip.setTextColor(Color.WHITE);
        chip.setChipBackgroundColorResource(R.color.cuatrovientos_blue);

        chip.setOnCloseIconClickListener(v -> {
            chipGroupSummary.removeView(chip);
            list.remove(text);
            if (originGroup != null) {
                restoreChipToGroup(originGroup, text, list, type);
            }
        });
        chipGroupSummary.addView(chip);
    }

    private void restoreChipToGroup(ChipGroup group, String text, List<String> list, String type) {
        Chip chip = new Chip(requireContext());
        chip.setText(text);
        chip.setCheckable(true);
        chip.setOnCheckedChangeListener((v, isChecked) -> {
            if (isChecked) {
                list.add(text);
                group.removeView(chip);
                addSummaryChip(text, type, list, group);
            }
        });
        group.addView(chip);
    }

    private void loadStaticListData() {
        GlobalData gd = GlobalData.getInstance();
        fillDropdown(actvExperience, gd.EXPERIENCE_LIST);
        fillDropdown(actvCar, gd.CAR_LIST);
        fillDropdown(actvCycle, gd.CYCLE_LIST);
        fillDropdown(actvLanguages, gd.LANGUAGE_LIST);
        fillDropdown(actvZona, gd.ZONES_LIST); // Ensure this exists in GlobalData
        fillDropdown(actvDays, gd.DAYS_LIST);
        fillDropdown(actvTimeSlots, gd.TIME_SLOTS_LIST);
    }

    private void fillDropdown(AutoCompleteTextView actv, String[] data) {
        actv.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, data));
    }

    private boolean validateForm() {
        boolean isValid = true;
        if (TextUtils.isEmpty(getText(etName))) { tilName.setError("Nombre obligatorio"); isValid = false; } else tilName.setError(null);
        if (TextUtils.isEmpty(getText(etDni))) { tilDni.setError("DNI/NIE obligatorio"); isValid = false; } else tilDni.setError(null);

        String email = getText(etEmail);
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Correo electrónico inválido"); isValid = false;
        } else tilEmail.setError(null);

        if (getText(etPassword).length() < 6) {
            tilPassword.setError("Mínimo 6 caracteres"); isValid = false;
        } else tilPassword.setError(null);

        if (selectedSkills.isEmpty() || selectedOds.isEmpty()) {
            StatusHelper.showStatus(getContext(), "Faltan categorías", "Selecciona al menos una habilidad y un ODS", true);
            isValid = false;
        }
        return isValid;
    }

    private void performFirebaseRegistration() {
        toggleLoading(true);
        mAuth.createUserWithEmailAndPassword(getText(etEmail), getText(etPassword))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && mAuth.getCurrentUser() != null) {
                        syncWithBackendSQL(mAuth.getCurrentUser());
                    } else {
                        toggleLoading(false);
                        String error = task.getException() != null ? task.getException().getMessage() : "Error de autenticación";
                        StatusHelper.showStatus(getContext(), "Error de Registro", error, true);
                    }
                });
    }

    private void syncWithBackendSQL(FirebaseUser firebaseUser) {
        VolunteerRegisterRequest request = new VolunteerRegisterRequest(
                getText(etName), getText(etDni), firebaseUser.getEmail(), getText(etPassword),
                actvZona.getText().toString(), actvCycle.getText().toString(), formatDate(getText(etBirthDate)),
                actvExperience.getText().toString(), actvCar.getText().toString(), selectedLanguages,
                selectedSkills, selectedInterests, selectedAvailability, "Pendiente"
        );

        APIClient.getAuthAPIService().registerVolunteer(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    saveUserRoleInFirestore(firebaseUser.getUid(), firebaseUser.getEmail());
                    StatusHelper.showStatus(getContext(), "¡Bienvenido!", "Registro completado con éxito.", false);
                    firebaseUser.sendEmailVerification();
                    getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new LoginFragment()).commit();
                } else {
                    cleanUpFailedUser(firebaseUser, "Error del servidor (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                cleanUpFailedUser(firebaseUser, "Sin conexión con el servidor");
            }
        });
    }

    private void saveUserRoleInFirestore(String uid, String email) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("role", "volunteer");
        FirebaseFirestore.getInstance().collection("users").document(uid).set(userData);
    }

    private void cleanUpFailedUser(FirebaseUser user, String msg) {
        user.delete();
        toggleLoading(false);
        StatusHelper.showStatus(getContext(), "Registro Fallido", msg, true);
    }

    private String getText(TextInputEditText et) { return Objects.requireNonNull(et.getText()).toString().trim(); }
    private void toggleLoading(boolean isLoading) { btnRegister.setEnabled(!isLoading); btnRegister.setText(isLoading ? "Cargando..." : "FINALIZAR REGISTRO"); }
    private String formatDate(String dateStr) { try { String[] p = dateStr.split("/"); return p[2]+"-"+p[1]+"-"+p[0]; } catch (Exception e) { return "1900-01-01"; } }

    private void setupToolbar() {
        MaterialToolbar toolbar = getActivity().findViewById(R.id.topAppBarOrg);
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
            toolbar.setNavigationIconTint(Color.parseColor("#1A3B85"));
            toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());
        }
    }
}