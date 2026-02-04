package com.example.appgestionvoluntariado.Fragments.Organization;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.appgestionvoluntariado.Models.Organization;
import com.example.appgestionvoluntariado.Models.Response.ProfileResponse;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Utils.FormData;
import com.example.appgestionvoluntariado.Utils.StatusHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrgProfileEditFragment extends Fragment {

    private TextInputEditText etName, etEmail, etVat, etPhone, etAddress, etPostalCode, etDescription;

    private TextInputLayout tilName, tilEmail, tilPhone, tilAddress, tilLocality, tilPostalCode, tilDescription,tilSector;
    private MaterialButton btnSave;
    private View loadingOverlay;
    private TextView tvLoadingText;

    private ImageView logoSpinner;
    private android.view.animation.Animation rotateAnimation;

    private AutoCompleteTextView actSector, actLocality;


    private FirebaseAuth mAuth;
    private final Gson gson = new Gson();
    private Organization currentOrg;

    private String oldEmail;

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
        fetchProfileData();

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
        etAddress = v.findViewById(R.id.etEditAddress);
        // Locality changed to AutoCompleteTextView:
        actLocality = v.findViewById(R.id.actvEditLocality);
        etPostalCode = v.findViewById(R.id.etEditPostalCode);
        etDescription = v.findViewById(R.id.etEditDescription);

        tilName = v.findViewById(R.id.tilEditName);
        tilEmail = v.findViewById(R.id.tilEditEmail);
        tilPhone = v.findViewById(R.id.tilEditPhone);
        tilAddress = v.findViewById(R.id.tilEditAddress);
        tilLocality = v.findViewById(R.id.tilEditLocality);
        tilPostalCode = v.findViewById(R.id.tilEditPostalCode);
        tilDescription = v.findViewById(R.id.tilEditDescription);
        tilSector = v.findViewById(R.id.tilSector);

        actSector = v.findViewById(R.id.actvSector);
        btnSave = v.findViewById(R.id.btnSaveProfile);
        loadingOverlay = v.findViewById(R.id.loadingOverlay);
        tvLoadingText = v.findViewById(R.id.tvLoadingText);
        logoSpinner = v.findViewById(R.id.ivLogoSpinner);
        
        
        rotateAnimation = android.view.animation.AnimationUtils.loadAnimation(getContext(), R.anim.rotate_infinite);
    }


    private void fetchProfileData() {
        toggleLoading(true, "Cargando perfil...");
        APIClient.getAuthAPIService().getProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                toggleLoading(false, null);
                if (!isAdded() || getContext() == null) return;
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
                toggleLoading(false, null);
                StatusHelper.showStatus(getContext(), "Error de red", "Sin conexión con el servidor", true);
            }
        });
    }

    private void populateForm(Organization org) {
        etName.setText(org.getName());
        etEmail.setText(org.getEmail());
        etVat.setText(org.getCif());
        etPhone.setText(org.getContactPhone());
        etAddress.setText(org.getAddress());
        etAddress.setText(org.getAddress());
        if (actLocality != null) actLocality.setText(org.getLocality(), false);
        etPostalCode.setText(org.getPostalCode());
        etDescription.setText(org.getDescription());
        actSector.setText(org.getSector());
        loadSector();
    }

    private void loadSector() {
        actSector.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, FormData.SECTORS_LIST));
        if (actLocality != null) {
            actLocality.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, FormData.ZONES_LIST));
        }
    }

    private void handleUpdateProcess() {
        String newEmail = getText(etEmail);
        toggleLoading(true, "Guardando cambios...");
        StatusHelper.showToast(getContext(), "Guardando cambios...", false);
        
        // Call backend directly - no Firebase needed (using JWT auth)
        updateBackend(newEmail);
    }

    private void revertEmail() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null || oldEmail == null) {
            toggleLoading(false, null);
            return;
        }

        // Intentamos volver al email antiguo
        user.updateEmail(oldEmail).addOnCompleteListener(task -> {
            toggleLoading(false, null);

            if (task.isSuccessful()) {
                // Éxito: Todo ha vuelto a como estaba antes de darle al botón
                StatusHelper.showStatus(getContext(), "Error",
                        "Fallo en el servidor. Se ha restaurado tu email original.", true);
            } else {
                // CASO CRÍTICO: Firebase cambió el email, Backend falló, y revertir falló.
                // Esto pasa raramente (ej. si pierde conexión justo en medio).
                StatusHelper.showStatus(getContext(), "Atención",
                        "Hubo un error de sincronización. Tu email ahora es el nuevo, pero los datos de perfil no se guardaron.", true);
            }
        });
    }

    private void updateBackend(String email) {
        Map<String, Object> update = new HashMap<>();
        update.put("nombre" , getText(etName));
        update.put("email" , email);
        update.put("telefono", getText(etPhone));
        update.put("direccion",getText(etAddress));
        update.put("localidad",actLocality.getText().toString());
        update.put("cp", getText(etPostalCode));
        update.put("descripcion",getText(etDescription));
        update.put("sector",actSector.getText().toString());


        APIClient.getOrganizationService().updateProfile(update).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                toggleLoading(false, null);
                if (response.isSuccessful()) {
                    StatusHelper.showStatus(getContext(), "Éxito", "Perfil actualizado correctamente", false);
                    getParentFragmentManager().popBackStack();
                } else {
                    revertEmail();
                    StatusHelper.showStatus(getContext(), "Error", "No se pudo sincronizar el servidor", true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                revertEmail();
                toggleLoading(false, null);
                StatusHelper.showStatus(getContext(), "Error", "Fallo de conexión", true);
            }
        });
    }

    private boolean isFormValid() {
        boolean isValid = true;
        if (getText(etName).isEmpty()) { tilName.setError("Requerido"); isValid = false; } else tilName.setError(null);
        if (!Patterns.EMAIL_ADDRESS.matcher(getText(etEmail)).matches()) { tilEmail.setError("Email inválido"); isValid = false; } else tilEmail.setError(null);
        if (getText(etPhone).isEmpty()) { tilPhone.setError("Requerido"); isValid = false; } else tilPhone.setError(null);
        if (actSector.getText().toString().isEmpty()){ tilSector.setError("Requerido"); isValid = false; } else tilSector.setError(null);
        return isValid;
    }

    private void setupToolbar(View view) {
        MaterialToolbar toolbar = view.findViewById(R.id.topAppBarOrg);
        if (toolbar != null) toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());
    }

    private void toggleLoading(boolean isLoading, @Nullable String msg) {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            if (tvLoadingText != null && msg != null) tvLoadingText.setText(msg);
            
            if (isLoading && logoSpinner != null && rotateAnimation != null) {
                logoSpinner.startAnimation(rotateAnimation);
            }
        }
        if (btnSave != null) {
            btnSave.setEnabled(!isLoading);
            if(isLoading) {
                btnSave.setText("Guardando...");
                btnSave.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#757575")));
            } else {
                btnSave.setText("GUARDAR CAMBIOS");
                btnSave.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#1A3B85")));
            }
        }
    }

    private String getText(TextInputEditText et) { return Objects.requireNonNull(et.getText()).toString().trim(); }
}