package com.example.appgestionvoluntariado.Fragments.Auth;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.appgestionvoluntariado.Models.OrganizationRegisterRequest;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Utils.StatusHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import com.example.appgestionvoluntariado.Fragments.Auth.LoginFragment;

public class OrganizationRegisterFragment extends Fragment {

    private FirebaseAuth mAuth;
    private Button btnRegister;

    // UI References in English [cite: 2026-01-09]
    private TextInputLayout tilName, tilEmail, tilPassword, tilCif, tilPhone, tilAddress, tilLocality, tilPostalCode, tilDescription,tilSector;
    private TextInputEditText etName, etEmail, etPassword, etCif, etPhone, etAddress, etLocality, etPostalCode, etDescription,etSector;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organization_register, container, false);

        initViews(view);
        setupToolbar(view);

        btnRegister.setOnClickListener(v -> {
            if (isFormValid()) {
                performRegistration();
            }
        });

        return view;
    }

    private void initViews(View view) {
        // Layouts for error management [cite: 2026-01-09]
        tilName = view.findViewById(R.id.tilName);
        tilEmail = view.findViewById(R.id.tilEmail);
        tilPassword = view.findViewById(R.id.tilPassword);
        tilCif = view.findViewById(R.id.tilCif);
        tilPhone = view.findViewById(R.id.tilPhone);
        tilAddress = view.findViewById(R.id.tilAddress);
        tilLocality = view.findViewById(R.id.tilLocality);
        tilPostalCode = view.findViewById(R.id.tilPostalCode);
        tilDescription = view.findViewById(R.id.tilDescription);
        tilSector = view.findViewById(R.id.tilSector);

        // Inputs
        etName = view.findViewById(R.id.etName);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        etCif = view.findViewById(R.id.etCif);
        etPhone = view.findViewById(R.id.etPhone);
        etAddress = view.findViewById(R.id.etAddress);
        etLocality = view.findViewById(R.id.etLocality);
        etPostalCode = view.findViewById(R.id.etPostalCode);
        etDescription = view.findViewById(R.id.etDescription);
        etSector = view.findViewById(R.id.etSector);

        btnRegister = view.findViewById(R.id.btnRegister);
    }

    private void setupToolbar(View view) {
        MaterialToolbar toolbar = view.findViewById(R.id.topAppBarOrg);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());
        }
    }

    private boolean isFormValid() {
        boolean isValid = true;
        // Validation for the 9 API required fields [cite: 2026-01-16]
        if (getText(etName).isEmpty()) { tilName.setError("Nombre obligatorio"); isValid = false; } else tilName.setError(null);
        if (getText(etCif).isEmpty()) { tilCif.setError("CIF obligatorio"); isValid = false; } else tilCif.setError(null);
        if (getText(etPhone).isEmpty()) { tilPhone.setError("Contacto obligatorio"); isValid = false; } else tilPhone.setError(null);
        if (getText(etAddress).isEmpty()) { tilAddress.setError("Dirección obligatoria"); isValid = false; } else tilAddress.setError(null);
        if (getText(etLocality).isEmpty()) { tilLocality.setError("Localidad obligatoria"); isValid = false; } else tilLocality.setError(null);
        if (getText(etPostalCode).isEmpty()) { tilPostalCode.setError("CP obligatorio"); isValid = false; } else tilPostalCode.setError(null);
        if (getText(etDescription).isEmpty()) { tilDescription.setError("Misión obligatoria"); isValid = false; } else tilDescription.setError(null);

        String email = getText(etEmail);
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { tilEmail.setError("Email inválido"); isValid = false; } else tilEmail.setError(null);

        if (getText(etPassword).length() < 10) { tilPassword.setError("Mínimo 10 caracteres"); isValid = false; } else tilPassword.setError(null);

        return isValid;
    }

    private void performRegistration() {
        // Firebase Auth process [cite: 2026-01-15]
        mAuth.createUserWithEmailAndPassword(getText(etEmail), getText(etPassword)).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    syncWithBackend(user);
                }
            } else {
                StatusHelper.showStatus(getContext(), "Error", "Fallo en el registro Firebase", true);
            }
        });
    }

    private void syncWithBackend(FirebaseUser fbUser) {
        OrganizationRegisterRequest req =
                new OrganizationRegisterRequest(
                        getText(etCif), getText(etName), fbUser.getEmail(), getText(etPassword),
                        getText(etPhone), getText(etAddress), getText(etLocality), getText(etPostalCode),
                        getText(etDescription), getText(etSector)
                );

        APIClient.getAuthAPIService().registerOrganization(req).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<Void> call, @NonNull retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    saveUserToFirestore(fbUser.getUid());
                    fbUser.sendEmailVerification();
                    StatusHelper.showStatus(getContext(), "¡Éxito!", "Cuenta creada. Verifica tu email.", false);
                    if (getParentFragmentManager() != null) {
                        getParentFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new LoginFragment()).commit();
                    }
                } else {
                    fbUser.delete();
                    try {
                        String err = response.errorBody() != null ? response.errorBody().string() : "Error desconocido";
                        StatusHelper.showStatus(getContext(), "Error servidor", err, true);
                    } catch (Exception e) {
                        StatusHelper.showStatus(getContext(), "Error servidor", "Código: " + response.code(), true);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<Void> call, @NonNull Throwable t) {
                fbUser.delete();
                StatusHelper.showStatus(getContext(), "Error conexión", t.getMessage(), true);
            }
        });
    }

    private void saveUserToFirestore(String uid) {
        Map<String, Object> data = new HashMap<>();
        data.put("email", getText(etEmail));
        data.put("rol", "organizacion");
        // Add additional API fields to Firestore if needed [cite: 2026-01-16]
        FirebaseFirestore.getInstance().collection("usuarios").document(uid).set(data);
    }

    private String getText(TextInputEditText et) { return Objects.requireNonNull(et.getText()).toString().trim(); }
}