package com.example.appgestionvoluntariado.Activities;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.appgestionvoluntariado.Fragments.Settings.ProfileUserFragment;
import com.example.appgestionvoluntariado.Fragments.Volunteer.VolunteerExploreFragment;
import com.example.appgestionvoluntariado.Fragments.Volunteer.VolunteerMyProjectsFragment;
import com.example.appgestionvoluntariado.R;
import com.google.android.material.navigation.NavigationView;

public class VolunteerActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageView btnHamburgerMenu, userSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_volunteer_main);

        btnHamburgerMenu = findViewById(R.id.ivMenu);
        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        userSettings = findViewById(R.id.ivUserProfile);

        btnHamburgerMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));

        userSettings.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new ProfileUserFragment())
                    .addToBackStack(null)
                    .commit();
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_voluntarios) {
                // nav_voluntarios IS confusing in Spanish menucode, but implies "Voluntariados" (Projects) for Volunteer view
                selectedFragment = new VolunteerExploreFragment(); 
            } else if (id == R.id.nav_misVoluntariados) {
                selectedFragment = new VolunteerMyProjectsFragment();
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
                    .replace(R.id.fragmentContainer, new VolunteerExploreFragment())
                    .commit();
            navigationView.setCheckedItem(R.id.nav_voluntarios);
        }
    }
}
