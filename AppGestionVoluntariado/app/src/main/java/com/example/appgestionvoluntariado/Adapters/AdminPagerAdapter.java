package com.example.appgestionvoluntariado.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.appgestionvoluntariado.Fragments.Admin.AdminDashboardFragment;
import com.example.appgestionvoluntariado.Fragments.Admin.AdminOrganizationListFragment;
import com.example.appgestionvoluntariado.Fragments.Admin.AdminProjectListFragment;
import com.example.appgestionvoluntariado.Fragments.Admin.AdminVolunteerListFragment;

public class AdminPagerAdapter extends FragmentStateAdapter {

    public AdminPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AdminDashboardFragment();
            case 1:
                return new AdminProjectListFragment();
            case 2:
                return new AdminVolunteerListFragment();
            case 3:
                return new AdminOrganizationListFragment();
            default:
                return new AdminDashboardFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
