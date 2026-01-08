package com.example.appgestionvoluntariado.Fragments.Auth;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.appgestionvoluntariado.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class OrganizationRegisterFragment extends Fragment {

    private Button btnRegister;
    private Button btnBack;
    private FirebaseAuth mAuth;

    private TextInputLayout tilName, tilEmail, tilSector, tilZone, tilDescription, tilPassword;
    private TextInputEditText etName, etEmail, etSector, etZone, etDescription, etPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organization_register, container, false);

        etName = view.findViewById(R.id.etNombre);
        etEmail = view.findViewById(R.id.etCorreo);
        etSector = view.findViewById(R.id.etSector);
        etZone = view.findViewById(R.id.etZona);
        etDescription = view.findViewById(R.id.etDescripcion);
        etPassword = view.findViewById(R.id.etPassword);

        tilName = view.findViewById(R.id.tilNombre);
        tilEmail = view.findViewById(R.id.tilCorreo);
        tilSector = view.findViewById(R.id.tilSector);
        tilZone = view.findViewById(R.id.tilZona);
        tilDescription = view.findViewById(R.id.tilDescripcion);
        tilPassword = view.findViewById(R.id.tilPassword);

        btnRegister = view.findViewById(R.id.btnRegistrar);
        btnRegister.setOnClickListener(v -> {
            if (isFormValid()){
                registerOrganization(v, etEmail.getText().toString(), etPassword.getText().toString(), "organizacion");
            }
        });

        btnBack = view.findViewById(R.id.btnVolver);
        btnBack.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new RegisterMenuFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private boolean isFormValid() {
        boolean isValid = true;

        String name = etName.getText().toString().trim();
        if (name.isEmpty()) {
            tilName.setError("No puedes dejar el campo vacío");
            isValid = false;
        } else if (!name.matches("^[a-zA-ZñÑáéíóúÁÉÍÓÚ\\s]+$")) {
            tilName.setError("El nombre solo puede contener letras");
            isValid = false;
        } else {
            tilName.setError(null);
        }

        String email = etEmail.getText().toString().trim();
        if (email.isEmpty()) {
            tilEmail.setError("No puedes dejar el campo vacío");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Introduce un correo válido");
            isValid = false;
        } else {
            tilEmail.setError(null);
        }

        String password = etPassword.getText().toString().trim();
        if (password.isEmpty()) {
            tilPassword.setError("No puedes dejar el campo vacío");
            isValid = false;
        } else if (!checkPassword(password)) {
            tilPassword.setError("La contraseña debe tener minimo 10 caracteres, una mayuscula, una minuscula, un numero y un caracter especial");
            isValid = false;
        } else {
            tilPassword.setError(null);
        }

        String sector = etSector.getText().toString().trim();
        if (sector.isEmpty()) {
            tilSector.setError("No puedes dejar el campo vacío");
            isValid = false;
        } else if (!sector.matches("^[a-zA-ZñÑáéíóúÁÉÍÓÚ\\s]+$")) {
            tilSector.setError("Solo letras permitidas");
            isValid = false;
        } else {
            tilSector.setError(null);
        }

        if (etZone.getText().toString().trim().isEmpty()) {
            tilZone.setError("No puedes dejar el campo vacío");
            isValid = false;
        } else {
            tilZone.setError(null);
        }

        if (etDescription.getText().toString().trim().isEmpty()) {
            tilDescription.setError("Cuéntanos algo sobre la organización");
            isValid = false;
        } else {
            tilDescription.setError(null);
        }

        return isValid;
    }

    private void registerOrganization(View v, String email, String password, String userType) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    showErrorDialog(getContext(), "Cuenta creada. Por favor, revisa tu correo para verificar tu identidad antes de entrar.");
                                    saveUserData(v, user.getUid(), email, userType);
                                    getParentFragmentManager().beginTransaction()
                                            .replace(R.id.fragmentContainer, new LoginFragment())
                                            .commit();
                                } else {
                                    showErrorDialog(getContext(), "No se pudo enviar el correo de verificación.");
                                }
                            }
                        });
                    }
                } else {
                    String errorMessage = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthUserCollisionException e) {
                        errorMessage = "Ese correo ya está registrado en otra cuenta.";
                    } catch (FirebaseNetworkException e) {
                        errorMessage = "No tienes conexión a internet.";
                    } catch (Exception e) {
                        errorMessage = "Error desconocido: " + e.getMessage();
                    }
                    showErrorDialog(getContext(), errorMessage);
                }
            }
        });
    }

    private void saveUserData(View v, String uid, String email, String userType) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("rol", userType);

        db.collection("usuarios").document(uid)
                .set(userData)
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al guardar datos", Toast.LENGTH_SHORT).show();
                });
    }

    private boolean checkPassword(String password) {
        boolean hasLowerCase = false;
        boolean hasUpperCase = false;
        boolean hasSpecialChar = false;
        boolean hasDigit = false;
        boolean hasMinLength = password.length() >= 10;

        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) hasLowerCase = true;
            else if (Character.isUpperCase(c)) hasUpperCase = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecialChar = true;
        }

        return hasLowerCase && hasUpperCase && hasSpecialChar && hasDigit && hasMinLength;
    }

    private void showErrorDialog(Context context, String error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View popupView = LayoutInflater.from(context).inflate(R.layout.dialog_error_message, null);

        TextView txtError = popupView.findViewById(R.id.tvErrorMessage);
        LinearLayout btnClose = popupView.findViewById(R.id.btnClosePopup);

        txtError.setText(error);
        builder.setView(popupView);
        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        btnClose.setOnClickListener(x -> dialog.dismiss());
        dialog.show();
    }
}
