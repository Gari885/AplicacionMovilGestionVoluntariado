package com.example.appgestionvoluntariado.Fragments.RegistroYFormularios;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.appgestionvoluntariado.R;

import java.util.HashMap;
import java.util.Map;


public class VoluntarioRegistrarseFragment extends Fragment {

    private Button registrado;

    private FirebaseAuth mAuth;

    private Button volver;

    private EditText email, contraseña;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registrarse_voluntario, container, false);

        registrado = view.findViewById(R.id.btnRegistrar);
        email = view.findViewById(R.id.etCorreo);
        contraseña = view.findViewById(R.id.etPassword);
        mAuth = FirebaseAuth.getInstance();


        registrado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificarFormulario()){
                    registrarVoluntario(v,email.getText().toString(),contraseña.getText().toString(), "voluntario");

                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.containerFragments, new LogInFragment())
                            .addToBackStack(null) // <--- IMPORTANTE: Para que el botón 'Atrás' del móvil te devuelva al menú
                            .commit();
                }

            }
        });

        volver = view.findViewById(R.id.btnVolver);
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.containerFragments, new MenuRegistrarseFragment())
                        .addToBackStack(null) // <--- IMPORTANTE: Para que el botón 'Atrás' del móvil te devuelva al menú
                        .commit();
            }
        });

        return view;
    }

    private void registrarVoluntario(View v,String email, String password, String tipoUsuario) {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // El usuario se creó en Auth, ahora guardamos sus datos extra en Firestore
                    FirebaseUser user = mAuth.getCurrentUser();
                    guardarDatosUsuario(v,user.getUid(), email, tipoUsuario);
                } else {
                    Toast.makeText(v.getContext(), "Error en registro", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void guardarDatosUsuario(View v,String uid, String email, String tipoUsuario) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Crear un objeto con los datos (usamos un Map simple)
        Map<String, Object> datosUsuario = new HashMap<>();
        datosUsuario.put("email", email);
        datosUsuario.put("rol", "voluntario"); // Aquí guardamos "admin" o "voluntario"

        // Guardar en la colección "usuarios", usando el UID como nombre del documento
        db.collection("usuarios").document(uid)
                .set(datosUsuario)
                .addOnFailureListener(e -> {
                    Toast.makeText(volver.getContext(), "Error al guardar datos", Toast.LENGTH_SHORT).show();
                });
    }

    private boolean verificarFormulario() {
        return true;
    }
}