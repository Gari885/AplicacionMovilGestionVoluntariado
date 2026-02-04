package com.example.appgestionvoluntariado.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.appgestionvoluntariado.Fragments.Auth.CreateProjectFragment;
import com.example.appgestionvoluntariado.Fragments.Organization.OrgActivitiesFragment;

public class OrganizationPagerAdapter extends FragmentStateAdapter {

    public OrganizationPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new OrgActivitiesFragment();
            case 1:
                return new CreateProjectFragment();
            default:
                return new OrgActivitiesFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
