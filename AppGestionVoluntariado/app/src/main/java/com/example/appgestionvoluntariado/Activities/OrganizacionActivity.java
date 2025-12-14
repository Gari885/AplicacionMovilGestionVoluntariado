package com.example.appgestionvoluntariado.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.appgestionvoluntariado.Fragments.Ajustes.PerfilUsuarioFragment;
import com.example.appgestionvoluntariado.Fragments.VistaVoluntario.MisVoluntariadosVolFragment;
import com.example.appgestionvoluntariado.Fragments.VistaVoluntario.VoluntariadosVolFragment;
import com.example.appgestionvoluntariado.R;
import com.google.android.material.navigation.NavigationView;

public class OrganizacionActivity extends AppCompatActivity {

    private ImageView  ajustesUsuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_organizacion);

        ajustesUsuario = findViewById(R.id.opcUsuario);
        ajustesUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new PerfilUsuarioFragment()) // <--- Aquí cargas el Dashboard
                        .commit();
            }
        });



    }
}