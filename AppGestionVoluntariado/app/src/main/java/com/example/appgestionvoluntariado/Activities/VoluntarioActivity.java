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

public class VoluntarioActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageView btnMenuHamburguesa, ajustesUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_voluntario);

        btnMenuHamburguesa = findViewById(R.id.ivMenu);
        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);


        btnMenuHamburguesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // GravityCompat.END porque el menú está a la derecha
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        ajustesUsuario = findViewById(R.id.opcUsuario);
        ajustesUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new PerfilUsuarioFragment()) // <--- Aquí cargas el Dashboard
                        .commit();
            }
        });

        // 2. Lógica de navegación del Menú
        navigationView.setNavigationItemSelectedListener(item -> {
            Fragment fragmentSeleccionado = null;
            int id = item.getItemId();

            if (id == R.id.nav_voluntarios) {
                fragmentSeleccionado = new VoluntariadosVolFragment();
            } else if (id == R.id.nav_misVoluntariados) {
                fragmentSeleccionado = new MisVoluntariadosVolFragment();
            }

            // Cambiar el fragmento
            if (fragmentSeleccionado != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragmentSeleccionado)
                        .commit();
            }
            drawerLayout.closeDrawer(GravityCompat.END);
            return true;
        });

        // 3. ¡IMPORTANTE! CARGAR EL DASHBOARD AL INICIO
        // Si es la primera vez que se abre la actividad (no es una rotación de pantalla)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new VoluntariadosVolFragment()) // <--- Aquí cargas el Dashboard
                    .commit();
            navigationView.setCheckedItem(R.id.nav_voluntariados); // Marcar la opción en azul
        }
    }
}