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
    
    private boolean isTabNavigation = false;

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

        // ViewPager2 Setup
        androidx.viewpager2.widget.ViewPager2 viewPager = findViewById(R.id.adminViewPager);
        com.example.appgestionvoluntariado.Adapters.AdminPagerAdapter adapter = 
            new com.example.appgestionvoluntariado.Adapters.AdminPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // 2. Configurar Bottom Navigation (5 Secciones)
        BottomNavigationView bottomNav = findViewById(R.id.admin_bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            isTabNavigation = true;
            
            // Re-enable checking for normal tabs if it was disabled
            bottomNav.getMenu().setGroupCheckable(0, true, true);

            int id = item.getItemId();

            if (id == R.id.nav_admin_more) {
                showMoreOptionsBottomSheet();
                return false; 
            }

            // Tabs
            viewPager.setVisibility(View.VISIBLE);
            findViewById(R.id.admin_fragment_container).setVisibility(View.GONE);
            // Clear back stack
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                 getSupportFragmentManager().popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }

            if (id == R.id.nav_admin_dashboard) {
                viewPager.setCurrentItem(0, true);
            } else if (id == R.id.nav_admin_projects) {
                viewPager.setCurrentItem(1, true);
            } else if (id == R.id.nav_admin_vols) {
                viewPager.setCurrentItem(2, true);
            } else if (id == R.id.nav_admin_orgs) {
                viewPager.setCurrentItem(3, true);
            }
            return true;
        });

        viewPager.registerOnPageChangeCallback(new androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0: bottomNav.setSelectedItemId(R.id.nav_admin_dashboard); break;
                    case 1: bottomNav.setSelectedItemId(R.id.nav_admin_projects); break;
                    case 2: bottomNav.setSelectedItemId(R.id.nav_admin_vols); break;
                    case 3: bottomNav.setSelectedItemId(R.id.nav_admin_orgs); break;
                }
            }
        });

        // Pantalla inicial por defecto: Dashboard (managed by ViewPager default item 0)
        // Ensure ViewPager is visible
        viewPager.setVisibility(View.VISIBLE);
        findViewById(R.id.admin_fragment_container).setVisibility(View.GONE);

        askNotificationPermission();
        
        // Monitor back stack changes to sync UI state
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            
            // Ignore if triggered by tab navigation to prevent race conditions
            if (isTabNavigation) {
                isTabNavigation = false;
                return;
            }

            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                viewPager.setVisibility(View.VISIBLE);
                findViewById(R.id.admin_fragment_container).setVisibility(View.GONE);
                
                // Restore chekable behavior
                bottomNav.getMenu().setGroupCheckable(0, true, true);

                // Restore bottom nav selection based on ViewPager
                int item = viewPager.getCurrentItem();
                switch (item) {
                    case 0: bottomNav.setSelectedItemId(R.id.nav_admin_dashboard); break;
                    case 1: bottomNav.setSelectedItemId(R.id.nav_admin_projects); break;
                    case 2: bottomNav.setSelectedItemId(R.id.nav_admin_vols); break;
                    case 3: bottomNav.setSelectedItemId(R.id.nav_admin_orgs); break;
                }
            } else {
                viewPager.setVisibility(View.GONE);
                findViewById(R.id.admin_fragment_container).setVisibility(View.VISIBLE);

                // Uncheck bottom navigation items when a sub-fragment is visible
                bottomNav.getMenu().setGroupCheckable(0, false, true);
                for (int i = 0; i < bottomNav.getMenu().size(); i++) {
                    bottomNav.getMenu().getItem(i).setChecked(false);
                }
                bottomNav.getMenu().setGroupCheckable(0, true, true);
            }
        });
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
        BottomSheetDialog dialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View view = getLayoutInflater().inflate(R.layout.layout_admin_more_options, null);

        // Matches Option
        view.findViewById(R.id.optionMatches).setOnClickListener(v -> {
            replaceFragment(new AdminMatchesListFragment());
            dialog.dismiss();
        });

        // New ODS & Skills Management Option
        view.findViewById(R.id.optionCategories).setOnClickListener(v -> {
            replaceFragment(new AdminCategoriesAddFragment());
            
            dialog.dismiss();
        });

        dialog.setContentView(view);
        dialog.show();
    }

    public void replaceFragment(Fragment fragment) {
        // Toggle visibility
        findViewById(R.id.adminViewPager).setVisibility(View.GONE);
        findViewById(R.id.admin_fragment_container).setVisibility(View.VISIBLE);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.admin_fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void openNotifications() {
        replaceFragment(new com.example.appgestionvoluntariado.Fragments.NotificationsFragment());
    }

    private void performLogout() {
        FirebaseAuth.getInstance().signOut();
        com.example.appgestionvoluntariado.Utils.TokenManager.getInstance(this).clearToken();
        SessionManager.getInstance(this).logout();
        android.content.Intent intent = new android.content.Intent(this, MainActivity.class);
        intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}