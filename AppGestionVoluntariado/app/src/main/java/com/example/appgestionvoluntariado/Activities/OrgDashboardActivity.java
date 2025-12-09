package com.example.appgestionvoluntariado.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appgestionvoluntariado.Adapters.StatAdapter;
import com.example.appgestionvoluntariado.Models.Stat;
import com.example.appgestionvoluntariado.R;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class OrgDashboardActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

    private StatAdapter statAdapter;

    private ArrayList<Stat> stats;

    private ImageView logo;

    private DrawerLayout drawerLayout;
    private ImageView btnMenuHamburguesa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_org_dashboard);

        recyclerView = findViewById(R.id.recyclerViewDashboard);
        drawerLayout = findViewById(R.id.drawerLayout);
        btnMenuHamburguesa = findViewById(R.id.ivMenu);

        btnMenuHamburguesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // GravityCompat.END porque el menú está a la derecha
                    drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        stats = new ArrayList<>();
        stats.add(new Stat("VOLUNTARIOS", 2, "+2 Pendientes", "voluntarios"));
        stats.add(new Stat("ORGANIZACIONES", 2, "+2 Pendientes", "organizacion"));
        stats.add(new Stat("TOTAL MATCHES", 2, "", "matches"));
        stats.add(new Stat("PENDIENTES", 4, "", "pendientes"));

        statAdapter = new StatAdapter(stats);

        recyclerView.setAdapter(statAdapter);

        recyclerView.setLayoutManager(new GridLayoutManager(this,2));

        logo = findViewById(R.id.ivLogo);

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrgDashboardActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_voluntarios) {
                Intent intent = new Intent(OrgDashboardActivity.this, OrgVoluntariosActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_organizacion) {
                Intent intent = new Intent(OrgDashboardActivity.this, OrgOrganizacionesActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_matches){
                Intent intent = new Intent(OrgDashboardActivity.this, OrgMatchesMenuActivity.class);
                startActivity(intent);
            }else if (id == R.id.nav_voluntariados){
                Intent intent = new Intent(OrgDashboardActivity.this, OrgVoluntariadosActivity.class);
                startActivity(intent);
            }else if (id == R.id.nav_dashboard){
                Intent intent = new Intent(OrgDashboardActivity.this, OrgDashboardActivity.class);
                startActivity(intent);
            }
            drawerLayout.closeDrawer(GravityCompat.END); // Cerrar menú
            return true;
        });

    }
}