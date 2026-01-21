package com.example.appgestionvoluntariado.Fragments.Auth;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.appgestionvoluntariado.Models.Cycle;
import com.example.appgestionvoluntariado.Models.Interest;
import com.example.appgestionvoluntariado.Models.Need;
import com.example.appgestionvoluntariado.Models.Ods;
import com.example.appgestionvoluntariado.Models.Request.OrganizationRegisterRequest;
import com.example.appgestionvoluntariado.Models.Skill;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Utils.CategoryManager;
import com.example.appgestionvoluntariado.Utils.FormData;
import com.example.appgestionvoluntariado.Utils.StatusHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class OrganizationRegisterFragment extends Fragment {

    private FirebaseAuth mAuth;
    private Button btnRegister;

    // UI References in English [cite: 2026-01-09]
    private TextInputLayout tilName, tilEmail, tilPassword, tilCif, tilPhone, tilAddress, tilLocality, tilPostalCode, tilDescription,tilSector;
    private TextInputEditText etName, etEmail, etPassword, etCif, etPhone, etAddress, etLocality, etPostalCode, etDescription;


    AutoCompleteTextView actSector;
    private View loadingOverlay;

    private ProgressBar progressBar;
    private String btnText;

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
        setUpSectors();

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

        actSector = view.findViewById(R.id.actvSector);
        loadingOverlay = view.findViewById(R.id.loadingOverlay);
        progressBar = view.findViewById(R.id.btnProgressBar);
        btnRegister = view.findViewById(R.id.btnRegister);
        loadingOverlay.setVisibility(View.VISIBLE);
        btnText = btnRegister.getText().toString();
    }

    private void setupToolbar(View view) {
        MaterialToolbar toolbar = view.findViewById(R.id.topAppBarOrg);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());
        }
    }

    private void setUpSectors() {
        actSector.setAdapter(new ArrayAdapter<>(requireContext(),android.R.layout.simple_spinner_dropdown_item, FormData.SECTORS_LIST));
        loadingOverlay.setVisibility(View.INVISIBLE);
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

        if (!isValid) StatusHelper.showStatus(getContext(), "Formulario incompleto", "Corrige los campos marcados en rojo.", true);
        return isValid;    }

    private void performRegistration() {
        btnRegister.setText("");
        progressBar.setVisibility(View.VISIBLE);
        // Firebase Auth process [cite: 2026-01-15]
        mAuth.createUserWithEmailAndPassword(getText(etEmail), getText(etPassword)).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    syncWithBackend(user);
                }
            } else {
                btnRegister.setText(btnText);
                progressBar.setVisibility(View.INVISIBLE);
                StatusHelper.showStatus(getContext(), "Error", "Fallo en el registro Firebase", true);
            }
        });
    }

    private void syncWithBackend(FirebaseUser fbUser) {
        OrganizationRegisterRequest req =
                new OrganizationRegisterRequest(
                        getText(etCif), getText(etName), fbUser.getEmail(), getText(etPassword),
                        getText(etPhone), getText(etAddress), getText(etLocality), getText(etPostalCode),
                        getText(etDescription), actSector.getText().toString()
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
                        btnRegister.setText(btnText);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                } else {
                    fbUser.delete();
                    try {
                        String err = response.errorBody() != null ? response.errorBody().string() : "Error desconocido";
                        StatusHelper.showStatus(getContext(), "Error servidor", err, true);
                        btnRegister.setText(btnText);
                        progressBar.setVisibility(View.INVISIBLE);
                    } catch (Exception e) {
                        StatusHelper.showStatus(getContext(), "Error servidor", "Código: " + response.code(), true);
                        btnRegister.setText(btnText);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<Void> call, @NonNull Throwable t) {
                fbUser.delete();
                StatusHelper.showStatus(getContext(), "Error conexión", t.getMessage(), true);
                btnRegister.setText(btnText);
                progressBar.setVisibility(View.INVISIBLE);
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