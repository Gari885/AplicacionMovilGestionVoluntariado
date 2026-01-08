package com.example.appgestionvoluntariado.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appgestionvoluntariado.Fragments.Settings.ProfileUserFragment;
import com.example.appgestionvoluntariado.Fragments.Organization.OrganizationMyProjectsFragment;
import com.example.appgestionvoluntariado.R;

public class OrganizationActivity extends AppCompatActivity {

    private ImageView userSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_organization_main);

        userSettings = findViewById(R.id.ivUserProfile);
        userSettings.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new ProfileUserFragment())
                    .addToBackStack(null) // Added backstack to allow return logic if needed
                    .commit();
        });

        if (savedInstanceState == null) {
            // Default fragment for Organization?? The original code had nothing.
            // But usually we want to show their projects.
            // I'll add OrganizationMyProjectsFragment as default to avoid empty screen.
             getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new OrganizationMyProjectsFragment())
                    .commit();
        }
    }
}
