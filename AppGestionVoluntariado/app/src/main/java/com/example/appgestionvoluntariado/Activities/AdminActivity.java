package com.example.appgestionvoluntariado.Activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.appgestionvoluntariado.Fragments.Admin.AdminCategoriesAddFragment;
import com.example.appgestionvoluntariado.Fragments.Admin.AdminDashboardFragment;
import com.example.appgestionvoluntariado.Fragments.Admin.AdminMatchesListFragment;
import com.example.appgestionvoluntariado.Fragments.Admin.AdminOrganizationListFragment;
import com.example.appgestionvoluntariado.Fragments.Admin.AdminProfileHubFragment;
import com.example.appgestionvoluntariado.Fragments.Admin.AdminProjectListFragment;
import com.example.appgestionvoluntariado.Fragments.Admin.AdminVolunteerListFragment;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        // 1. Configurar Toolbar y Perfil (Icono arriba a la derecha)
        MaterialToolbar topAppBar = findViewById(R.id.topAppBarAdmin);
        topAppBar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_user) {
                replaceFragment(new AdminProfileHubFragment());
                return true;
            } else if (id == R.id.action_logout) {
                performLogout();
                return true;
            }
            return false;
        });

        // 2. Configurar Bottom Navigation (5 Secciones)
        BottomNavigationView bottomNav = findViewById(R.id.admin_bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_admin_more) {
                showMoreOptionsBottomSheet();
                return false;
            }

            if (id == R.id.nav_admin_dashboard) {
                selectedFragment = new AdminDashboardFragment();
            } else if (id == R.id.nav_admin_vols) {
                selectedFragment = new AdminVolunteerListFragment();
            } else if (id == R.id.nav_admin_projects) {
                selectedFragment = new AdminProjectListFragment();
            } else if (id == R.id.nav_admin_orgs) {
                selectedFragment = new AdminOrganizationListFragment();
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

    private void showMoreOptionsBottomSheet() {
        // We can reuse our pretty Bottom Sheet logic [cite: 2026-01-16]
        BottomSheetDialog dialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View view = getLayoutInflater().inflate(R.layout.layout_admin_more_options, null);

        // Matches Option
        view.findViewById(R.id.optionMatches).setOnClickListener(v -> {
            replaceFragment(new AdminMatchesListFragment());
            dialog.dismiss();
        });

        // New ODS & Skills Management Option [cite: 2026-01-16]
        view.findViewById(R.id.optionCategories).setOnClickListener(v -> {
            replaceFragment(new AdminCategoriesAddFragment());
            dialog.dismiss();
        });

        dialog.setContentView(view);
        dialog.show();
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.admin_fragment_container, fragment)
                .commit();
    }

    private void performLogout() {
        FirebaseAuth.getInstance().signOut();
        SessionManager.getInstance(this).logout();
        android.content.Intent intent = new android.content.Intent(this, MainActivity.class);
        intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}