package com.example.appgestionvoluntariado.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.appgestionvoluntariado.Fragments.Volunteer.VolunteerExploreFragment;
import com.example.appgestionvoluntariado.Fragments.Volunteer.VolunteerMyProjectsFragment;

public class VolunteerPagerAdapter extends FragmentStateAdapter {

    public VolunteerPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new VolunteerExploreFragment();
            case 1:
                return new VolunteerMyProjectsFragment();
            default:
                return new VolunteerExploreFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
