package com.example.appgestionvoluntariado.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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
            } else if (id == R.id.action_notifications) {
                openNotifications();
                return true;
            }
            return false;
        });

        // Setup Badge for Notifications
        android.view.MenuItem notificationItem = topAppBar.getMenu().findItem(R.id.action_notifications);
        if (notificationItem != null) {
            notificationItem.setActionView(R.layout.action_bar_notification_icon);
            View actionView = notificationItem.getActionView();
            
            // Re-bind click listener
            actionView.setOnClickListener(v -> openNotifications());
            
            android.widget.TextView badge = actionView.findViewById(R.id.tv_notification_badge);
            
            // Observe Database
            com.example.appgestionvoluntariado.Models.AppDatabase.getDatabase(this)
                .notificationDao().getUnreadCount().observe(this, count -> {
                    if (count == null || count == 0) {
                        badge.setVisibility(View.GONE);
                    } else {
                        badge.setVisibility(View.VISIBLE);
                        if (count > 99) {
                            badge.setText("99+");
                        } else {
                            badge.setText(String.valueOf(count));
                        }
                    }
                });
        }

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

        askNotificationPermission();
    }

    private final androidx.activity.result.ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new androidx.activity.result.contract.ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                } else {
                    // TODO: Inform user that that your app will not show notifications.
                }
            });

    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                    android.content.pm.PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
                 requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            }
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

    private void openNotifications() {
        Fragment fragment = new com.example.appgestionvoluntariado.Fragments.NotificationsFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.admin_fragment_container, fragment)
                .addToBackStack(null)
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