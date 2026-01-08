package com.example.appgestionvoluntariado.Fragments.Auth;

import static com.example.appgestionvoluntariado.GlobalSession.showError;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.appgestionvoluntariado.GlobalData;
import com.example.appgestionvoluntariado.Models.VolunteerRegisterRequest;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Services.AuthAPIService;
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

    private Button btnRegister, btnBack;
    private TextInputEditText etName, etEmail, etDni, etPassword, etBirthDate, etZone;
    private TextInputLayout tilName, tilEmail, tilDni, tilPassword, tilBirthDate;

    private AutoCompleteTextView actvLanguages, actvExperience, actvCar, actvCycle;
    private ChipGroup chipGroupSkills, chipGroupInterests, chipGroupAvailability, chipGroupSummary;

    private FirebaseAuth mAuth;
    private final List<String> selectedSkills = new ArrayList<>();
    private final List<String> selectedInterests = new ArrayList<>();
    private final List<String> selectedAvailability = new ArrayList<>();
    private final List<String> selectedLanguages = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_volunteer_register, container, false);

        mAuth = FirebaseAuth.getInstance();
        initViews(view);
        loadListData();
        initListeners();

        return view;
    }

    private void initViews(View view) {
        btnRegister = view.findViewById(R.id.btnRegistrar);
        btnBack = view.findViewById(R.id.btnVolver);

        etName = view.findViewById(R.id.etNombre);
        etEmail = view.findViewById(R.id.etCorreo);
        etDni = view.findViewById(R.id.etDni);
        etPassword = view.findViewById(R.id.etPassword);
        etBirthDate = view.findViewById(R.id.etFechaNac);
        etZone = view.findViewById(R.id.etZona);

        tilName = view.findViewById(R.id.tilNombre);
        tilEmail = view.findViewById(R.id.tilCorreo);
        tilDni = view.findViewById(R.id.tilDni);
        tilPassword = view.findViewById(R.id.tilPassword);
        tilBirthDate = view.findViewById(R.id.tilFechaNac);

        actvLanguages = view.findViewById(R.id.actvIdiomas);
        actvExperience = view.findViewById(R.id.actvExperiencia);
        actvCar = view.findViewById(R.id.actvCoche);
        actvCycle = view.findViewById(R.id.actvCiclo);

        chipGroupSkills = view.findViewById(R.id.chipGroupHabilidades);
        chipGroupInterests = view.findViewById(R.id.chipGroupIntereses);
        chipGroupAvailability = view.findViewById(R.id.chipGroupDisponibilidad);
        chipGroupSummary = view.findViewById(R.id.chipGroupResumen);
    }

    private void initListeners() {
        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        btnRegister.setOnClickListener(v -> {
            if (validateMandatoryFields()) {
                toggleLoading(true);
                String email = Objects.requireNonNull(etEmail.getText()).toString().trim();
                String pass = Objects.requireNonNull(etPassword.getText()).toString().trim();

                registerInFirebase(email, pass);
            }
        });
    }

    private boolean validateMandatoryFields() {
        boolean isValid = true;

        if (TextUtils.isEmpty(etName.getText())) {
            tilName.setError("Obligatorio");
            isValid = false;
        } else {
            tilName.setError(null);
        }

        if (TextUtils.isEmpty(etDni.getText())) {
            tilDni.setError("Obligatorio");
            isValid = false;
        } else {
            tilDni.setError(null);
        }

        String email = Objects.requireNonNull(etEmail.getText()).toString().trim();
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Correo inválido");
            isValid = false;
        } else {
            tilEmail.setError(null);
        }

        if (TextUtils.isEmpty(etPassword.getText()) || Objects.requireNonNull(etPassword.getText()).length() < 6) {
            tilPassword.setError("Mínimo 6 caracteres");
            isValid = false;
        } else {
            tilPassword.setError(null);
        }

        String date = Objects.requireNonNull(etBirthDate.getText()).toString().trim();
        if (!date.isEmpty()) {
            if (!date.matches("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/\\d{4}$")) {
                tilBirthDate.setError("Formato incorrecto (DD/MM/YYYY)");
                isValid = false;
            } else {
                tilBirthDate.setError(null);
            }
        }

        return isValid;
    }

    private void registerInFirebase(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveInBackendSQL(user.getUid(), email, user);
                        }
                    } else {
                        toggleLoading(false);
                        String error = task.getException() != null ? task.getException().getMessage() : "Error desconocido";
                        showError(getContext(), "Error Auth: " + error);
                    }
                });
    }

    private void saveInBackendSQL(String uid, String email, FirebaseUser firebaseUser) {
        String name = getTextSafe(etName);
        String dni = getTextSafe(etDni);
        String pass = getTextSafe(etPassword);
        String zone = getTextSafe(etZone);
        String experience = actvExperience.getText().toString();
        String car = actvCar.getText().toString();
        String cycle = actvCycle.getText().toString();

        String dateInput = getTextSafe(etBirthDate);
        String formattedDate = convertDateToSQL(dateInput);

        VolunteerRegisterRequest request = new VolunteerRegisterRequest(
                name, dni, email, pass, zone, cycle,
                formattedDate, experience, car, selectedLanguages,
                selectedSkills, selectedInterests, selectedAvailability, "Pendiente"
        );

        AuthAPIService apiService = APIClient.getAuthAPIService();
        apiService.registerVolunteer(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (!isAdded() || getContext() == null) return;

                if (response.isSuccessful()) {
                    saveRoleInFirestore(uid, email);
                    Toast.makeText(getContext(), "¡Registro completado!", Toast.LENGTH_LONG).show();
                    if (firebaseUser != null) firebaseUser.sendEmailVerification();
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainer, new LoginFragment())
                            .commit();
                } else {
                    firebaseUser.delete();
                    toggleLoading(false);
                    showError(getContext(), "Error Servidor (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                if (!isAdded() || getContext() == null) return;
                firebaseUser.delete();
                toggleLoading(false);
                showError(getContext(), "Fallo de conexión.");
            }
        });
    }

    private String convertDateToSQL(String dateDDMMYYYY) {
        try {
            String[] parts = dateDDMMYYYY.split("/");
            if (parts.length == 3) {
                return parts[2] + "-" + parts[1] + "-" + parts[0];
            }
        } catch (Exception e) {
            return "1900-01-01";
        }
        return "1900-01-01";
    }

    private void saveRoleInFirestore(String uid, String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("rol", "voluntario");
        db.collection("usuarios").document(uid).set(userData);
    }

    private void loadListData() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, GlobalData.getInstance().LANGUAGE_LIST);
        actvLanguages.setAdapter(adapter);
        actvLanguages.setOnItemClickListener((parent, view, position, id) -> {
            String language = parent.getItemAtPosition(position).toString();
            if (!selectedLanguages.contains(language)) {
                selectedLanguages.add(language);
                addSummaryChip(language, "IDIOMA");
            }
        });

        fillDropdown(actvExperience, GlobalData.getInstance().EXPERIENCE_LIST);
        fillDropdown(actvCar, GlobalData.getInstance().CAR_LIST);
        fillDropdown(actvCycle, GlobalData.getInstance().CYCLE_LIST);

        fillChips(chipGroupSkills, GlobalData.getInstance().SKILL_CHIPS, selectedSkills, "HABILIDADES");
        fillChips(chipGroupInterests, GlobalData.getInstance().INTEREST_CHIPS, selectedInterests, "INTERESES");
        fillChips(chipGroupAvailability, GlobalData.getInstance().AVAILABILITY, selectedAvailability, "DISPONIBILIDAD");
    }

    private void fillDropdown(AutoCompleteTextView actv, String[] data) {
        actv.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, data));
    }

    private void fillChips(ChipGroup group, String[] tags, List<String> list, String type) {
        for (String tag : tags) {
            Chip chip = new Chip(requireContext());
            chip.setText(tag);
            chip.setCheckable(true);
            chip.setChipBackgroundColorResource(android.R.color.white);
            chip.setOnCheckedChangeListener((v, isChecked) -> {
                if (isChecked) {
                    list.add(tag);
                    group.removeView(chip);
                    addSummaryChip(tag, type);
                }
            });
            group.addView(chip);
        }
    }

    private void addSummaryChip(String text, String type) {
        Chip chip = new Chip(requireContext());
        chip.setText(text);
        chip.setCloseIconVisible(true);
        chip.setCheckable(false);

        int color = android.R.color.darker_gray;
        if (type.equals("IDIOMA")) color = android.R.color.holo_purple;
        else if (type.equals("HABILIDADES")) color = android.R.color.holo_green_dark;
        else if (type.equals("INTERESES")) color = android.R.color.holo_orange_light;
        else if (type.equals("DISPONIBILIDAD")) color = android.R.color.holo_blue_dark;

        chip.setChipBackgroundColorResource(color);
        chip.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));

        chip.setOnCloseIconClickListener(v -> {
            chipGroupSummary.removeView(chip);
            if (type.equals("IDIOMA")) selectedLanguages.remove(text);
            else if (type.equals("HABILIDADES")) { selectedSkills.remove(text); restoreChip(chipGroupSkills, text, selectedSkills, type); }
            else if (type.equals("INTERESES")) { selectedInterests.remove(text); restoreChip(chipGroupInterests, text, selectedInterests, type); }
            else if (type.equals("DISPONIBILIDAD")) { selectedAvailability.remove(text); restoreChip(chipGroupAvailability, text, selectedAvailability, type); }
        });
        chipGroupSummary.addView(chip);
    }

    private void restoreChip(ChipGroup group, String text, List<String> list, String type) {
        Chip chip = new Chip(requireContext());
        chip.setText(text);
        chip.setCheckable(true);
        chip.setChipBackgroundColorResource(android.R.color.white);
        chip.setOnCheckedChangeListener((v, isChecked) -> {
            if (isChecked) {
                list.add(text);
                group.removeView(chip);
                addSummaryChip(text, type);
            }
        });
        group.addView(chip);
    }

    private String getTextSafe(TextInputEditText et) {
        return Objects.requireNonNull(et.getText()).toString().trim();
    }

    private void toggleLoading(boolean isLoading) {
        btnRegister.setEnabled(!isLoading);
        btnRegister.setText(isLoading ? "Procesando..." : "Registrarme");
        btnBack.setEnabled(!isLoading);
    }
}
