package com.example.appgestionvoluntariado.Fragments.Organization;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.appgestionvoluntariado.Models.Organization;
import com.example.appgestionvoluntariado.Models.ProfileResponse;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Utils.StatusHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment to edit ONG profile using the new ProfileResponse wrapper.
 * Logic in English, UI feedback in Spanish.
 */
public class OrgProfileEditFragment extends Fragment {

    private TextInputEditText etName, etEmail, etVat, etPhone, etAddress, etLocality, etPostalCode, etDescription;
    private TextInputLayout tilName, tilEmail, tilPhone, tilAddress, tilLocality, tilPostalCode, tilDescription;
    private MaterialButton btnSave;
    private View loadingOverlay;

    private FirebaseAuth mAuth;
    private final Gson gson = new Gson();
    private Organization currentOrg;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organization_profile_edit, container, false);

        initViews(view);
        setupToolbar(view);
        fetchProfileData(); // Fetch using the wrapper logic

        btnSave.setOnClickListener(v -> {
            if (isFormValid()) {
                handleUpdateProcess();
            }
        });

        return view;
    }

    private void initViews(View v) {
        etName = v.findViewById(R.id.etEditName);
        etEmail = v.findViewById(R.id.etEditEmail);
        etVat = v.findViewById(R.id.etEditVat);
        etPhone = v.findViewById(R.id.etEditPhone);
        etAddress = v.findViewById(R.id.etEditAddress);
        etLocality = v.findViewById(R.id.etEditLocality);
        etPostalCode = v.findViewById(R.id.etEditPostalCode);
        etDescription = v.findViewById(R.id.etEditDescription);

        tilName = v.findViewById(R.id.tilEditName);
        tilEmail = v.findViewById(R.id.tilEditEmail);
        tilPhone = v.findViewById(R.id.tilEditPhone);
        tilAddress = v.findViewById(R.id.tilEditAddress);
        tilLocality = v.findViewById(R.id.tilEditLocality);
        tilPostalCode = v.findViewById(R.id.tilEditPostalCode);
        tilDescription = v.findViewById(R.id.tilEditDescription);

        btnSave = v.findViewById(R.id.btnSaveProfile);
        loadingOverlay = v.findViewById(R.id.loadingOverlay);
    }

    private void fetchProfileData() {
        toggleLoading(true);
        // Using AuthAPIService with ProfileResponse as seen in Hub
        APIClient.getAuthAPIService().getProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                toggleLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    ProfileResponse wrapper = response.body();
                    if ("organizacion".equalsIgnoreCase(wrapper.getType())) {
                        currentOrg = gson.fromJson(wrapper.getData(), Organization.class);
                        populateForm(currentOrg);
                    }
                } else {
                    StatusHelper.showStatus(getContext(), "Error", "No se pudieron cargar los datos", true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
                toggleLoading(false);
                StatusHelper.showStatus(getContext(), "Error de red", "Sin conexión con el servidor", true);
            }
        });
    }

    private void populateForm(Organization org) {
        etName.setText(org.getName());
        etEmail.setText(org.getEmail());
        etVat.setText(org.getVat());
        etPhone.setText(org.getContactPhone());
        etAddress.setText(org.getAddress());
        etLocality.setText(org.getLocality());
        etPostalCode.setText(org.getPostalCode());
        etDescription.setText(org.getDescription());
    }

    private void handleUpdateProcess() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        String newEmail = getText(etEmail);
        toggleLoading(true);

        // Step 1: Sync with Firebase Auth if email changed
        if (!newEmail.equalsIgnoreCase(user.getEmail())) {
            user.updateEmail(newEmail).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    updateBackend(newEmail);
                } else {
                    toggleLoading(false);
                    StatusHelper.showStatus(getContext(), "Seguridad", "Re-autentícate para cambiar el email.", true);
                }
            });
        } else {
            updateBackend(newEmail);
        }
    }

    private void updateBackend(String email) {
        Organization updated = new Organization();
        updated.setName(getText(etName));
        updated.setEmail(email);
        updated.setContactPhone(getText(etPhone));
        updated.setAddress(getText(etAddress));
        updated.setLocality(getText(etLocality));
        updated.setPostalCode(getText(etPostalCode));
        updated.setDescription(getText(etDescription));

        APIClient.getOrganizationService().updateProfile(updated).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                toggleLoading(false);
                if (response.isSuccessful()) {
                    StatusHelper.showStatus(getContext(), "Éxito", "Perfil actualizado correctamente", false);
                    getParentFragmentManager().popBackStack();
                } else {
                    StatusHelper.showStatus(getContext(), "Error", "No se pudo sincronizar el servidor", true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                toggleLoading(false);
                StatusHelper.showStatus(getContext(), "Error", "Fallo de conexión", true);
            }
        });
    }

    private boolean isFormValid() {
        boolean isValid = true;
        if (getText(etName).isEmpty()) { tilName.setError("Requerido"); isValid = false; } else tilName.setError(null);
        if (!Patterns.EMAIL_ADDRESS.matcher(getText(etEmail)).matches()) { tilEmail.setError("Email inválido"); isValid = false; } else tilEmail.setError(null);
        if (getText(etPhone).isEmpty()) { tilPhone.setError("Requerido"); isValid = false; } else tilPhone.setError(null);
        return isValid;
    }

    private void setupToolbar(View view) {
        MaterialToolbar toolbar = view.findViewById(R.id.topAppBarOrg);
        if (toolbar != null) toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());
    }

    private void toggleLoading(boolean isLoading) {
        if (loadingOverlay != null) loadingOverlay.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!isLoading);
    }

    private String getText(TextInputEditText et) { return Objects.requireNonNull(et.getText()).toString().trim(); }
}