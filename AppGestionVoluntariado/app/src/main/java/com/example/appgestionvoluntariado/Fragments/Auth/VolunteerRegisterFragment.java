package com.example.appgestionvoluntariado.Fragments.Auth;

import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.appgestionvoluntariado.Models.*;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Utils.CategoryManager;
import com.example.appgestionvoluntariado.Utils.StatusHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VolunteerRegisterFragment extends Fragment {

    private Button btnRegister, btnAddAvailability, btnAddLanguage, btnSelectSkills, btnSelectInterests;
    private TextInputEditText etName, etEmail, etDni, etPassword, etBirthDate;
    private TextInputLayout tilName, tilEmail, tilDni, tilPassword, tilBirthDate, tilCycle, tilZona;
    private AutoCompleteTextView actvLanguages, actvExperience, actvCar, actvCycle, actvZona, actvDays, actvTimeSlots;
    private ChipGroup cgSelectedSkills, cgSelectedInterests, cgSummary;
    private View loadingOverlay;

    private final List<Skill> masterSkillsList = new ArrayList<>();
    private final List<Interest> masterInterestsList = new ArrayList<>();

    // Selections initialized to avoid NullPointerExceptions [cite: 2026-01-18]
    private final List<String> selectedSkills = new ArrayList<>();
    private final List<String> selectedInterests = new ArrayList<>();
    private final List<String> selectedAvailability = new ArrayList<>();
    private final List<String> selectedLanguages = new ArrayList<>();

    // Static Data Lists
    private final String[] CYCLE_LIST = {"SMR", "ASIR", "DAM", "DAW", "Marketing", "Administración", "Otros"};
    private final String[] EXPERIENCE_LIST = {"Ninguna", "Menos de 1 año", "Entre 1 y 3 años", "Más de 3 años"};
    private final String[] CAR_LIST = {"Sí", "No"};
    private final String[] LANGUAGE_LIST = {"Castellano", "Inglés", "Francés", "Euskera", "Alemán", "Otros"};
    private final String[] DAYS_LIST = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo", "Lunes a Viernes", "Fines de semana"};
    private final String[] TIME_SLOTS_LIST = {"Mañana", "Tarde", "Noche"};
    private final String[] ZONES_LIST = {
            "Casco Viejo", "Ensanche", "Iturrama", "San Juan / Donibane", "Mendebaldea / Ermitagaña",
            "Milagrosa / Arrosadia", "Azpilagaña", "Chantrea / Txantrea", "Rochapea / Arrotxapea",
            "San Jorge / Sanduzelai", "Buztintxuri", "Mendillorri", "Lezkairu", "Erripagaña",
            "Ansoáin / Antsoain", "Barañáin", "Burlada / Burlata", "Villava / Atarrabia",
            "Zizur Mayor / Zizur Nagusia", "Mutilva / Mutiloa", "Sarriguren"
    };

    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_volunteer_register, container, false);
        mAuth = FirebaseAuth.getInstance();

        initViews(view);
        setupToolbar(view);
        loadStaticListData();
        loadDynamicCategories();
        setupListeners();

        return view;
    }

    private void initViews(View v) {
        btnRegister = v.findViewById(R.id.btnRegister);
        btnAddAvailability = v.findViewById(R.id.btnAddAvailability);
        btnAddLanguage = v.findViewById(R.id.btnAddLanguage);
        btnSelectSkills = v.findViewById(R.id.btnSelectSkills);
        btnSelectInterests = v.findViewById(R.id.btnSelectInterests);

        cgSelectedSkills = v.findViewById(R.id.cgSelectedSkills);
        cgSelectedInterests = v.findViewById(R.id.cgSelectedInterests);
        cgSummary = v.findViewById(R.id.cgSummary);

        etName = v.findViewById(R.id.etName);
        etEmail = v.findViewById(R.id.etEmail);
        etDni = v.findViewById(R.id.etDni);
        etPassword = v.findViewById(R.id.etPassword);
        etBirthDate = v.findViewById(R.id.etBirthDate);

        tilName = v.findViewById(R.id.tilName);
        tilEmail = v.findViewById(R.id.tilEmail);
        tilDni = v.findViewById(R.id.tilDni);
        tilPassword = v.findViewById(R.id.tilPassword);
        tilBirthDate = v.findViewById(R.id.tilBirthDate);
        tilCycle = v.findViewById(R.id.tilCycle);
        tilZona = v.findViewById(R.id.tilZone);

        actvLanguages = v.findViewById(R.id.actvIdiomas);
        actvCycle = v.findViewById(R.id.actvCycle);
        actvExperience = v.findViewById(R.id.actvExperiencia);
        actvCar = v.findViewById(R.id.actvCoche);
        actvZona = v.findViewById(R.id.actvZona);
        actvDays = v.findViewById(R.id.actvDays);
        actvTimeSlots = v.findViewById(R.id.actvTimeSlots);
        loadingOverlay = v.findViewById(R.id.loadingOverlay);
    }

    private void setupListeners() {
        etBirthDate.setOnClickListener(v -> showDatePickerDialog());

        btnSelectSkills.setOnClickListener(v -> openMultiSelectSheet("Seleccionar Habilidades", masterSkillsList, selectedSkills, cgSelectedSkills));
        btnSelectInterests.setOnClickListener(v -> openMultiSelectSheet("Seleccionar Intereses", masterInterestsList, selectedInterests, cgSelectedInterests));

        btnAddLanguage.setOnClickListener(v -> {
            String lang = actvLanguages.getText().toString();
            if (!lang.isEmpty() && !selectedLanguages.contains(lang)) {
                selectedLanguages.add(lang);
                updateGlobalSummary();
                actvLanguages.setText("");
            }
        });

        btnAddAvailability.setOnClickListener(v -> {
            String dayStr = actvDays.getText().toString();
            String timeStr = actvTimeSlots.getText().toString();
            if (!dayStr.isEmpty() && !timeStr.isEmpty()) {
                String combo = dayStr + " (" + timeStr + ")";
                if (!selectedAvailability.contains(combo)) {
                    selectedAvailability.add(combo);
                    updateGlobalSummary();
                    actvDays.setText(""); actvTimeSlots.setText("");
                }
            }
        });

        btnRegister.setOnClickListener(v -> { if (validateForm()) performFirebaseRegistration(); });
    }

    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, y, m, d) -> {
                    String mStr = (m + 1) < 10 ? "0" + (m + 1) : String.valueOf(m + 1);
                    String dStr = d < 10 ? "0" + d : String.valueOf(d);
                    etBirthDate.setText(y + "-" + mStr + "-" + dStr);
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private <T> void openMultiSelectSheet(String title, List<T> data, List<String> selection, ChipGroup targetGroup) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View sheet = getLayoutInflater().inflate(R.layout.layout_selector_sheet, null);

        TextView tvTitle = sheet.findViewById(R.id.tvSheetTitle);
        EditText etSearch = sheet.findViewById(R.id.etSearchSheet);
        ChipGroup cgItems = sheet.findViewById(R.id.cgAllItems);
        Button btnConfirm = sheet.findViewById(R.id.btnConfirmSelection);

        tvTitle.setText(title);
        populateSheetChips(cgItems, data, selection);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                for (int i = 0; i < cgItems.getChildCount(); i++) {
                    Chip c = (Chip) cgItems.getChildAt(i);
                    c.setVisibility(c.getText().toString().toLowerCase().contains(s.toString().toLowerCase()) ? View.VISIBLE : View.GONE);
                }
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        btnConfirm.setOnClickListener(v -> {
            selection.clear();
            targetGroup.removeAllViews();
            for (int i = 0; i < cgItems.getChildCount(); i++) {
                Chip c = (Chip) cgItems.getChildAt(i);
                if (c.isChecked()) {
                    selection.add(c.getText().toString());
                    addVisibleChipToGroup(targetGroup, c.getText().toString(), selection);
                }
            }
            updateGlobalSummary();
            dialog.dismiss();
        });
        dialog.setContentView(sheet);
        dialog.show();
    }

    private <T> void populateSheetChips(ChipGroup group, List<T> data, List<String> selection) {
        for (T item : data) {
            String name = (item instanceof Ods) ? ((Ods) item).getName() :
                    (item instanceof Skill) ? ((Skill) item).getName() : ((Interest) item).getName();
            Chip chip = new Chip(requireContext());
            chip.setText(name);
            chip.setCheckable(true);
            chip.setChecked(selection.contains(name));
            applySelectionStyle(chip, chip.isChecked());
            chip.setOnCheckedChangeListener((bv, isChecked) -> applySelectionStyle((Chip)bv, isChecked));
            group.addView(chip);
        }
    }

    private void applySelectionStyle(Chip chip, boolean isSelected) {
        if (isSelected) {
            chip.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#1A3B85")));
            chip.setChipStrokeWidth(4f);
            chip.setChipBackgroundColorResource(R.color.cuatrovientos_blue_light);
            chip.setTextColor(Color.parseColor("#1A3B85"));
        } else {
            chip.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#E0E0E0")));
            chip.setChipStrokeWidth(2f);
            chip.setChipBackgroundColorResource(android.R.color.white);
            chip.setTextColor(Color.parseColor("#757575"));
        }
    }

    private void addVisibleChipToGroup(ChipGroup group, String text, List<String> list) {
        Chip chip = new Chip(requireContext());
        chip.setText(text);
        chip.setCloseIconVisible(true);
        chip.setCloseIconTint(ColorStateList.valueOf(Color.parseColor("#1A3B85")));
        chip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#F0F4FF")));
        chip.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#1A3B85")));
        chip.setChipStrokeWidth(2f);
        chip.setTextColor(Color.parseColor("#1A3B85"));

        chip.setOnCloseIconClickListener(v -> {
            group.removeView(chip);
            list.remove(text);
            updateGlobalSummary();
        });
        group.addView(chip);
    }

    private void updateGlobalSummary() {
        cgSummary.removeAllViews();
        List<String> combined = new ArrayList<>();
        combined.addAll(selectedSkills); combined.addAll(selectedInterests);
        combined.addAll(selectedLanguages); combined.addAll(selectedAvailability);

        for (String s : combined) {
            Chip c = new Chip(requireContext());
            c.setText(s);
            c.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#EEEEEE")));
            c.setChipStrokeWidth(0f);
            c.setTextColor(Color.parseColor("#616161"));
            cgSummary.addView(c);
        }
    }

    private void loadDynamicCategories() {
        if (loadingOverlay != null) loadingOverlay.setVisibility(View.VISIBLE);
        final int[] completedRequests = {0};
        final int TOTAL_REQUESTS = 4;

        Runnable checkDone = () -> {
            completedRequests[0]++;
            if (completedRequests[0] >= TOTAL_REQUESTS) {
                if (loadingOverlay != null) loadingOverlay.setVisibility(View.GONE);
            }
        };

        CategoryManager cm = new CategoryManager();
        cm.fetchAllCategories(
                new CategoryManager.CategoryCallback<Ods>() {
                    @Override public void onSuccess(List<Ods> data) { checkDone.run(); }
                    @Override public void onError(String e) { checkDone.run(); }
                },
                new CategoryManager.CategoryCallback<Skill>() {
                    @Override public void onSuccess(List<Skill> data) { masterSkillsList.clear(); masterSkillsList.addAll(data); checkDone.run(); }
                    @Override public void onError(String e) { checkDone.run(); }
                },
                new CategoryManager.CategoryCallback<Interest>() {
                    @Override public void onSuccess(List<Interest> data) { masterInterestsList.clear(); masterInterestsList.addAll(data); checkDone.run(); }
                    @Override public void onError(String e) { checkDone.run(); }
                },
                new CategoryManager.CategoryCallback<Need>() {
                    @Override public void onSuccess(List<Need> data) { checkDone.run(); }
                    @Override public void onError(String e) { checkDone.run(); }
                }
        );
    }

    private void loadStaticListData() {
        fillDropdown(actvCycle, CYCLE_LIST);
        fillDropdown(actvExperience, EXPERIENCE_LIST);
        fillDropdown(actvCar, CAR_LIST);
        fillDropdown(actvZona, ZONES_LIST);
        fillDropdown(actvLanguages, LANGUAGE_LIST);
        fillDropdown(actvDays, DAYS_LIST);
        fillDropdown(actvTimeSlots, TIME_SLOTS_LIST);
    }

    private void performFirebaseRegistration() {
        toggleLoading(true);
        mAuth.createUserWithEmailAndPassword(getText(etEmail), getText(etPassword)).addOnCompleteListener(task -> {
            if (task.isSuccessful() && mAuth.getCurrentUser() != null) syncWithBackend(mAuth.getCurrentUser());
            else { toggleLoading(false); StatusHelper.showStatus(getContext(), "Error", "No se pudo crear la cuenta en Firebase.", true); }
        });
    }

    private void syncWithBackend(FirebaseUser fbUser) {
        VolunteerRegisterRequest req = new VolunteerRegisterRequest(
                getText(etName), getText(etDni), fbUser.getEmail(), getText(etPassword),
                actvZona.getText().toString(), actvCycle.getText().toString(), etBirthDate.getText().toString(),
                actvExperience.getText().toString(), actvCar.getText().toString(), selectedLanguages,
                selectedSkills, selectedInterests, selectedAvailability, "Pendiente"
        );
        APIClient.getAuthAPIService().registerVolunteer(req).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> c, @NonNull Response<Void> r) {
                if (r.isSuccessful()) {
                    saveRoleToFirestore(fbUser.getUid(), fbUser.getEmail());
                    StatusHelper.showStatus(getContext(), "Éxito", "Usuario registrado correctamente.", false);
                    fbUser.sendEmailVerification();
                    getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new LoginFragment()).commit();
                } else {
                    fbUser.delete();
                    StatusHelper.showStatus(getContext(), "Error servidor", "Código: " + r.code(), true);
                }
                toggleLoading(false);
            }
            @Override
            public void onFailure(@NonNull Call<Void> c, @NonNull Throwable t) {
                fbUser.delete();
                StatusHelper.showStatus(getContext(), "Error conexión", t.getMessage(), true);
                toggleLoading(false);
            }
        });
    }

    private void saveRoleToFirestore(String uid, String email) {
        Map<String, Object> data = new HashMap<>();
        data.put("email", email); data.put("rol", "voluntario");
        FirebaseFirestore.getInstance().collection("usuarios").document(uid).set(data);
    }

    private void fillDropdown(AutoCompleteTextView actv, String[] data) { actv.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, data)); }

    private boolean validateForm() {
        boolean isValid = true;
        tilName.setError(null); tilDni.setError(null); tilEmail.setError(null);
        tilPassword.setError(null); tilBirthDate.setError(null);
        if (tilCycle != null) tilCycle.setError(null);
        if (tilZona != null) tilZona.setError(null);

        if (getText(etName).isEmpty()) { tilName.setError("Campo obligatorio"); isValid = false; }
        if (getText(etDni).isEmpty()) { tilDni.setError("Campo obligatorio"); isValid = false; }

        String email = getText(etEmail);
        if (email.isEmpty()) { tilEmail.setError("Campo obligatorio"); isValid = false; }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { tilEmail.setError("Email inválido"); isValid = false; }

        if (getText(etPassword).length() < 6) { tilPassword.setError("Mínimo 6 caracteres"); isValid = false; }
        if (getText(etBirthDate).isEmpty()) { tilBirthDate.setError("Campo obligatorio"); isValid = false; }

        if (actvZona.getText().toString().isEmpty()) { if (tilZona != null) tilZona.setError("Selecciona una zona"); isValid = false; }
        if (actvCycle.getText().toString().isEmpty()) { if (tilCycle != null) tilCycle.setError("Selecciona tu ciclo"); isValid = false; }

        if (!isValid) StatusHelper.showStatus(getContext(), "Formulario incompleto", "Corrige los campos marcados en rojo.", true);
        return isValid;
    }

    private String getText(TextInputEditText et) { return Objects.requireNonNull(et.getText()).toString().trim(); }
    private void toggleLoading(boolean load) { btnRegister.setEnabled(!load); btnRegister.setText(load ? "Cargando..." : "FINALIZAR REGISTRO"); }
    private void setupToolbar(View v) {
        MaterialToolbar toolbar = v.findViewById(R.id.topAppBarVol);
        if (toolbar != null) toolbar.setNavigationOnClickListener(x -> getParentFragmentManager().popBackStack());
    }
}