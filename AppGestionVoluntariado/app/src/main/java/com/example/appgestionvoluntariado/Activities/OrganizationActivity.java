package com.example.appgestionvoluntariado.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.appgestionvoluntariado.Fragments.Auth.CreateProjectFragment;
import com.example.appgestionvoluntariado.Fragments.Organization.OrgActivitiesFragment;
import com.example.appgestionvoluntariado.Fragments.Organization.OrgProfileHubFragment;
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
                replaceFragment(new OrgProfileHubFragment());
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
        android.view.MenuItem notificationItem = toolbar.getMenu().findItem(R.id.action_notifications);
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

        // Pantalla de inicio por defecto

        // Pantalla de inicio por defecto
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.organization_fragment_container, new OrgActivitiesFragment())
                    .addToBackStack(null)
                    .commit();
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

    private void openNotifications() {
        Fragment fragment = new com.example.appgestionvoluntariado.Fragments.NotificationsFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.organization_fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }


}