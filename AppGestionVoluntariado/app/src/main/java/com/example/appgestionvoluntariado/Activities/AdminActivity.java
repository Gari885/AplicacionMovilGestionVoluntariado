package com.example.appgestionvoluntariado.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.appgestionvoluntariado.Fragments.Admin.AdminDashboardFragment;
import com.example.appgestionvoluntariado.Fragments.Admin.AdminMatchesMenuFragment;
import com.example.appgestionvoluntariado.Fragments.Admin.AdminOrganizationListFragment;
import com.example.appgestionvoluntariado.Fragments.Admin.AdminVolunteerListFragment;
import com.example.appgestionvoluntariado.Fragments.Admin.OrganizationExploreFragment;
import com.example.appgestionvoluntariado.R;
import com.google.android.material.navigation.NavigationView;

public class AdminActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageView btnHamburgerMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_main);
        
        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        btnHamburgerMenu = findViewById(R.id.ivMenu);

        btnHamburgerMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));

        navigationView.setNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_voluntarios) {
                selectedFragment = new AdminVolunteerListFragment();
            } else if (id == R.id.nav_organizacion) {
                selectedFragment = new AdminOrganizationListFragment();
            } else if (id == R.id.nav_matches){
                selectedFragment = new AdminMatchesMenuFragment();
            } else if (id == R.id.nav_voluntariados){
                selectedFragment = new OrganizationExploreFragment(); // Maps to OrgVoluntariadosFragment
            } else if (id == R.id.nav_dashboard){
                selectedFragment = new AdminDashboardFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, selectedFragment)
                        .commit();
            }
            drawerLayout.closeDrawer(GravityCompat.END);
            return true;
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new AdminDashboardFragment())
                    .commit();
            navigationView.setCheckedItem(R.id.nav_dashboard);
        }
    }
}
