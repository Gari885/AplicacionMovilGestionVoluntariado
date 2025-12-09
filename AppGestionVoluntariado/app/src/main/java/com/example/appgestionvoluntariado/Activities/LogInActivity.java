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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.appgestionvoluntariado.R;

public class LogInActivity extends AppCompatActivity {
    private ImageView logoImagen;
    private TextView txtRegistrar;

    private EditText correo;

    private EditText contraseña;
    private Button login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);

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
                if (correo.getText().toString().isEmpty() || contraseña.getText().toString().isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View popupView = LayoutInflater.from(context).inflate(R.layout.dialog_mensaje_error,null);
                    TextView msnError = popupView.findViewById(R.id.mensajeError);
                    LinearLayout cerrar = popupView.findViewById(R.id.btnCerrarPopup);

                    msnError.setText("No puedes dejar ningun campo vacio");
                    builder.setView(popupView);
                    AlertDialog dialog = builder.create();
                    if(dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    cerrar.setOnClickListener(x -> dialog.dismiss());
                    dialog.show();

                }else {
                    Intent intent = new Intent(LogInActivity.this, OrgDashboardActivity.class);
                    startActivity(intent);
                }

            }
        });


    }

    private void invocarError(String error){

    }
}