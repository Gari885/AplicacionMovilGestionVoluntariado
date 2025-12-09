package com.example.appgestionvoluntariado.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.appgestionvoluntariado.R;
import com.google.android.material.navigation.NavigationView;

public class OrgVoluntariadosActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageView btnMenuHamburguesa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_org_voluntariados);


        drawerLayout = findViewById(R.id.drawerLayout);
        btnMenuHamburguesa = findViewById(R.id.ivMenu);
        btnMenuHamburguesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // GravityCompat.END porque el menú está a la derecha
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_voluntarios) {
                Intent intent = new Intent(OrgVoluntariadosActivity.this, OrgVoluntariosActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_organizacion) {
                Intent intent = new Intent(OrgVoluntariadosActivity.this, OrgOrganizacionesActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_matches){
                Intent intent = new Intent(OrgVoluntariadosActivity.this, OrgMatchesMenuActivity.class);
                startActivity(intent);
            }else if (id == R.id.nav_voluntariados){
                Intent intent = new Intent(OrgVoluntariadosActivity.this, OrgVoluntariadosActivity.class);
                startActivity(intent);
            }else if (id == R.id.nav_dashboard){
                Intent intent = new Intent(OrgVoluntariadosActivity.this, OrgDashboardActivity.class);
                startActivity(intent);
            }
            drawerLayout.closeDrawer(GravityCompat.END); // Cerrar menú
            return true;
        });

    }
}