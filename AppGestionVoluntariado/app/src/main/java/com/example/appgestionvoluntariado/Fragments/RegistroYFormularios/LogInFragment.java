package com.example.appgestionvoluntariado.Fragments.RegistroYFormularios;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.appgestionvoluntariado.Activities.OrganizacionActivity;
import com.example.appgestionvoluntariado.Activities.OrganizadorActivity;
import com.example.appgestionvoluntariado.Activities.VoluntarioActivity;
import com.example.appgestionvoluntariado.R;
// Si necesitas SesionGlobal para algo específico, descoméntalo,
// pero con Firebase Auth suele ser innecesario guardar la pass en local.
// import com.example.appgestionvoluntariado.SesionGlobal;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LogInFragment extends Fragment {

    // Elementos Log In
    private TextView txtRegistrar;
    private EditText correo;
    private EditText contraseña;
    private Button login;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // Regex para validar email
    private String regex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inicializar instancias de Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_log_in, container, false);

        // Vincular vistas
        correo = view.findViewById(R.id.editTextTextEmailAddress);
        contraseña = view.findViewById(R.id.editTextTextPassword);
        txtRegistrar = view.findViewById(R.id.registar);
        login = view.findViewById(R.id.btnLogin);

        // Botón ir a Registrarse
        txtRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.containerFragments, new MenuRegistrarseFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        // Botón Login
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                String emailInput = correo.getText().toString().trim();
                String passInput = contraseña.getText().toString().trim();
                String error = "";

                // Validaciones
                if (emailInput.isEmpty() || passInput.isEmpty()) {
                    error = "No puedes dejar los campos vacíos";
                } else if (!emailInput.matches(regex)) {
                    error = "Introduce un correo válido";
                }

                if (error.equals("")) {
                    // Si no hay errores locales, llamamos a Firebase
                    iniciarSesion(context, emailInput, passInput);
                } else {
                    // Si hay error de validación, mostramos el popup
                    invocarError(context, error);
                }
            }
        });

        return view;
    }

    // --- MÉTODOS AUXILIARES ---
    private void iniciarSesion(Context context, String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 1. Auth correcto. Obtenemos usuario
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // 2. Verificamos su rol en Firestore
                                verificarRol(context, user.getUid());
                            }
                        } else {
                            // Error en Firebase (contraseña mal, usuario no existe, etc)
                            String msg = "Error de autenticación";
                            if (task.getException() != null) {
                                msg = task.getException().getMessage(); // Mensaje técnico (opcional)
                            }
                            invocarError(context, "Credenciales incorrectas o error de conexión.");
                        }
                    }
                });
    }

    private void verificarRol(Context context, String uid) {
        // Buscamos el documento del usuario por su UID en la colección "usuarios"
        db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Obtenemos el campo "rol"
                        String rol = documentSnapshot.getString("rol");

                        // IMPORTANTE: Manejar el caso de que el rol sea null
                        if (rol != null) {
                            redirigirUsuario(context, rol);
                        } else {
                            invocarError(context, "El usuario no tiene un rol asignado.");
                        }
                    } else {
                        invocarError(context, "Usuario no encontrado en la base de datos.");
                    }
                })
                .addOnFailureListener(e -> {
                    invocarError(context, "Error al conectar con la base de datos.");
                });
    }

    private void redirigirUsuario(Context context, String rol) {
        Intent intent = null;

        // Normalizamos el string (quitamos espacios y ponemos minúsculas para comparar mejor)
        String rolNormalizado = rol.trim().toLowerCase();

        switch (rolNormalizado) {
            case "voluntario":
                // Si usabas SesionGlobal aquí, puedes ponerlo antes del intent
                intent = new Intent(context, VoluntarioActivity.class);
                break;
            case "organizador":
                intent = new Intent(context, OrganizadorActivity.class);
                break;
            case "organizacion":
                intent = new Intent(context, OrganizacionActivity.class);
                break;
            default:
                // Rol desconocido o "admin"
                // Puedes redirigir a una por defecto o mostrar error
                invocarError(context, "Rol de usuario desconocido: " + rol);
                return;
        }

        if (intent != null) {
            startActivity(intent);
            // IMPORTANTE: En fragmentos se usa requireActivity().finish()
            if (getActivity() != null) {
                getActivity().finish();
            }
        }
    }

    private void invocarError(Context context, String error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View popupView = LayoutInflater.from(context).inflate(R.layout.dialog_mensaje_error, null);

        TextView msnError = popupView.findViewById(R.id.mensajeError);
        LinearLayout cerrar = popupView.findViewById(R.id.btnCerrarPopup);

        msnError.setText(error);
        builder.setView(popupView);
        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        cerrar.setOnClickListener(x -> dialog.dismiss());
        dialog.show();
    }
}