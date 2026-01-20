package com.example.appgestionvoluntariado.Fragments.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.appgestionvoluntariado.Activities.AdminActivity;
import com.example.appgestionvoluntariado.Activities.OrganizationActivity;
;
import com.example.appgestionvoluntariado.Activities.VolunteerActivity;
import com.example.appgestionvoluntariado.Models.Volunteer;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Utils.StatusHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Login logic in English, UI feedback in Spanish [cite: 2026-01-16].
 */
public class LoginFragment extends Fragment {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegisterPrompt, tvForgotPassword;
    private android.widget.ProgressBar pbLoading;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private int contadorFallosLogin;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auth_login, container, false);

        initViews(view);
        setupListeners();

        return view;
    }

    private void initViews(View view) {
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        tvRegisterPrompt = view.findViewById(R.id.tvSignupPrompt);
        pbLoading = view.findViewById(R.id.pbLoginLoading);
        tvForgotPassword = view.findViewById(R.id.tvForgotPassword);
        contadorFallosLogin = 0;
    }

    private void setupListeners() {
        tvRegisterPrompt.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, new RegisterMenuFragment())
                .addToBackStack(null)
                .commit();
        });

        tvForgotPassword.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new AuthResetPasswordFragment())
                    .addToBackStack(null)
                    .commit();
        });

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (validateInput(email, password)) {
                performLogin(email, password);
            }
        });
    }

    private boolean validateInput(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            StatusHelper.showStatus(getContext(), "Campos vacíos", "Por favor, rellena todos los datos", true);
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            StatusHelper.showStatus(getContext(), "Correo inválido", "Introduce un email correcto", true);
            return false;
        }
        return true;
    }

    private void performLogin(String email, String password) {
        toggleLoading(true);
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        fetchUserProfile();
                    }
                } else {
                    contadorFallosLogin++;
                    if (contadorFallosLogin >= 3) {
                        tvForgotPassword.setVisibility(View.VISIBLE);
                        StatusHelper.showStatus(getContext(), "Intentos fallidos", 
                            "Parece que tienes problemas. Puedes restablecer tu contraseña si lo necesitas.", true);
                    } else {
                        StatusHelper.showStatus(getContext(), "Error de acceso", "Credenciales incorrectas o fallo de red", true);
                    }
                    toggleLoading(false);
                }
            });
    }

    private void fetchUserProfile() {
        APIClient.getAuthAPIService().getProfile().enqueue(new Callback<com.example.appgestionvoluntariado.Models.ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<com.example.appgestionvoluntariado.Models.ProfileResponse> call, @NonNull Response<com.example.appgestionvoluntariado.Models.ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    processLogin(response.body());
                } else {
                    // Caso Raro: Usuario en Firebase pero no en SQL (o token invalido)
                    mAuth.signOut();
                    StatusHelper.showStatus(getContext(), "Cuenta Inconsistente", 
                        "Tu usuario no consta en la base de datos de Voluntariado.\nContacta con soporte o regístrate de nuevo.", true);
                    toggleLoading(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<com.example.appgestionvoluntariado.Models.ProfileResponse> call, @NonNull Throwable t) {
                mAuth.signOut();
                StatusHelper.showStatus(getContext(), "Error de Conexión", "No se pudo obtener tu perfil.\n" + t.getMessage(), true);
                toggleLoading(false);
            }
        });
    }

    private void processLogin(com.example.appgestionvoluntariado.Models.ProfileResponse profile) {
        String type = profile.getType(); 
        if (type == null) type = "";

        switch (type.toLowerCase().trim()) {
            case "voluntario":
                startActivityAndFinish(VolunteerActivity.class);
                break;
            case "organizacion":
                startActivityAndFinish(OrganizationActivity.class);
                break;
            case "admin":
                startActivityAndFinish(AdminActivity.class);
                break;
            default:
                mAuth.signOut();
                StatusHelper.showStatus(getContext(), "Error", "Tipo de usuario desconocido: " + type, true);
        }
    }

    private void startActivityAndFinish(Class<?> activityClass) {
        // No need to untoggle loading here as activity finishes
        Intent intent = new Intent(getContext(), activityClass);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    private void toggleLoading(boolean isLoading) {
        if (isLoading) {
            pbLoading.setVisibility(View.VISIBLE);
            btnLogin.setText("");
            btnLogin.setEnabled(false);
        } else {
            pbLoading.setVisibility(View.INVISIBLE);
            btnLogin.setText(getString(R.string.logIn));
            btnLogin.setEnabled(true);
        }
    }
}