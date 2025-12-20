package com.example.appgestionvoluntariado.Fragments.RegistroYFormularios;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.appgestionvoluntariado.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class OrganizacionRegistrarseFragment extends Fragment {

    private Button registrado;

    private Button volver;

    private FirebaseAuth mAuth;

    private EditText nombre,email,sector, zona,descripcion, contraseña;

    private String regex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registrarse_organizacion, container, false);

        nombre = view.findViewById(R.id.etNombre);
        email = view.findViewById(R.id.etCorreo);
        sector = view.findViewById(R.id.etSector);
        zona = view.findViewById(R.id.etZona);
        descripcion = view.findViewById(R.id.etDescripcion);
        mAuth = FirebaseAuth.getInstance();
        contraseña = view.findViewById(R.id.etPassword);


        registrado = view.findViewById(R.id.btnRegistrar);

        registrado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificarFormulario()){
                    registrarVoluntario(v,email.getText().toString(),contraseña.getText().toString(), "organizacion");

                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.containerFragments, new LogInFragment())
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

    private boolean verificarFormulario() {
        String error = "";
        String nombreVerificar = nombre.getText().toString();
        String emailVerificar = email.getText().toString();
        String sectorVerificar = sector.getText().toString();
        String zonaVerificar = zona.getText().toString();
        String descripcionVerificar = descripcion.getText().toString();

        if (nombreVerificar.isEmpty() || emailVerificar.isEmpty() || sectorVerificar.isEmpty()
                ||  zonaVerificar.isEmpty() || descripcionVerificar.isEmpty()){
            error = "No puedes dejar ningun campo vacio";
        }else {
            if (!nombreVerificar.matches("^[a-zA-ZñÑáéíóúÁÉÍÓÚ\\s]+$")) {
                error =  "El nombre solo puede contener letras";
                return false;
            }

            if (!emailVerificar.matches(regex)){
                error = "Debes introducir un email valido";
            }

            if (!sectorVerificar.matches("^[a-zA-ZñÑáéíóúÁÉÍÓÚ\\s]+$")) {
                error = "El sector solo puede contener letras";
                return false;
            }
        }

        if (error == ""){
            return true;
        }else {
            return false;
        }
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
        datosUsuario.put("rol", "organizacion"); // Aquí guardamos "admin" o "voluntario"

        // Guardar en la colección "usuarios", usando el UID como nombre del documento
        db.collection(tipoUsuario).document(uid)
                .set(datosUsuario)
                .addOnFailureListener(e -> {
                    Toast.makeText(volver.getContext(), "Error al guardar datos", Toast.LENGTH_SHORT).show();
                });
    }


}