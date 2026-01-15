package com.example.appgestionvoluntariado.Fragments.Auth;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.appgestionvoluntariado.Activities.OrganizationActivity;
import com.example.appgestionvoluntariado.Activities.AdminActivity;
import com.example.appgestionvoluntariado.Activities.VolunteerActivity;
import com.example.appgestionvoluntariado.Models.Volunteer;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Services.FindVolunteerService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    private TextView txtRegister;
    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String emailRegex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auth_login, container, false);

        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        txtRegister = view.findViewById(R.id.tvSignupPrompt);
        btnLogin = view.findViewById(R.id.btnLogin);

        txtRegister.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new RegisterMenuFragment()) // Using new fragment
                    .addToBackStack(null)
                    .commit();
        });

        btnLogin.setOnClickListener(v -> {
            Context context = v.getContext();
            String emailInput = etEmail.getText().toString().trim();
            String passInput = etPassword.getText().toString().trim();
            String error = "";

            if (emailInput.isEmpty() || passInput.isEmpty()) {
                error = "No puedes dejar los campos vacíos";
            } else if (!emailInput.matches(emailRegex)) {
                error = "Introduce un correo válido";
            }

            if (error.equals("")) {
                performLogin(context, emailInput, passInput);
            } else {
                showErrorDialog(context, error);
            }
        });

        return view;
    }

    private void performLogin(Context context, String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                if (user.isEmailVerified()) {
                                    checkUserRole(context, user.getUid());
                                } else {
                                    user.sendEmailVerification();
                                    showErrorDialog(context, "Debes verificar tu correo electrónico antes de iniciar sesión. Revisa tu bandeja de entrada (y Spam).");
                                    mAuth.signOut();
                                }
                            }
                        } else {
                            showErrorDialog(context, "Credenciales incorrectas o error de conexión.");
                        }
                    }
                });
    }

    private void checkUserRole(Context context, String uid) {
        db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("rol");
                        if (role != null) {
                            redirectUser(context, role);
                        } else {
                            showErrorDialog(context, "El usuario no tiene un rol asignado.");
                        }
                    } else {
                        showErrorDialog(context, "Usuario no encontrado en la base de datos.");
                    }
                })
                .addOnFailureListener(e -> {
                    showErrorDialog(context, "Error al conectar con la base de datos: " + e.getMessage());
                });
    }

    private void redirectUser(Context context, String role) {
        Intent intent = null;
        String normalizedRole = role.trim().toLowerCase();
        String email = etEmail.getText().toString();

        switch (normalizedRole) {
            case "voluntario":
                FindVolunteerService findVolunteerService = APIClient.getFindVolunteerService();
                findVolunteerService.getVolunteer(email).enqueue(new Callback<Volunteer>() {
                    @Override
                    public void onResponse(Call<Volunteer> call, Response<Volunteer> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            GlobalSession.loginVolunteer(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<Volunteer> call, Throwable t) {
                    }
                });
                intent = new Intent(context, VolunteerActivity.class);
                break;

            case "admin":
                GlobalSession.loginAdmin();
                intent = new Intent(context, AdminActivity.class);
                break;
            case "organizacion":
                intent = new Intent(context, OrganizationActivity.class);
                break;
            default:
                showErrorDialog(context, "Rol de usuario desconocido: " + role);
                return;
        }

        if (intent != null) {
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
        }
    }

    private void showErrorDialog(Context context, String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View popupView = LayoutInflater.from(context).inflate(R.layout.dialog_error_message, null);

        TextView txtError = popupView.findViewById(R.id.tvErrorMessage);
        LinearLayout btnClose = popupView.findViewById(R.id.btnClosePopup);

        txtError.setText(errorMessage);
        builder.setView(popupView);
        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        btnClose.setOnClickListener(x -> dialog.dismiss());
        dialog.show();
    }
}