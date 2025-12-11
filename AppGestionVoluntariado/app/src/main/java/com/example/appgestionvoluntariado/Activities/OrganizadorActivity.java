package com.example.appgestionvoluntariado.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.appgestionvoluntariado.Fragments.DashboardFragment;
import com.example.appgestionvoluntariado.Fragments.MatchesMenuFragment;
import com.example.appgestionvoluntariado.Fragments.OrgVoluntariadosFragment;
import com.example.appgestionvoluntariado.Fragments.OrganizacionesFragment;
import com.example.appgestionvoluntariado.Fragments.VoluntariosFragment;
import com.example.appgestionvoluntariado.R;
import com.google.android.material.navigation.NavigationView;

public class OrganizadorActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageView btnMenuHamburguesa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_organizador);
        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);

        btnMenuHamburguesa = findViewById(R.id.ivMenu);

        btnMenuHamburguesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // GravityCompat.END porque el menú está a la derecha
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });


        // 2. Lógica de navegación del Menú
        navigationView.setNavigationItemSelectedListener(item -> {
            Fragment fragmentSeleccionado = null;
            int id = item.getItemId();

            if (id == R.id.nav_voluntarios) {
                fragmentSeleccionado = new VoluntariosFragment();
            } else if (id == R.id.nav_organizacion) {
                fragmentSeleccionado = new OrganizacionesFragment();
            } else if (id == R.id.nav_matches){
                fragmentSeleccionado = new MatchesMenuFragment();
            }else if (id == R.id.nav_voluntariados){
                fragmentSeleccionado = new OrgVoluntariadosFragment();
            }else if (id == R.id.nav_dashboard){
                fragmentSeleccionado = new DashboardFragment();
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
                    .replace(R.id.fragment_container, new DashboardFragment()) // <--- Aquí cargas el Dashboard
                    .commit();
            navigationView.setCheckedItem(R.id.nav_dashboard); // Marcar la opción en azul
        }
    }
}