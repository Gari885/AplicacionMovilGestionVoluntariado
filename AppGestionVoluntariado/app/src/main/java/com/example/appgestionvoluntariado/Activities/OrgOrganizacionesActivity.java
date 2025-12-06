package com.example.appgestionvoluntariado.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.appgestionvoluntariado.R;
import com.google.android.material.navigation.NavigationView;

public class OrgOrganizacionesActivity extends AppCompatActivity {
    private Button crearOrganizacion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_org_organizaciones);
        crearOrganizacion = findViewById(R.id.btnAnadirOrg);

        crearOrganizacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrgOrganizacionesActivity.this,OrgRegistroActivity.class);
                intent.putExtra("tipo", "Añadir");
                startActivity(intent);
            }
        });


    }
}