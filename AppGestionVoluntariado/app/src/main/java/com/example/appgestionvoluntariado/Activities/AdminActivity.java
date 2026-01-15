package com.example.appgestionvoluntariado.Activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.appgestionvoluntariado.Fragments.Admin.AdminDashboardFragment;
import com.example.appgestionvoluntariado.Fragments.Admin.AdminOrganizationListFragment;
import com.example.appgestionvoluntariado.Fragments.Admin.AdminProfileHubFragment;
import com.example.appgestionvoluntariado.Fragments.Admin.AdminVolunteerListFragment;
import com.example.appgestionvoluntariado.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        // 1. Configurar Toolbar y Perfil (Icono arriba a la derecha)
        Toolbar topAppBar = findViewById(R.id.topAppBarAdmin);
        topAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_user) {
                replaceFragment(new AdminProfileHubFragment());
                return true;
            }
            return false;
        });

        // 2. Configurar Bottom Navigation (5 Secciones)
        BottomNavigationView bottomNav = findViewById(R.id.admin_bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_admin_dashboard) {
                selectedFragment = new AdminDashboardFragment();
            } else if (id == R.id.nav_admin_vols) {
                selectedFragment = new AdminVolunteerListFragment();
            } else if (id == R.id.nav_admin_projects) {
                selectedFragment = new OrganizationExploreFragment();
            } else if (id == R.id.nav_admin_orgs) {
                selectedFragment = new AdminOrganizationListFragment();
            } else if (id == R.id.nav_admin_matches) {
                selectedFragment = new AdminMatchesMenuFragment();
            }

            if (selectedFragment != null) {
                replaceFragment(selectedFragment);
            }
            return true;
        });

        // Pantalla inicial por defecto: Dashboard
        if (savedInstanceState == null) {
            replaceFragment(new AdminDashboardFragment());
            bottomNav.setSelectedItemId(R.id.nav_admin_dashboard);
        }
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}