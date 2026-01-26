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

import com.example.appgestionvoluntariado.Models.Request.OrganizationRegisterRequest;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Utils.FormData;
import com.example.appgestionvoluntariado.Utils.StatusHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
// import com.google.firebase.auth.FirebaseAuth; // ELIMINADO

import org.json.JSONObject; // Importante para leer el error JSON

import java.io.IOException;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrganizationRegisterFragment extends Fragment {

    // private FirebaseAuth mAuth; // ELIMINADO
    private Button btnRegister;

    // UI References
    private TextInputLayout tilName, tilEmail, tilPassword, tilCif, tilPhone, tilAddress, tilLocality, tilPostalCode, tilDescription,tilSector;
    private TextInputEditText etName, etEmail, etPassword, etCif, etPhone, etAddress, etLocality, etPostalCode, etDescription;

    AutoCompleteTextView actSector;
    private View loadingOverlay;

    private ProgressBar progressBar;
    private String btnText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // mAuth = FirebaseAuth.getInstance(); // ELIMINADO
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organization_register, container, false);

        initViews(view);
        setupToolbar(view);
        setUpSectors();

        btnRegister.setOnClickListener(v -> {
            if (isFormValid()) {
                // CAMBIO: Llamada directa al backend
                registerWithBackend();
            }
        });

        return view;
    }

    private void initViews(View view) {
        // Layouts for error management
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
        
        if (getText(etName).isEmpty()) { tilName.setError("Nombre obligatorio"); isValid = false; } else tilName.setError(null);
        
        String cif = getText(etCif);
        if (cif.isEmpty()) { 
            tilCif.setError("CIF obligatorio"); 
            isValid = false; 
        } else if (!isValidCIF(cif)) {
            tilCif.setError("CIF inválido");
            isValid = false;
        } else {
            tilCif.setError(null);
        }

        String phone = getText(etPhone).replaceAll("[^0-9]", ""); // Strip non-digits
        if (phone.isEmpty()) { 
            tilPhone.setError("Contacto obligatorio"); 
            isValid = false; 
        } else if (phone.length() != 9) {
            tilPhone.setError("Debe tener 9 dígitos");
            isValid = false;
        } else {
            tilPhone.setError(null);
        }

        if (getText(etAddress).isEmpty()) { tilAddress.setError("Dirección obligatoria"); isValid = false; } else tilAddress.setError(null);
        if (getText(etLocality).isEmpty()) { tilLocality.setError("Localidad obligatoria"); isValid = false; } else tilLocality.setError(null);
        
        if (getText(etPostalCode).isEmpty()) { 
            tilPostalCode.setError("CP obligatorio"); 
            isValid = false; 
        } else if (!getText(etPostalCode).matches("^[0-9]{5}$")) {
            tilPostalCode.setError("CP inválido (5 dígitos)");
            isValid = false;
        } else {
            tilPostalCode.setError(null);
        }

        if (getText(etDescription).isEmpty()) { tilDescription.setError("Misión obligatoria"); isValid = false; } else tilDescription.setError(null);

        String email = getText(etEmail);
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { tilEmail.setError("Email inválido"); isValid = false; } else tilEmail.setError(null);

        if (getText(etPassword).length() < 10) { tilPassword.setError("Mínimo 10 caracteres"); isValid = false; } else tilPassword.setError(null);

        if (!isValid) StatusHelper.showStatus(getContext(), "Formulario incompleto", "Corrige los campos marcados en rojo.", true);
        return isValid;
    }

    private boolean isValidCIF(String cif) {
        cif = cif.toUpperCase().trim();
        if (cif.length() != 9) return false;
        
        char firstChar = cif.charAt(0);
        if ("ABCDEFGHJKLMNPQRSUVW".indexOf(firstChar) == -1) return false;

        String digits = cif.substring(1, 8);
        if (!digits.matches("[0-9]+")) return false;

        int evenSum = 0;
        int oddSum = 0;
        
        for (int i = 0; i < digits.length(); i++) {
            int digit = Character.getNumericValue(digits.charAt(i));
            if ((i + 1) % 2 == 0) { // Even positions (in 1-based index, index 1, 3, 5...) -> 2nd, 4th, 6th digit
                evenSum += digit;
            } else { // Odd positions -> 1st, 3rd, 5th digit
                int doubled = digit * 2;
                oddSum += (doubled / 10) + (doubled % 10);
            }
        }
        
        int totalSum = evenSum + oddSum;
        int controlDigit = (10 - (totalSum % 10)) % 10;
        
        char expectedChar;
        if ("NPQRSW".indexOf(firstChar) != -1) {
            // Letter control
            expectedChar = "JABCDEFGHI".charAt(controlDigit);
        } else {
            // Number control
            expectedChar = Character.forDigit(controlDigit, 10);
        }

        char lastChar = cif.charAt(8);
        // Some CIFs can end in letter OR number, usually strict validation checks specific types
        // Simplified check: if it matches numeric or letter control, acceptable
        if (lastChar == expectedChar) return true;
        
        // Check alternative (some types allow letter)
        if ("ABCDEFGH".indexOf(firstChar) != -1) {
             // These usually end in number, but check letter equivalent just in case is rare? No, standard says number.
             // Let's stick to standard strict or slightly lenient?
             // Implementing slightly lenient for "JABCDEFGHI" mapping if digit doesn't match
             char altChar = "JABCDEFGHI".charAt(controlDigit);
             return lastChar == altChar;
        }
        
        return false;
    }

    // MÉTODO MODIFICADO: Registro directo en backend
    private void registerWithBackend() {
        btnRegister.setText("");
        progressBar.setVisibility(View.VISIBLE);

        OrganizationRegisterRequest req =
                new OrganizationRegisterRequest(
                        getText(etCif), getText(etName), getText(etEmail), getText(etPassword),
                        getText(etPhone), getText(etAddress), getText(etLocality), getText(etPostalCode),
                        getText(etDescription), actSector.getText().toString()
                );

        APIClient.getAuthAPIService().registerOrganization(req).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<Void> call, @NonNull retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    StatusHelper.showStatus(getContext(), "¡Éxito!", "Organización creada", false);
                    if (getParentFragmentManager() != null) {
                        getParentFragmentManager().popBackStack();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                } else {
                    // MANEJO DE ERRORES INTELIGENTE
                    String errorMessage = "Error en el registro";
                    try {
                        String errorBody = response.errorBody().string();
                        JSONObject jsonError = new JSONObject(errorBody);
                        if (jsonError.has("error")) {
                            errorMessage = jsonError.getString("error");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (response.code() == 409) {
                        StatusHelper.showStatus(getContext(), "Error", errorMessage, true);
                    } else {
                        StatusHelper.showStatus(getContext(), "Error servidor", errorMessage, true);
                    }
                    btnRegister.setText(btnText);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<Void> call, @NonNull Throwable t) {
                StatusHelper.showStatus(getContext(), "Error conexión", t.getMessage(), true);
                btnRegister.setText(btnText);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private String getText(TextInputEditText et) { return Objects.requireNonNull(et.getText()).toString().trim(); }
}