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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appgestionvoluntariado.Activities.OrganizacionActivity;
import com.example.appgestionvoluntariado.Activities.OrganizadorActivity;
import com.example.appgestionvoluntariado.Activities.VoluntarioActivity;
import com.example.appgestionvoluntariado.SesionGlobal;
import com.example.appgestionvoluntariado.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class LogInFragment extends Fragment {


    private ImageView logoImagen;
    private TextView txtRegistrar;

    private EditText correo;

    private EditText contraseña;
    private Button login;

    private Map<String,String> credenciales ;

    private FirebaseAuth mAuth;

    private String regex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_log_in, container, false);
        credenciales = new HashMap<>();
        credenciales.put("admin@gmail.com", "admin");
        credenciales.put("usuario@gmail.com","usuario");
        credenciales.put("organizacion@gmail.com","org");

        correo = view.findViewById(R.id.editTextTextEmailAddress);
        contraseña = view.findViewById(R.id.editTextTextPassword);
        mAuth = FirebaseAuth.getInstance();


        txtRegistrar = view.findViewById(R.id.registar);
        txtRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.containerFragments, new MenuRegistrarseFragment())
                        .addToBackStack(null) // <--- IMPORTANTE: Para que el botón 'Atrás' del móvil te devuelva al menú
                        .commit();
                }
        });


        login = view.findViewById(R.id.btnLogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                String error = "";
                if (correo.getText().toString().isEmpty() || contraseña.getText().toString().isEmpty()) {
                    error = "No puedes dejar los campos vacios";
                }else if (!correo.getText().toString().matches(regex)){
                    error = "Introduce un correo valido";
                }

                if (error.equals("")){
                    logearUsuario(correo.getText().toString(),contraseña.getText().toString(),v);
                }else {
                    invocarError(context,error);
                }

            }
        });

        return view;
    }
    private void invocarError(Context context, String error){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View popupView = LayoutInflater.from(context).inflate(R.layout.dialog_mensaje_error,null);
        TextView msnError = popupView.findViewById(R.id.mensajeError);
        LinearLayout cerrar = popupView.findViewById(R.id.btnCerrarPopup);

        msnError.setText(error);
        builder.setView(popupView);
        AlertDialog dialog = builder.create();
        if(dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cerrar.setOnClickListener(x -> dialog.dismiss());
        dialog.show();
    }

    private void logearUsuario(String email, String contraseña,View view){
        Context context = view.getContext();
        if (credenciales.containsKey(email)) {
            if (email.equals("admin@gmail.com") && credenciales.containsValue(contraseña)) {
                SesionGlobal.setContrasena(contraseña);
                Intent intent = new Intent(context, OrganizadorActivity.class);
                startActivity(intent);
            }
            if (email.equals("usuario@gmail.com") && credenciales.containsValue(contraseña)){
                SesionGlobal.setContrasena(contraseña);
                SesionGlobal.iniciarSesionVol();
                Intent intent = new Intent(context, VoluntarioActivity.class);
                startActivity(intent);

            }

            if (email.equals("organizacion@gmail.com") && credenciales.containsValue(contraseña)){
                SesionGlobal.setContrasena(contraseña);
                SesionGlobal.iniciarSesionOrg();
                Intent intent = new Intent(context, OrganizacionActivity.class);
                startActivity(intent);

            }
        }

    }

    private void iniciarSesion(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Login correcto, ahora verificamos qué rol tiene
                            FirebaseUser user = mAuth.getCurrentUser();
                            verificarRol(user.getUid());
                    }
                });
        }
    }
                
    }

    private void verificarRol(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Buscamos el documento del usuario por su ID
        db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Obtenemos el campo "rol"
                        String rol = documentSnapshot.getString("rol");

                        // Redirigimos según el rol
                        redirigirUsuario(rol);
                    }
                });
    }

    private void redirigirUsuario(String rol) {
        Intent intent;
        if ("admin".equals(rol)) {
            intent = new Intent(MainActivity.this, AdminActivity.class);
        } else {
            intent = new Intent(MainActivity.this, VoluntarioActivity.class);
        }
        startActivity(intent);
        finish();
    }
}