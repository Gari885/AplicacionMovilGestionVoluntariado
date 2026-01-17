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
    private TextView tvRegisterPrompt;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

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
    }

    private void setupListeners() {
        tvRegisterPrompt.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new RegisterMenuFragment())
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
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            if (user.isEmailVerified()) {
                                fetchUserRole(user.getUid());
                            } else {
                                StatusHelper.showStatus(getContext(), "Verificación pendiente", "Debes verificar tu correo antes de entrar", true);
                                mAuth.signOut();
                            }
                        }
                    } else {
                        StatusHelper.showStatus(getContext(), "Error de acceso", "Credenciales incorrectas o fallo de red", true);
                    }
                });
    }

    private void fetchUserRole(String uid) {
        // Fetch role from Firestore "usuarios" collection [cite: 2026-01-16]
        db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("rol");
                        if (role != null) {
                            handleRoleRedirection(role.toLowerCase().trim());
                        }
                    } else {
                        StatusHelper.showStatus(getContext(), "Error", "No se encontró perfil de usuario", true);
                    }
                })
                .addOnFailureListener(e -> StatusHelper.showStatus(getContext(), "Error", "Fallo al conectar con la base de datos", true));
    }

    private void handleRoleRedirection(String role) {
        String email = etEmail.getText().toString().trim();

        switch (role) {
            case "voluntario":
                checkVolunteerStatusAndLogin(email);
                break;
            case "organizacion":
                startActivityAndFinish(OrganizationActivity.class);
                break;
            case "admin":
                startActivityAndFinish(AdminActivity.class);
                break;
            default:
                StatusHelper.showStatus(getContext(), "Error", "Rol no reconocido: " + role, true);
        }
    }

    /**
     * Logic for volunteer verification: Must be 'Aceptado' to enter [cite: 2026-01-16].
     */
    private void checkVolunteerStatusAndLogin(String email) {
        APIClient.getFindVolunteerService().getVolunteer(email).enqueue(new Callback<Volunteer>() {
            @Override
            public void onResponse(@NonNull Call<Volunteer> call, @NonNull Response<Volunteer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Volunteer volunteer = response.body();

                    // The "Concept": Block if not accepted [cite: 2026-01-16]
                    if ("Aceptado".equalsIgnoreCase(volunteer.getStatus())) {
                        // Success - Save session and enter
                        // GlobalSession.loginVolunteer(volunteer);
                        startActivityAndFinish(VolunteerActivity.class);
                    } else {
                        mAuth.signOut();
                        StatusHelper.showStatus(getContext(), "Cuenta Pendiente",
                                "Tu cuenta aún no ha sido verificada por un administrador. Te avisaremos por email.", true);
                    }
                } else {
                    mAuth.signOut();
                    StatusHelper.showStatus(getContext(), "Error", "No se pudo obtener tu información de voluntario", true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Volunteer> call, @NonNull Throwable t) {
                mAuth.signOut();
                StatusHelper.showStatus(getContext(), "Error de red", "No se pudo verificar tu estado", true);
            }
        });
    }

    private void startActivityAndFinish(Class<?> activityClass) {
        Intent intent = new Intent(getContext(), activityClass);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}