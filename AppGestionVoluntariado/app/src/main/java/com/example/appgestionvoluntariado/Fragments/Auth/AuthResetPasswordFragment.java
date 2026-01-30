package com.example.appgestionvoluntariado.Fragments.Auth;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.appgestionvoluntariado.Models.Request.ForgotPasswordRequest;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Utils.StatusHelper;
import com.google.android.material.appbar.MaterialToolbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthResetPasswordFragment extends Fragment {

    private EditText etEmail;
    private Button btnReset;
    private ProgressBar pbLoading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_auth_reset_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupListeners();
    }

    private void initViews(View v) {
        etEmail = v.findViewById(R.id.etEmail);
        btnReset = v.findViewById(R.id.btnReset);
        pbLoading = v.findViewById(R.id.pbLoading);
        
        MaterialToolbar toolbar = v.findViewById(R.id.topAppBar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(view -> {
                if (getParentFragmentManager() != null) {
                    getParentFragmentManager().popBackStack();
                }
            });
        }
    }

    private void setupListeners() {
        btnReset.setOnClickListener(v -> handleReset());
    }

    private void handleReset() {
        String email = etEmail.getText().toString().trim();

        if (email.isEmpty()) {
            StatusHelper.showStatus(getContext(), "Email requerido", "Por favor, introduce tu email.", true);
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            StatusHelper.showStatus(getContext(), "Email inválido", "El formato del correo no es correcto.", true);
            return;
        }

        toggleLoading(true);

        ForgotPasswordRequest request = new ForgotPasswordRequest(email);

        APIClient.getAuthAPIService().forgotPassword(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                toggleLoading(false);
                // Si es 200 OK, la API ha enviado el correo.
                // Si es 404 Not Found u otro error, la API puede decidir no revelarlo por seguridad, 
                // o enviar un 200 genérico. Asumimos comportamiento estándar: 
                // "Si el usuario existe, se enviará un correo".
                
                if (response.isSuccessful()) {
                    StatusHelper.showStatus(getContext(), "Solicitud recibida", 
                            "Si el correo está registrado, recibirás un enlace de recuperación.", false);
                    if (getParentFragmentManager() != null) {
                        getParentFragmentManager().popBackStack();
                    }
                } else {
                    // Manejar errores si es necesario. Por ahora mostramos mensaje genérico o el error del servidor.
                    // Para seguridad (user enumeration), idealmente mostraríamos el mismo mensaje de éxito.
                    // Pero si el servidor devuelve error explícito (ej 500), avisamos.
                    StatusHelper.showStatus(getContext(), "Error en el servidor", 
                            "Inténtalo de nuevo más tarde (" + response.code() + ")", true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                toggleLoading(false);
                StatusHelper.showStatus(getContext(), "Error de conexión", 
                        "Verificia tu conexión a internet.", true);
            }
        });
    }

    private void toggleLoading(boolean isLoading) {
        if (pbLoading == null) return;
        pbLoading.setVisibility(isLoading ? View.VISIBLE : View.INVISIBLE);
        btnReset.setEnabled(!isLoading);
        btnReset.setText(isLoading ? "" : "Enviar Correo");
    }
}