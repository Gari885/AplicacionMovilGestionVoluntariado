package com.example.appgestionvoluntariado.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.appgestionvoluntariado.R;

public class RegistrarseActivity extends AppCompatActivity {

    private ImageView logoImagen;
    private Button btnOrg;
    private Button btnVol;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrarse);

        logoImagen = findViewById(R.id.ivLogo);
        logoImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrarseActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnOrg = (Button) findViewById(R.id.btnOrganizador);
        btnOrg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrarseActivity.this, OrgRegistroActivity.class);
                startActivity(intent);
            }
        });

        btnVol = (Button) findViewById(R.id.btnVolutnario);
        btnVol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrarseActivity.this, VolRegistroActivity.class);
                startActivity(intent);
            }
        });

    }
}