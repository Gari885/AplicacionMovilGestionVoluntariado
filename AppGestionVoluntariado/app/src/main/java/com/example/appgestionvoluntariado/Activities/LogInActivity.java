package com.example.appgestionvoluntariado.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appgestionvoluntariado.R;

import java.util.HashMap;
import java.util.Map;

public class LogInActivity extends AppCompatActivity {
    private ImageView logoImagen;
    private TextView txtRegistrar;

    private EditText correo;

    private EditText contraseña;
    private Button login;

    private Map<String,String> credenciales ;



    private String regex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);

        credenciales = new HashMap<>();
        credenciales.put("admin@gmail.com", "admin");
        credenciales.put("usuario","usuario");
        credenciales.put("org","org");

        correo = findViewById(R.id.editTextTextEmailAddress);
        contraseña = findViewById(R.id.editTextTextPassword);

        logoImagen = findViewById(R.id.ivLogo);
        logoImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        txtRegistrar = findViewById(R.id.registar);
        txtRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogInActivity.this, RegistrarseActivity.class);
                startActivity(intent);
            }
        });


        login = findViewById(R.id.btnLogin);
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
                    logearUsuario(correo.getText().toString(),contraseña.getText().toString());
                }else {
                    invocarError(context,error);
                }

            }
        });


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

    private void logearUsuario(String email, String contraseña){
        if (credenciales.containsKey(email)) {
            Intent intent = new Intent(LogInActivity.this, OrganizadorActivity.class);
            startActivity(intent);
        }

    }
}