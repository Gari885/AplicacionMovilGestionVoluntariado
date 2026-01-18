package com.example.appgestionvoluntariado.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.appgestionvoluntariado.Fragments.Auth.CreateProjectFragment;
import com.example.appgestionvoluntariado.Fragments.Organization.OrgActivitiesFragment;
import com.example.appgestionvoluntariado.Fragments.Volunteer.VolunteerProfileHubFragment;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class OrganizationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization);

        BottomNavigationView bottomNav = findViewById(R.id.organization_bottom_navigation);
        MaterialToolbar toolbar = findViewById(R.id.topAppBarOrg);

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

        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_user) {
                replaceFragment(new VolunteerProfileHubFragment());
                return true;
            } else if (id == R.id.action_logout) {
                performLogout();
                return true;
            }
            return false;
        });

        // Pantalla de inicio por defecto
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.organization_fragment_container, new OrgActivitiesFragment())
                    .commit();
        }

    }

    private void performLogout() {
        FirebaseAuth.getInstance().signOut();
        SessionManager.getInstance(this).logout();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.organization_fragment_container, fragment)
                .commit();
    }


}