package com.example.appgestionvoluntariado.Activities;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.appgestionvoluntariado.Fragments.Auth.CreateProjectFragment;
import com.example.appgestionvoluntariado.Fragments.Organization.OrgActivitiesFragment;
import com.example.appgestionvoluntariado.Fragments.Organization.OrgCreateProjectFragment;
import com.example.appgestionvoluntariado.Fragments.Organization.OrgProfileFragment;
import com.example.appgestionvoluntariado.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class OrganizationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization);

        BottomNavigationView bottomNav = findViewById(R.id.organization_bottom_navigation);

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_org_activities) {
                selectedFragment = new OrgActivitiesFragment();
            } else if (id == R.id.nav_org_create) {
                selectedFragment = new CreateProjectFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.organization_fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        // Pantalla de inicio por defecto
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.organization_fragment_container, new OrgActivitiesFragment())
                    .commit();
        }

    }


}