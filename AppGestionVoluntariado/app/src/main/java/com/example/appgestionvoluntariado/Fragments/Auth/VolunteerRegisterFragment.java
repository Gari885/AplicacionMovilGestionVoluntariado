package com.example.appgestionvoluntariado.Fragments.Auth;

import static com.example.appgestionvoluntariado.Utils.FormData.CAR_LIST;
import static com.example.appgestionvoluntariado.Utils.FormData.DAYS_LIST;
import static com.example.appgestionvoluntariado.Utils.FormData.EXPERIENCE_LIST;
import static com.example.appgestionvoluntariado.Utils.FormData.LANGUAGE_LIST;
import static com.example.appgestionvoluntariado.Utils.FormData.TIME_SLOTS_LIST;
import static com.example.appgestionvoluntariado.Utils.FormData.ZONES_LIST;

import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.appgestionvoluntariado.Models.Cycle;
import com.example.appgestionvoluntariado.Models.Interest;
import com.example.appgestionvoluntariado.Models.Request.VolunteerRegisterRequest;
import com.example.appgestionvoluntariado.Models.Skill;
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
// import com.google.firebase.auth.FirebaseAuth; // YA NO SE NECESITA FirebaseAuth AQUÍ

import org.json.JSONObject; // Importante para leer el error JSON

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VolunteerRegisterFragment extends Fragment {

    // UI Components
    private Button btnRegister, btnAddAvailability, btnAddLanguage, btnSelectSkills, btnSelectInterests;
    private ProgressBar pbRegisterLoading;
    private TextInputEditText etName, etEmail, etDni, etPassword, etBirthDate;
    private TextInputLayout tilName, tilEmail, tilDni, tilPassword, tilBirthDate, tilCycle, tilZona;
    private AutoCompleteTextView actvLanguages, actvExperience, actvCar, actvCycle, actvZona, actvDays, actvTimeSlots;
    private ChipGroup cgSelectedSkills, cgSelectedInterests, cgSummary;
    private View loadingOverlay;
    private ImageView ivLogoSpinner;
    private Animation rotateAnim;

    // Master Data
    private final List<Skill> masterSkillsList = new ArrayList<>();
    private final List<Interest> masterInterestsList = new ArrayList<>();
    private final List<Cycle> masterCycleList = new ArrayList<>();
    private final List<String> cycleList = new ArrayList<>();

    // User Selections
    private final List<String> selectedSkills = new ArrayList<>();
    private final List<String> selectedInterests = new ArrayList<>();
    private final List<String> selectedAvailability = new ArrayList<>();
    private final List<String> selectedLanguages = new ArrayList<>();

    // private FirebaseAuth mAuth; // ELIMINADO

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_volunteer_register, container, false);
        // mAuth = FirebaseAuth.getInstance(); // ELIMINADO

        initViews(view);
        setupToolbar(view);
        loadStaticListData();
        loadDynamicCategories();
        setupListeners();

        return view;
    }

    private void initViews(View v) {
        // Button loading elements
        btnRegister = v.findViewById(R.id.btnRegister);
        pbRegisterLoading = v.findViewById(R.id.pbRegisterLoading);

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

        // Global loading elements
        loadingOverlay = v.findViewById(R.id.loadingOverlay);
        ivLogoSpinner = v.findViewById(R.id.ivLogoSpinner);
        rotateAnim = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_infinite);
    }

    private void toggleLoading(boolean load) {
        btnRegister.setEnabled(!load);
        if (load) {
            btnRegister.setText("");
            pbRegisterLoading.setVisibility(View.VISIBLE);
        } else {
            btnRegister.setText("FINALIZAR REGISTRO");
            pbRegisterLoading.setVisibility(View.INVISIBLE);
        }
    }

    private void setupListeners() {
        etBirthDate.setOnClickListener(v -> showDatePickerDialog());

        btnSelectSkills.setOnClickListener(v -> openMultiSelectSheet("Habilidades", masterSkillsList, selectedSkills, cgSelectedSkills));
        btnSelectInterests.setOnClickListener(v -> openMultiSelectSheet("Intereses", masterInterestsList, selectedInterests, cgSelectedInterests));

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

        btnRegister.setOnClickListener(v -> {
            if (validateForm()) {
                // CAMBIO IMPORTANTE: Llamada directa al backend
                registerWithBackend();
            }
        });
    }

    // MÉTODO MODIFICADO: Ya no recibe FirebaseUser
    private void registerWithBackend() {
        toggleLoading(true);

        VolunteerRegisterRequest req = new VolunteerRegisterRequest(
                getText(etName), getText(etDni), getText(etEmail), getText(etPassword),
                actvZona.getText().toString(), getCleanCycle(), etBirthDate.getText().toString(),
                actvExperience.getText().toString(), actvCar.getText().toString(), selectedLanguages,
                selectedSkills, selectedInterests, selectedAvailability, "Pendiente"
        );

        APIClient.getAuthAPIService().registerVolunteer(req).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> c, @NonNull Response<Void> r) {
                toggleLoading(false);
                if (r.isSuccessful()) {
                    StatusHelper.showStatus(getContext(), "Éxito", "Usuario registrado correctamente.", false);
                    getParentFragmentManager().popBackStack();
                } else {
                    // MANEJO DE ERRORES INTELIGENTE
                    String errorMessage = "Error en el registro";
                    try {
                        String errorBody = r.errorBody().string();
                        // Intentamos parsear el JSON de error del servidor
                        JSONObject jsonError = new JSONObject(errorBody);
                        if (jsonError.has("error")) {
                            errorMessage = jsonError.getString("error");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (r.code() == 409) {
                        StatusHelper.showStatus(getContext(), "Error", errorMessage, true); // "El correo ya existe..."
                    } else {
                        StatusHelper.showStatus(getContext(), "Error servidor", errorMessage, true);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> c, @NonNull Throwable t) {
                toggleLoading(false);
                StatusHelper.showStatus(getContext(), "Error conexión", t.getMessage(), true);
            }
        });
    }

    private void loadDynamicCategories() {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(View.VISIBLE);
        }

        final int[] completedRequests = {0};
        final int TOTAL_REQUESTS = 3;

        CategoryManager.CategoryCallback<Object> emptyCallback = new CategoryManager.CategoryCallback<Object>() {
            @Override public void onSuccess(List<Object> data) { checkDone(completedRequests, TOTAL_REQUESTS); }
            @Override public void onError(String error) { checkDone(completedRequests, TOTAL_REQUESTS); }
        };

        CategoryManager cm = new CategoryManager();
        cm.fetchAllCategories(
                null,
                new CategoryManager.CategoryCallback<Skill>() {
                    @Override
                    public void onSuccess(List<Skill> data) {
                        masterSkillsList.clear(); masterSkillsList.addAll(data);
                        checkDone(completedRequests, TOTAL_REQUESTS);
                    }
                    @Override public void onError(String e) { checkDone(completedRequests, TOTAL_REQUESTS); }
                },
                new CategoryManager.CategoryCallback<Interest>() {
                    @Override
                    public void onSuccess(List<Interest> data) {
                        masterInterestsList.clear(); masterInterestsList.addAll(data);
                        checkDone(completedRequests, TOTAL_REQUESTS);
                    }
                    @Override public void onError(String e) { checkDone(completedRequests, TOTAL_REQUESTS); }
                },
                null,
                new CategoryManager.CategoryCallback<Cycle>() {
                    @Override
                    public void onSuccess(List<Cycle> data) {
                        masterCycleList.clear(); masterCycleList.addAll(data);
                        convertCycleData();
                        checkDone(completedRequests, TOTAL_REQUESTS);
                    }
                    @Override public void onError(String error) { checkDone(completedRequests, TOTAL_REQUESTS); }
                }
        );
    }

    private void checkDone(int[] counter, int total) {
        counter[0]++;
        if (counter[0] >= total && loadingOverlay != null) {
            loadingOverlay.setVisibility(View.GONE);
        }
    }

    private void convertCycleData() {
        cycleList.clear();
        for (Cycle c : masterCycleList) cycleList.add(c.getFullCycle());
        actvCycle.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, cycleList));
    }

    private void loadStaticListData() {
        fillDropdown(actvExperience, EXPERIENCE_LIST);
        fillDropdown(actvCar, CAR_LIST);
        fillDropdown(actvZona, ZONES_LIST);
        fillDropdown(actvLanguages, LANGUAGE_LIST);
        fillDropdown(actvDays, DAYS_LIST);
        fillDropdown(actvTimeSlots, TIME_SLOTS_LIST);
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
            c.setTextColor(Color.parseColor("#616161"));
            cgSummary.addView(c);
        }
    }

    private <T> void openMultiSelectSheet(String title, List<T> data, List<String> selection, ChipGroup targetGroup) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View sheet = getLayoutInflater().inflate(R.layout.layout_selector_sheet, null);

        TextView tvTitle = sheet.findViewById(R.id.tvSheetTitle);
        EditText etSearch = sheet.findViewById(R.id.etSearchSheet);
        ChipGroup cgItems = sheet.findViewById(R.id.cgAllItems);
        Button btnConfirm = sheet.findViewById(R.id.btnConfirmSelection);

        tvTitle.setText(title);

        for (T item : data) {
            String name = (item instanceof Skill) ? ((Skill) item).getName() : ((Interest) item).getName();
            Chip chip = new Chip(requireContext());
            chip.setText(name);
            chip.setCheckable(true);
            chip.setChecked(selection.contains(name));
            applySelectionStyle(chip, chip.isChecked());
            chip.setOnCheckedChangeListener((bv, isChecked) -> applySelectionStyle((Chip)bv, isChecked));
            cgItems.addView(chip);
        }

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

    private void addVisibleChipToGroup(ChipGroup group, String text, List<String> list) {
        Chip chip = new Chip(requireContext());
        chip.setText(text);
        chip.setCloseIconVisible(true);
        chip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#F0F4FF")));
        chip.setTextColor(Color.parseColor("#1A3B85"));
        chip.setOnCloseIconClickListener(v -> {
            group.removeView(chip);
            list.remove(text);
            updateGlobalSummary();
        });
        group.addView(chip);
    }

    private void applySelectionStyle(Chip chip, boolean isSelected) {
        if (isSelected) {
            chip.setChipBackgroundColorResource(R.color.cuatrovientos_blue_light);
            chip.setTextColor(Color.parseColor("#1A3B85"));
        } else {
            chip.setChipBackgroundColorResource(android.R.color.white);
            chip.setTextColor(Color.parseColor("#757575"));
        }
    }

    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        new DatePickerDialog(requireContext(), (view, y, m, d) -> {
            etBirthDate.setText(String.format(Locale.getDefault(), "%d-%02d-%02d", y, m + 1, d));
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private Cycle getCleanCycle() {
        String full = actvCycle.getText().toString().trim();
        int idx = full.lastIndexOf(" ");
        return (idx != -1) ? new Cycle(full.substring(0, idx), full.substring(idx + 1)) : new Cycle(full, "");
    }

    private void fillDropdown(AutoCompleteTextView actv, String[] data) {
        actv.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, data));
    }

    private String getText(TextInputEditText et) { return Objects.requireNonNull(et.getText()).toString().trim(); }

    private void setupToolbar(View v) {
        MaterialToolbar toolbar = v.findViewById(R.id.topAppBarVol);
        if (toolbar != null) toolbar.setNavigationOnClickListener(x -> getParentFragmentManager().popBackStack());
    }

    private boolean validateForm() {
        boolean isValid = true;
        tilName.setError(null); tilDni.setError(null); tilEmail.setError(null);
        tilPassword.setError(null); tilBirthDate.setError(null);
        if (tilCycle != null) tilCycle.setError(null);
        if (tilZona != null) tilZona.setError(null);

        if (getText(etName).isEmpty()) { tilName.setError("Campo obligatorio"); isValid = false; }
        
        String dni = getText(etDni);
        if (dni.isEmpty()) { 
            tilDni.setError("Campo obligatorio"); 
            isValid = false; 
        } else if (!isValidSpanishID(dni)) {
            tilDni.setError("DNI/NIE inválido"); 
            isValid = false;
        }

        String email = getText(etEmail);
        if (email.isEmpty()) { tilEmail.setError("Campo obligatorio"); isValid = false; }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { tilEmail.setError("Email inválido"); isValid = false; }

        if (getText(etPassword).length() < 6) { tilPassword.setError("Mínimo 6 caracteres"); isValid = false; }
        
        String dob = getText(etBirthDate);
        if (dob.isEmpty()) { 
            tilBirthDate.setError("Campo obligatorio"); 
            isValid = false; 
        } else {
            // Check Age >= 16
            try {
                // Assuming format YYYY-MM-DD from showDatePickerDialog
                // But showDatePickerDialog uses YYYY-MM-DD? Let's check format.
                // Looking at showDatePicker: etBirthDate.setText(String.format(Locale.getDefault(), "%d-%02d-%02d", y, m + 1, d));
                // Yes, YYYY-MM-DD.
                String[] parts = dob.split("-");
                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                int day = Integer.parseInt(parts[2]);

                Calendar birth = Calendar.getInstance();
                birth.set(year, month - 1, day);
                
                Calendar minAge = Calendar.getInstance();
                minAge.add(Calendar.YEAR, -16);

                if (birth.after(minAge)) {
                    tilBirthDate.setError("Debes tener al menos 16 años");
                    isValid = false;
                }
            } catch (Exception e) {
                // If parsing fails for some reason
            }
        }

        if (actvZona.getText().toString().isEmpty()) { if (tilZona != null) tilZona.setError("Selecciona una zona"); isValid = false; }
        if (actvCycle.getText().toString().isEmpty()) { if (tilCycle != null) tilCycle.setError("Selecciona tu ciclo"); isValid = false; }

        if (!isValid) StatusHelper.showStatus(getContext(), "Formulario incompleto", "Corrige los campos marcados en rojo.", true);
        return isValid;
    }

    private boolean isValidSpanishID(String id) {
        String nif = id.toUpperCase().replaceAll("[^0-9A-Z]", "");
        if (nif.length() != 9) return false;

        String letter = nif.substring(8);
        String numbers = nif.substring(0, 8);

        if (Character.isLetter(nif.charAt(0))) { // NIE
            String niePrefix = nif.substring(0, 1);
            numbers = nif.substring(1, 8);
            if (niePrefix.equals("X")) numbers = "0" + numbers;
            else if (niePrefix.equals("Y")) numbers = "1" + numbers;
            else if (niePrefix.equals("Z")) numbers = "2" + numbers;
            else return false;
        }

        try {
            int num = Integer.parseInt(numbers);
            String validLetters = "TRWAGMYFPDXBNJZSQVHLCKE";
            String calculated = String.valueOf(validLetters.charAt(num % 23));
            return letter.equals(calculated);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}